package com.example.fordfocusdpfscan.car

import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.fordfocusdpfscan.R
import com.example.fordfocusdpfscan.data.DpfData
import com.example.fordfocusdpfscan.data.DpfRepository
import com.example.fordfocusdpfscan.data.RegenStatus
import com.example.fordfocusdpfscan.data.RegenStrategy
import com.example.fordfocusdpfscan.service.DpfForegroundService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

// ═══════════════════════════════════════════════════════════════════════════════
// DpfScreen.kt — Android Auto main dashboard screen.
//
// ListTemplate — 4 righe:
//   ┌─────────────────────────────────────────────────────┐
//   │ Filtro DPF                                          │
//   │ Soot X%  ·  Load X%  ·  ΔP X kPa                  │
//   ├─────────────────────────────────────────────────────┤
//   │ Rigenerazione                                       │
//   │ Inattiva — puoi spegnere  ·  Flag ECU              │
//   ├─────────────────────────────────────────────────────┤
//   │ Temperature                                         │
//   │ EGT X°C  ·  Refrig. X°C                           │
//   ├─────────────────────────────────────────────────────┤
//   │ Motore                                              │
//   │ Carico X%  ·  Boost X kPa  ·  Regen X km          │
//   └─────────────────────────────────────────────────────┘
//
// ActionStrip: [Ricollega] — riconnette il dongle OBD2
// CarToast: su ogni transizione di stato (WARNING / ACTIVE / COMPLETED)
// ═══════════════════════════════════════════════════════════════════════════════

class DpfScreen(carContext: CarContext) : Screen(carContext) {

    private val TAG = "DpfScreen"

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var previousRegenStatus: RegenStatus = RegenStatus.INACTIVE
    private var currentData: DpfData = DpfData()

    /** Throttle invalidate() — Car App Library template API ha un rate limit ~1/s */
    private var lastInvalidateTime = 0L
    private val INVALIDATE_INTERVAL_MS = 1_000L

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
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
            override fun onDestroy(owner: LifecycleOwner) { scope.cancel() }
        })
    }

    private fun safeInvalidate() {
        val now = System.currentTimeMillis()
        if (now - lastInvalidateTime < INVALIDATE_INTERVAL_MS) return
        lastInvalidateTime = now
        try {
            invalidate()
        } catch (e: IllegalStateException) {
            Log.w(TAG, "invalidate() outside car lifecycle — ignored: ${e.message}")
        }
    }

    private fun safeShowCarToast(newStatus: RegenStatus) {
        try { showCarToast(newStatus) } catch (e: IllegalStateException) {
            Log.w(TAG, "CarToast outside car lifecycle — ignored: ${e.message}")
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Template — ListTemplate con 4 righe
    // ═════════════════════════════════════════════════════════════════════════

    override fun onGetTemplate(): Template {
        val data = currentData
        return ListTemplate.Builder()
            .setTitle(carContext.getString(R.string.car_screen_title))
            .setHeaderAction(Action.APP_ICON)
            .setActionStrip(buildActionStrip())
            .setSingleList(
                ItemList.Builder()
                    .addItem(buildDpfRow(data))
                    .addItem(buildRegenRow(data))
                    .addItem(buildTempsRow(data))
                    .addItem(buildMotoreRow(data))
                    .build()
            )
            .build()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Riga 1 — FILTRO DPF: Soot · Load · ΔP
    // ═════════════════════════════════════════════════════════════════════════

    private fun buildDpfRow(data: DpfData): Row {
        val soot   = if (data.sootPercentage >= 0)       "${data.sootPercentage.toInt()}%"              else "– –"
        val load   = if (data.loadPercentage >= 0)       "${data.loadPercentage.toInt()}%"              else "– –"
        val deltaP = if (data.dpfDeltaPressureKpa >= 0)  "${"%.1f".format(data.dpfDeltaPressureKpa)} kPa" else "– –"

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

        return Row.Builder()
            .setTitle("Filtro DPF")
            .addText(coloredSpan("Soot $soot  ·  Load $load  ·  ΔP $deltaP", color))
            .build()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Riga 2 — STATUS RIGENERAZIONE
    // ═════════════════════════════════════════════════════════════════════════

    private fun buildRegenRow(data: DpfData): Row {
        val (statusStr, statusColor) = when (data.regenStatus) {
            RegenStatus.INACTIVE  -> "Inattiva — puoi spegnere"  to CarColor.GREEN
            RegenStatus.WARNING   -> "Attenzione — non spegnere" to CarColor.YELLOW
            RegenStatus.ACTIVE    -> "ATTIVA — non spegnere!"    to CarColor.RED
            RegenStatus.COMPLETED -> "Completata ✓"              to CarColor.GREEN
        }

        val source = when (data.regenStrategy) {
            RegenStrategy.DIRECT_FLAG  -> "  ·  Flag ECU"
            RegenStrategy.EGT_FALLBACK -> "  ·  Temp EGT"
            RegenStrategy.NONE         -> ""
        }

        return Row.Builder()
            .setTitle("Rigenerazione")
            .addText(coloredSpan("$statusStr$source", statusColor))
            .build()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Riga 3 — TEMPERATURE: EGT · Refrigerante
    // ═════════════════════════════════════════════════════════════════════════

    private fun buildTempsRow(data: DpfData): Row {
        val egtStr     = if (data.egtCelsius >= 0)    "${data.egtCelsius.toInt()} °C"    else "– –"
        val coolantStr = if (data.coolantTempC >= 0)  "${data.coolantTempC.toInt()} °C"  else "– –"

        val egtColor = when {
            data.egtCelsius < 0    -> CarColor.DEFAULT
            data.egtCelsius >= 550 -> CarColor.RED
            data.egtCelsius >= 450 -> CarColor.YELLOW
            else                   -> CarColor.GREEN
        }

        return Row.Builder()
            .setTitle("Temperature")
            .addText(coloredSpan("EGT $egtStr  ·  Refrig. $coolantStr", egtColor))
            .build()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Riga 4 — MOTORE: Carico · Boost · km dall'ultima regen
    //
    // Boost = MAP (intakeMapKpa) − baro (baroKpa).
    // Se baroKpa non è disponibile, mostra MAP assoluto come approssimazione.
    // ═════════════════════════════════════════════════════════════════════════

    private fun buildMotoreRow(data: DpfData): Row {
        val carico = if (data.engineLoadPct >= 0) "${data.engineLoadPct.toInt()}%" else "– –"

        val boostStr = when {
            data.intakeMapKpa >= 0 && data.baroKpa >= 0 ->
                "${"%.2f".format((data.intakeMapKpa - data.baroKpa) / 100f)} bar"
            data.intakeMapKpa >= 0 ->
                "${"%.2f".format(data.intakeMapKpa / 100f)} bar"
            else -> "– –"
        }

        val regenKmStr = if (data.kmSinceLastRegen > 0) "${"%,d".format(data.kmSinceLastRegen)} km" else "– –"

        return Row.Builder()
            .setTitle("Motore")
            .addText("Carico $carico  ·  Boost $boostStr  ·  Regen $regenKmStr")
            .build()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ActionStrip — [Ricollega] in alto a destra
    // ═════════════════════════════════════════════════════════════════════════

    private fun buildActionStrip(): ActionStrip {
        val reconnectAction = Action.Builder()
            .setTitle(carContext.getString(R.string.car_action_reconnect))
            .setOnClickListener {
                carContext.startForegroundService(
                    Intent(carContext, DpfForegroundService::class.java).apply {
                        action = DpfForegroundService.ACTION_RECONNECT
                    }
                )
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
            RegenStatus.INACTIVE  -> return
        }
        CarToast.makeText(carContext, message, CarToast.LENGTH_LONG).show()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Helper — SpannableString con CarColor span
    // ═════════════════════════════════════════════════════════════════════════

    private fun coloredSpan(text: String, color: CarColor): SpannableString =
        SpannableString(text).apply {
            setSpan(
                ForegroundCarColorSpan.create(color),
                0, text.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
}
