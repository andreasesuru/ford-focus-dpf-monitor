package com.example.fordfocusdpfscan.car

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
import com.example.fordfocusdpfscan.service.DpfForegroundService
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

// ═══════════════════════════════════════════════════════════════════════════════
// DpfScreen.kt — Android Auto main dashboard screen.
//
// Renders a PaneTemplate with 4 data rows:
//   Row 1 — DPF Load %              (colored by threshold)
//   Row 2 — Regeneration Status     (colored by status)
//   Row 3 — Temperatures            (Coolant + EGT, colored)
//   Row 4 — History                 (Last regen km + last service km)
//
// ActionStrip:
//   [🔄 Reconnect] — top-right corner action, triggers BLE reconnect
//
// Update mechanism:
//   A coroutine collects DpfRepository.dpfData StateFlow.
//   On every emission, invalidate() is called so Android Auto re-calls
//   onGetTemplate() with the latest data.
//
// CarToast:
//   Fired for every status transition (WARNING, ACTIVE, COMPLETED, BLE lost).
//   Shown simultaneously with the system notification heads-up.
// ═══════════════════════════════════════════════════════════════════════════════

class DpfScreen(carContext: CarContext) : Screen(carContext) {

    // Coroutine scope tied to the screen's lifecycle
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Track the previous regen status to detect transitions and show CarToast
    private var previousRegenStatus: RegenStatus = RegenStatus.INACTIVE

    // Snapshot of the latest data — updated from the StateFlow collector
    private var currentData: DpfData = DpfData()

    init {
        // ── Observe DpfRepository and call invalidate() on every update ────────
        scope.launch {
            DpfRepository.dpfData.collectLatest { data ->
                // Detect status transitions to show CarToast in-car
                if (data.regenStatus != previousRegenStatus) {
                    showCarToast(previousRegenStatus, data.regenStatus)
                    previousRegenStatus = data.regenStatus
                }
                currentData = data
                invalidate()  // tells Android Auto to call onGetTemplate() again
            }
        }

        // Clean up scope when the screen is destroyed
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                scope.cancel()
            }
        })
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Template — called by Android Auto on every invalidate()
    // ═════════════════════════════════════════════════════════════════════════

    override fun onGetTemplate(): Template {
        val data = currentData

        val pane = Pane.Builder()
            .addRow(buildLoadRow(data))
            .addRow(buildRegenRow(data))
            .addRow(buildTemperaturesRow(data))
            .addRow(buildHistoryRow(data))
            .build()

        return PaneTemplate.Builder(pane)
            .setTitle(carContext.getString(R.string.car_screen_title))
            .setHeaderAction(Action.APP_ICON)
            .setActionStrip(buildActionStrip())
            .build()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Row builders
    // ═════════════════════════════════════════════════════════════════════════

    /** Row 1 — DPF Load percentage with color-coded value. */
    private fun buildLoadRow(data: DpfData): Row {
        val loadText: SpannableString = if (data.loadPercentage >= 0) {
            val pct = data.loadPercentage.toInt()
            val color = when {
                pct < 60  -> CarColor.GREEN
                pct < 80  -> CarColor.YELLOW
                else      -> CarColor.RED
            }
            coloredSpan("$pct%", color)
        } else {
            SpannableString("– –")
        }

        val sootText = if (data.sootPercentage >= 0) {
            "Soot: ${data.sootPercentage.toInt()}%"
        } else {
            "Soot: – –"
        }

        return Row.Builder()
            .setTitle(carContext.getString(R.string.car_row_load))
            .addText(loadText)
            .addText(sootText)
            .build()
    }

    /** Row 2 — Regeneration status with color and safety message. */
    private fun buildRegenRow(data: DpfData): Row {
        val (statusText, statusColor) = when (data.regenStatus) {
            RegenStatus.INACTIVE  -> Pair(
                carContext.getString(R.string.regen_inactive), CarColor.GREEN
            )
            RegenStatus.WARNING   -> Pair(
                carContext.getString(R.string.regen_warning), CarColor.YELLOW
            )
            RegenStatus.ACTIVE    -> Pair(
                carContext.getString(R.string.regen_active), CarColor.RED
            )
            RegenStatus.COMPLETED -> Pair(
                carContext.getString(R.string.regen_completed), CarColor.GREEN
            )
        }

        val strategyText = when (data.regenStrategy) {
            com.example.fordfocusdpfscan.data.RegenStrategy.DIRECT_FLAG  -> "Source: ECU flag"
            com.example.fordfocusdpfscan.data.RegenStrategy.EGT_FALLBACK -> "Source: EGT temp"
            com.example.fordfocusdpfscan.data.RegenStrategy.NONE         -> ""
        }

        return Row.Builder()
            .setTitle(carContext.getString(R.string.car_row_regen))
            .addText(coloredSpan(statusText, statusColor))
            .apply { if (strategyText.isNotEmpty()) addText(strategyText) }
            .build()
    }

    /** Row 3 — Coolant temperature + EGT side by side. */
    private fun buildTemperaturesRow(data: DpfData): Row {
        val coolantStr: SpannableString = if (data.coolantTempC >= 0) {
            val color = if (data.coolantTempC > 100f) CarColor.RED else CarColor.GREEN
            coloredSpan("Coolant: ${data.coolantTempC.toInt()}°C", color)
        } else {
            SpannableString("Coolant: – –")
        }

        val egtStr: SpannableString = if (data.egtCelsius >= 0) {
            val color = when {
                data.egtCelsius >= 550f -> CarColor.RED
                data.egtCelsius >= 450f -> CarColor.YELLOW
                else                    -> CarColor.DEFAULT
            }
            coloredSpan("EGT: ${data.egtCelsius.toInt()}°C", color)
        } else {
            SpannableString("EGT: – –")
        }

        return Row.Builder()
            .setTitle(carContext.getString(R.string.car_row_temps))
            .addText(coolantStr)
            .addText(egtStr)
            .build()
    }

    /** Row 4 — Last regen km + last service km. */
    private fun buildHistoryRow(data: DpfData): Row {
        val regenKmStr = if (data.kmSinceLastRegen > 0L)
            "Last regen: ${"%,d".format(data.kmSinceLastRegen)} km"
        else
            "Last regen: – –"

        val serviceKmStr = if (data.kmSinceOilChange > 0L)
            "Last service: ${"%,d".format(data.kmSinceOilChange)} km"
        else
            "Last service: not set"

        return Row.Builder()
            .setTitle(carContext.getString(R.string.car_row_history))
            .addText(regenKmStr)
            .addText(serviceKmStr)
            .build()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ActionStrip — Reconnect button in the top-right corner
    // ═════════════════════════════════════════════════════════════════════════

    private fun buildActionStrip(): ActionStrip {
        val reconnectAction = Action.Builder()
            .setTitle(carContext.getString(R.string.car_action_reconnect))
            .setBackgroundColor(CarColor.BLUE)
            .setOnClickListener {
                // Send reconnect intent to the foreground service
                val intent = Intent(carContext, DpfForegroundService::class.java).apply {
                    action = DpfForegroundService.ACTION_RECONNECT
                }
                carContext.startForegroundService(intent)

                // Show immediate feedback in-car
                CarToast.makeText(
                    carContext,
                    "Reconnecting to Android-Vlink…",
                    CarToast.LENGTH_SHORT
                ).show()
            }
            .build()

        return ActionStrip.Builder()
            .addAction(reconnectAction)
            .build()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // CarToast — in-car overlay messages for status transitions
    // ═════════════════════════════════════════════════════════════════════════

    private fun showCarToast(old: RegenStatus, new: RegenStatus) {
        val message = when (new) {
            RegenStatus.WARNING   -> carContext.getString(R.string.toast_warning)
            RegenStatus.ACTIVE    -> carContext.getString(R.string.toast_active)
            RegenStatus.COMPLETED -> carContext.getString(R.string.toast_complete)
            RegenStatus.INACTIVE  -> return  // No toast when going back to inactive
        }

        CarToast.makeText(carContext, message, CarToast.LENGTH_LONG).show()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Helper — builds a SpannableString with a CarColor span
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
