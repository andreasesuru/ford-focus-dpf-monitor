package com.example.fordfocusdpfscan.car

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.*
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.fordfocusdpfscan.R
import com.example.fordfocusdpfscan.data.DpfData
import com.example.fordfocusdpfscan.data.DpfRepository
import com.example.fordfocusdpfscan.data.RegenStatus
import com.example.fordfocusdpfscan.data.RegenStrategy
import com.example.fordfocusdpfscan.service.DpfForegroundService
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

// ═══════════════════════════════════════════════════════════════════════════════
// DpfScreen.kt — Android Auto main dashboard screen.
//
// PaneTemplate — 4 rows:
//   Row 1 — FILTRO DPF   → Soot % · Load %  /  Delta P kPa
//   Row 2 — RIGENERAZIONE → Status (colored) / fonte (ECU flag o EGT)
//   Row 3 — TEMPERATURE  → EGT °C (colored) / Refrig. °C (colored)
//   Row 4 — DISTANZE     → km da regen / km da tagliando
//
// ActionStrip: [Ricollega] — riconnette il dongle OBD2
//
// Update: coroutine su DpfRepository.dpfData StateFlow → invalidate()
// CarToast: su ogni transizione di stato (WARNING / ACTIVE / COMPLETED)
// ═══════════════════════════════════════════════════════════════════════════════

class DpfScreen(carContext: CarContext) : Screen(carContext) {

    private val TAG = "DpfScreen"

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var previousRegenStatus: RegenStatus = RegenStatus.INACTIVE
    private var currentData: DpfData = DpfData()

    /** Throttle invalidate() — the Car App Library template API has its own
     *  rate limit (~1/s) and calling it faster floods the host renderer,
     *  causing visible lag on the car display and on the phone. */
    private var lastInvalidateTime = 0L
    private val INVALIDATE_INTERVAL_MS = 1_000L

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                // Start collecting only when the screen is visible on the car display.
                // This prevents OutOfCarLifecycle exceptions caused by invalidate() /
                // CarToast calls before the session is fully active.
                scope.launch {
                    DpfRepository.dpfData.collectLatest { data ->
                        if (data.regenStatus != previousRegenStatus) {
                            safeShowCarToast(data.regenStatus)
                            previousRegenStatus = data.regenStatus
                        }
                        currentData = data
                        safeInvalidate()
                    }
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                scope.cancel()
            }
        })
    }

    // Safe wrappers — guard against OutOfCarLifecycle if the session ends
    // while a coroutine emission is in-flight.
    private fun safeInvalidate() {
        val now = System.currentTimeMillis()
        if (now - lastInvalidateTime < INVALIDATE_INTERVAL_MS) return
        lastInvalidateTime = now
        try {
            invalidate()
        } catch (e: IllegalStateException) {
            Log.w(TAG, "invalidate() called outside car lifecycle — ignored: ${e.message}")
        }
    }

    private fun safeShowCarToast(newStatus: RegenStatus) {
        try {
            showCarToast(newStatus)
        } catch (e: IllegalStateException) {
            Log.w(TAG, "CarToast called outside car lifecycle — ignored: ${e.message}")
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Template — chiamato da Android Auto ad ogni invalidate()
    // ═════════════════════════════════════════════════════════════════════════

    override fun onGetTemplate(): Template {
        val data = currentData

        val pane = Pane.Builder()
            .addRow(buildDpfRow(data))
            .addRow(buildRegenRow(data))
            .addRow(buildTempsRow(data))
            .addRow(buildDistancesRow(data))
            .build()

        return PaneTemplate.Builder(pane)
            .setTitle(carContext.getString(R.string.car_screen_title))
            .setHeaderAction(Action.APP_ICON)
            .setActionStrip(buildActionStrip())
            .build()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Row 1 — FILTRO DPF: Soot % · Load %  |  Delta P kPa
    // ═════════════════════════════════════════════════════════════════════════

    private fun buildDpfRow(data: DpfData): Row {
        // Soot + Load sulla stessa riga di testo
        val sootLoad: SpannableString = run {
            val soot = if (data.sootPercentage >= 0) "${data.sootPercentage.toInt()}%" else "– –"
            val load = if (data.loadPercentage >= 0) "${data.loadPercentage.toInt()}%" else "– –"
            // Colora in base al valore peggiore tra soot e load
            val worst = maxOf(
                data.sootPercentage.takeIf { it >= 0f } ?: 0f,
                data.loadPercentage.takeIf { it >= 0f } ?: 0f
            )
            val color = when {
                data.sootPercentage < 0 && data.loadPercentage < 0 -> CarColor.DEFAULT
                worst >= 80f -> CarColor.RED
                worst >= 60f -> CarColor.YELLOW
                else         -> CarColor.GREEN
            }
            coloredSpan(
                "${carContext.getString(R.string.car_label_soot)}: $soot  ·  " +
                "${carContext.getString(R.string.car_label_load)}: $load",
                color
            )
        }

        // Delta P — colore: verde < 5, giallo 5-18, rosso > 18 kPa
        val deltaP: SpannableString = if (data.dpfDeltaPressureKpa >= 0) {
            val kpa = data.dpfDeltaPressureKpa
            val color = when {
                kpa > 18f -> CarColor.RED
                kpa >= 5f -> CarColor.YELLOW
                else      -> CarColor.GREEN
            }
            coloredSpan(
                "${carContext.getString(R.string.car_label_delta_p)}: ${"%.1f".format(kpa)} kPa",
                color
            )
        } else {
            SpannableString("${carContext.getString(R.string.car_label_delta_p)}: ${carContext.getString(R.string.car_no_data)}")
        }

        return Row.Builder()
            .setTitle(carContext.getString(R.string.car_row_dpf))
            .addText(sootLoad)
            .addText(deltaP)
            .build()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Row 2 — RIGENERAZIONE: status colorato + fonte (ECU / EGT)
    // ═════════════════════════════════════════════════════════════════════════

    private fun buildRegenRow(data: DpfData): Row {
        val (statusStr, statusColor) = when (data.regenStatus) {
            RegenStatus.INACTIVE  -> carContext.getString(R.string.car_regen_inactive)  to CarColor.GREEN
            RegenStatus.WARNING   -> carContext.getString(R.string.car_regen_warning)   to CarColor.YELLOW
            RegenStatus.ACTIVE    -> carContext.getString(R.string.car_regen_active)    to CarColor.RED
            RegenStatus.COMPLETED -> carContext.getString(R.string.car_regen_completed) to CarColor.GREEN
        }

        val sourceStr = when (data.regenStrategy) {
            RegenStrategy.DIRECT_FLAG  -> carContext.getString(R.string.car_label_source_ecu)
            RegenStrategy.EGT_FALLBACK -> carContext.getString(R.string.car_label_source_egt)
            RegenStrategy.NONE         -> ""
        }

        return Row.Builder()
            .setTitle(carContext.getString(R.string.car_row_regen))
            .addText(coloredSpan(statusStr, statusColor))
            .apply { if (sourceStr.isNotEmpty()) addText(sourceStr) }
            .build()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Row 3 — TEMPERATURE: EGT (prima, critica) + Refrigerante
    // ═════════════════════════════════════════════════════════════════════════

    private fun buildTempsRow(data: DpfData): Row {
        // EGT — soglie: verde < 450, giallo 450-549, rosso ≥ 550
        val egtStr: SpannableString = if (data.egtCelsius >= 0) {
            val color = when {
                data.egtCelsius >= 550f -> CarColor.RED
                data.egtCelsius >= 450f -> CarColor.YELLOW
                else                    -> CarColor.GREEN
            }
            coloredSpan(
                "${carContext.getString(R.string.car_label_egt)}: ${data.egtCelsius.toInt()} °C",
                color
            )
        } else {
            SpannableString("${carContext.getString(R.string.car_label_egt)}: ${carContext.getString(R.string.car_no_data)}")
        }

        // Refrigerante — verde < 100, rosso ≥ 100
        val coolantStr: SpannableString = if (data.coolantTempC >= 0) {
            val color = if (data.coolantTempC >= 100f) CarColor.RED else CarColor.GREEN
            coloredSpan(
                "${carContext.getString(R.string.car_label_coolant)}: ${data.coolantTempC.toInt()} °C",
                color
            )
        } else {
            SpannableString("${carContext.getString(R.string.car_label_coolant)}: ${carContext.getString(R.string.car_no_data)}")
        }

        return Row.Builder()
            .setTitle(carContext.getString(R.string.car_row_temps))
            .addText(egtStr)
            .addText(coolantStr)
            .build()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Row 4 — DISTANZE: km da ultima regen + km da ultimo tagliando
    // ═════════════════════════════════════════════════════════════════════════

    private fun buildDistancesRow(data: DpfData): Row {
        val regenStr = if (data.kmSinceLastRegen > 0L)
            "${carContext.getString(R.string.car_label_km_regen)}: ${"%,d".format(data.kmSinceLastRegen)} km"
        else
            "${carContext.getString(R.string.car_label_km_regen)}: ${carContext.getString(R.string.car_no_data)}"

        // Colora il tagliando: verde < 9000, giallo 9000-11000, rosso ≥ 11000
        val oilStr: SpannableString = if (data.kmSinceOilChange > 0L) {
            val km = data.kmSinceOilChange
            val color = when {
                km >= 11_000L -> CarColor.RED
                km >= 9_000L  -> CarColor.YELLOW
                else          -> CarColor.DEFAULT
            }
            coloredSpan(
                "${carContext.getString(R.string.car_label_km_oil)}: ${"%,d".format(km)} km",
                color
            )
        } else {
            SpannableString("${carContext.getString(R.string.car_label_km_oil)}: ${carContext.getString(R.string.car_no_data)}")
        }

        return Row.Builder()
            .setTitle(carContext.getString(R.string.car_row_distances))
            .addText(regenStr)
            .addText(oilStr)
            .build()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ActionStrip — [Ricollega] in alto a destra
    // ═════════════════════════════════════════════════════════════════════════

    private fun buildActionStrip(): ActionStrip {
        val reconnectAction = Action.Builder()
            .setTitle(carContext.getString(R.string.car_action_reconnect))
            .setBackgroundColor(CarColor.BLUE)
            .setOnClickListener {
                val intent = Intent(carContext, DpfForegroundService::class.java).apply {
                    action = DpfForegroundService.ACTION_RECONNECT
                }
                carContext.startForegroundService(intent)

                CarToast.makeText(
                    carContext,
                    "Riconnessione a Android-Vlink…",
                    CarToast.LENGTH_SHORT
                ).show()
            }
            .build()

        return ActionStrip.Builder()
            .addAction(reconnectAction)
            .build()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // CarToast — messaggio sovrapposto in auto su ogni transizione di stato
    // ═════════════════════════════════════════════════════════════════════════

    private fun showCarToast(newStatus: RegenStatus) {
        val message = when (newStatus) {
            RegenStatus.WARNING   -> carContext.getString(R.string.car_toast_warning)
            RegenStatus.ACTIVE    -> carContext.getString(R.string.car_toast_active)
            RegenStatus.COMPLETED -> carContext.getString(R.string.car_toast_complete)
            RegenStatus.INACTIVE  -> return  // nessun toast per il ritorno a inattivo
        }
        CarToast.makeText(carContext, message, CarToast.LENGTH_LONG).show()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Helper — SpannableString con CarColor span
    // ═════════════════════════════════════════════════════════════════════════

    private fun coloredSpan(text: String, color: CarColor): SpannableString {
        return SpannableString(text).apply {
            setSpan(
                ForegroundCarColorSpan.create(color),
                0, text.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
    }
}
