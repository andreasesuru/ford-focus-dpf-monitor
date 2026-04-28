package com.example.fordfocusdpfscan.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ═══════════════════════════════════════════════════════════════════════════════
// DpfRepository.kt — Singleton data store and regen detection engine.
//
// Responsibilities:
//   1. Hold the single source of truth (StateFlow<DpfData>) consumed by both
//      the phone UI (MainActivity) and the Android Auto screen (DpfScreen).
//   2. Apply regen detection logic (Strategy A + Strategy B) on every update.
//   3. Persist history data (last regen km, last service km) in SharedPreferences.
//   4. Expose callback lambdas so NotificationHelper can react to state changes.
// ═══════════════════════════════════════════════════════════════════════════════

object DpfRepository {

    // ── SharedPreferences (kept for future use — currently unused) ───────────
    private const val PREFS_NAME = "focus_prefs"

    // ── EGT thresholds (°C) ───────────────────────────────────────────────────
    /** Below this → INACTIVE (safe to stop). */
    const val EGT_SAFE_THRESHOLD: Float       = 400f
    /** Above this → WARNING (do not stop, could be passive regen). */
    const val EGT_WARNING_THRESHOLD: Float    = 450f
    /** Above this → ACTIVE (confirmed active regen, ECU post-injection likely). */
    const val EGT_ACTIVE_THRESHOLD: Float     = 550f
    /** Consecutive samples above [EGT_ACTIVE_THRESHOLD] required before
     *  declaring ACTIVE (20 s ÷ 3 s poll = ~7 samples). */
    private const val EGT_ACTIVE_CONFIRM_COUNT = 7

    // ── Internal mutable state ────────────────────────────────────────────────
    private val _dpfData = MutableStateFlow(DpfData())
    val dpfData: StateFlow<DpfData> = _dpfData.asStateFlow()

    /** Running count of consecutive samples above [EGT_ACTIVE_THRESHOLD]. */
    private var egtActiveCounter = 0

    /** SharedPreferences instance — initialised once via [init]. */
    private lateinit var prefs: SharedPreferences

    // ── Turbo cooldown state ──────────────────────────────────────────────────
    /** Duration of the post-trip cooldown in seconds. */
    private const val COOLDOWN_DURATION_S = 45

    /** EGT threshold above which we consider the engine "hot" (recent driving). */
    private const val COOLDOWN_HOT_EGT_THRESHOLD = 150f

    /** RPM range considered "engine at idle" (running but not being driven).
     *  Ford Focus 1.5 TDCi idles at ~750 RPM. */
    private val COOLDOWN_IDLE_RPM_RANGE = 500f..1100f

    /** Vehicle speed threshold below which we consider the car stopped (km/h). */
    private const val COOLDOWN_STOPPED_KMH = 3f

    /** True once EGT or load crossed the threshold — a trip was detected. */
    private var wasHot = false

    /** Running cooldown coroutine (null when inactive). */
    private var cooldownJob: Job? = null

    /** Dedicated scope for the cooldown timer. Survives across data updates. */
    private val cooldownScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // ── Notification callback ─────────────────────────────────────────────────
    /**
     * Lambda invoked by the repository whenever the [RegenStatus] changes.
     * Set by [DpfForegroundService] to trigger notifications without a hard
     * dependency on Android context from within BleManager.
     */
    var onRegenStatusChanged: ((oldStatus: RegenStatus, newStatus: RegenStatus) -> Unit)? = null

    /**
     * Lambda invoked on regen lifecycle events for history recording.
     * Events: "STARTED", "DATA_POINT", "COMPLETED", "INTERRUPTED"
     * Set by [DpfForegroundService] which owns [RegenHistoryRepository].
     */
    var onRegenSessionEvent: ((event: String, data: DpfData) -> Unit)? = null

    // ═════════════════════════════════════════════════════════════════════════
    // Initialisation
    // ═════════════════════════════════════════════════════════════════════════

    /** Must be called once from Application.onCreate(). */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Distance counters (kmSinceLastRegen, kmSinceOilChange) are now read live
        // from the ECU via PID 22 050B and 22 0542 — no local persistence needed.
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Public update methods — called by BleManager on every parsed response
    // ═════════════════════════════════════════════════════════════════════════

    fun updateSoot(percentage: Float) {
        _dpfData.value = _dpfData.value.copy(sootPercentage = percentage)
    }

    fun updateLoad(percentage: Float) {
        _dpfData.value = _dpfData.value.copy(loadPercentage = percentage)
    }

    fun updateCoolantTemp(celsius: Float) {
        _dpfData.value = _dpfData.value.copy(coolantTempC = celsius)
    }

    /**
     * Updates the EGT reading and immediately re-evaluates the regen status
     * using Strategy B (EGT fallback). Strategy A [updateRegenFlag] takes
     * priority if the PID becomes available.
     */
    fun updateEgt(celsius: Float) {
        val previous = _dpfData.value
        val newStatus = computeRegenStatusFromEgt(celsius, previous.regenStatus)
        val newData = previous.copy(
            egtCelsius    = celsius,
            regenStatus   = newStatus,
            regenStrategy = RegenStrategy.EGT_FALLBACK
        )
        applyStatusChange(previous, newData)
        if (celsius > COOLDOWN_HOT_EGT_THRESHOLD) wasHot = true
        checkAndStartCooldown(_dpfData.value)
    }

    /**
     * Strategy A — ECU direct flag PID response.
     * [flagActive] = true if the ECU response byte equals 0x01.
     * Overrides the EGT-based status when available.
     */
    fun updateRegenFlag(flagActive: Boolean) {
        val previous = _dpfData.value
        val newStatus = when {
            flagActive                                       -> RegenStatus.ACTIVE
            previous.regenStatus == RegenStatus.ACTIVE ||
            previous.regenStatus == RegenStatus.WARNING     -> RegenStatus.COMPLETED
            else                                            -> RegenStatus.INACTIVE
        }
        val newData = previous.copy(
            regenStatus   = newStatus,
            regenStrategy = RegenStrategy.DIRECT_FLAG
        )
        applyStatusChange(previous, newData)
    }

    /**
     * Updates the DPF differential pressure reading from PID 01 7A.
     * [kPa] = (256×B + C) / 100 per SAE J1979. Normal: ~0 at idle, 5–15 at cruise.
     */
    fun updateDpfDeltaPressure(kPa: Float) {
        _dpfData.value = _dpfData.value.copy(dpfDeltaPressureKpa = kPa)
    }

    fun updateOdometer(km: Long) {
        _dpfData.value = _dpfData.value.copy(odometerKm = km)
    }

    /** Updates km travelled since last DPF regeneration (PID 22 050B). */
    fun updateKmSinceLastRegen(km: Long) {
        _dpfData.value = _dpfData.value.copy(kmSinceLastRegen = km)
    }

    /** Updates km travelled since last oil change (PID 22 0542). */
    fun updateKmSinceOilChange(km: Long) {
        _dpfData.value = _dpfData.value.copy(kmSinceOilChange = km)
    }

    /** Updates engine RPM (PID 01 0C). Formula: ((A*256)+B)/4. */
    fun updateRpm(rpm: Float) {
        _dpfData.value = _dpfData.value.copy(rpmValue = rpm)
    }

    /** Updates vehicle speed in km/h (PID 01 0D). */
    fun updateSpeed(kmh: Float) {
        _dpfData.value = _dpfData.value.copy(speedKmh = kmh)
        checkAndStartCooldown(_dpfData.value)
    }

    /** Updates engine load % (PID 01 04). Formula: A*100/255. */
    fun updateEngineLoad(pct: Float) {
        _dpfData.value = _dpfData.value.copy(engineLoadPct = pct)
        if (pct > 25f) wasHot = true
        checkAndStartCooldown(_dpfData.value)
    }

    /** Updates intake MAP in kPa (PID 01 0B). Subtract baroKpa for boost delta. */
    fun updateIntakeMap(kpa: Float) {
        _dpfData.value = _dpfData.value.copy(intakeMapKpa = kpa)
    }

    /** Updates ambient barometric pressure in kPa (PID 01 33). */
    fun updateBaroPressure(kpa: Float) {
        _dpfData.value = _dpfData.value.copy(baroKpa = kpa)
    }

    /** Updates engine oil temperature in °C (PID 01 5C). */
    fun updateOilTemp(celsius: Float) {
        _dpfData.value = _dpfData.value.copy(oilTempC = celsius)
    }

    /** Updates post-DPF EGT sensor 2 in °C (from PID 01 78 byte pair 5–6). */
    fun updateEgtPost(celsius: Float) {
        _dpfData.value = _dpfData.value.copy(egtPostDpfC = celsius)
    }

    fun updateBleConnected(connected: Boolean) {
        _dpfData.value = _dpfData.value.copy(bleConnected = connected)
        if (!connected) resetCooldown()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Internal helpers
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Applies the updated [DpfData], fires [onRegenStatusChanged] if the status
     * changed, and records the last-regen km when a regeneration completes.
     */
    private fun applyStatusChange(previous: DpfData, updated: DpfData) {
        _dpfData.value = updated

        if (previous.regenStatus != updated.regenStatus) {
            onRegenStatusChanged?.invoke(previous.regenStatus, updated.regenStatus)

            // Fire history session events on state transitions
            when {
                // Regen just started (INACTIVE/COMPLETED → WARNING or ACTIVE)
                updated.regenStatus == RegenStatus.WARNING &&
                    previous.regenStatus == RegenStatus.INACTIVE ->
                    onRegenSessionEvent?.invoke("STARTED", updated)

                updated.regenStatus == RegenStatus.ACTIVE &&
                    previous.regenStatus != RegenStatus.ACTIVE ->
                    onRegenSessionEvent?.invoke("STARTED", updated)

                // Regen completed successfully
                updated.regenStatus == RegenStatus.COMPLETED ->
                    onRegenSessionEvent?.invoke("COMPLETED", updated)

                // Regen interrupted (went straight from ACTIVE/WARNING to INACTIVE without COMPLETED)
                updated.regenStatus == RegenStatus.INACTIVE &&
                    (previous.regenStatus == RegenStatus.ACTIVE ||
                     previous.regenStatus == RegenStatus.WARNING) ->
                    onRegenSessionEvent?.invoke("INTERRUPTED", updated)
            }
        }

        // Emit periodic data points while regen is active
        if (updated.regenStatus == RegenStatus.ACTIVE) {
            onRegenSessionEvent?.invoke("DATA_POINT", updated)
        }
    }

    /**
     * Strategy B — derives [RegenStatus] purely from EGT temperature.
     *
     * Hysteresis and a confirmation counter prevent rapid oscillation:
     *   • Going to ACTIVE requires [EGT_ACTIVE_CONFIRM_COUNT] consecutive hot samples.
     *   • Going back to INACTIVE requires EGT to drop below [EGT_SAFE_THRESHOLD].
     */
    private fun computeRegenStatusFromEgt(egt: Float, current: RegenStatus): RegenStatus {
        return when {
            // ── Transition to ACTIVE (confirmation counter) ───────────────────
            egt >= EGT_ACTIVE_THRESHOLD -> {
                egtActiveCounter++
                if (egtActiveCounter >= EGT_ACTIVE_CONFIRM_COUNT) RegenStatus.ACTIVE
                else current.takeIf { it == RegenStatus.ACTIVE } ?: RegenStatus.WARNING
            }

            // ── Transition to WARNING ─────────────────────────────────────────
            egt >= EGT_WARNING_THRESHOLD -> {
                egtActiveCounter = 0
                if (current == RegenStatus.ACTIVE) RegenStatus.ACTIVE  // stay until cooled
                else RegenStatus.WARNING
            }

            // ── Cooling down — was previously hot ─────────────────────────────
            egt < EGT_SAFE_THRESHOLD && (current == RegenStatus.ACTIVE ||
                                          current == RegenStatus.WARNING) -> {
                egtActiveCounter = 0
                RegenStatus.COMPLETED
            }

            // ── Normal operation ──────────────────────────────────────────────
            else -> {
                egtActiveCounter = 0
                if (current == RegenStatus.COMPLETED) RegenStatus.COMPLETED  // keep briefly
                else RegenStatus.INACTIVE
            }
        }
    }

    /** Resets COMPLETED back to INACTIVE after it has been acknowledged/shown. */
    fun acknowledgeCompleted() {
        if (_dpfData.value.regenStatus == RegenStatus.COMPLETED) {
            _dpfData.value = _dpfData.value.copy(
                regenStatus   = RegenStatus.INACTIVE,
                regenStrategy = RegenStrategy.NONE
            )
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Turbo cooldown — private helpers
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Called after every EGT or load update.
     * Starts the cooldown countdown when ALL conditions are met:
     *  • Engine was "hot" during this trip (EGT > 150°C or load > 25%)
     *  • Engine is now at idle (load < 12%, RPM in 500–1300)
     *  • No active regeneration in progress
     *  • Cooldown is not already running
     */
    private fun checkAndStartCooldown(data: DpfData) {
        if (!wasHot) return
        if (cooldownJob != null) return
        if (data.cooldownSecondsLeft >= 0) return
        if (data.regenStatus == RegenStatus.ACTIVE || data.regenStatus == RegenStatus.WARNING) return

        // "At idle" = engine running in idle RPM range AND vehicle stopped.
        // We use speed instead of engine load because diesel OBD2 load reads
        // 25-35% even at idle (it's relative to peak torque, not fuel delivery).
        val engineRunningAtIdle = data.rpmValue in COOLDOWN_IDLE_RPM_RANGE
        val vehicleStopped = data.speedKmh in 0f..COOLDOWN_STOPPED_KMH

        if (engineRunningAtIdle && vehicleStopped) startCooldown()
    }

    /** Launches the 45-second countdown, updating [DpfData.cooldownSecondsLeft] every second. */
    private fun startCooldown() {
        cooldownJob = cooldownScope.launch {
            for (s in COOLDOWN_DURATION_S downTo 1) {
                _dpfData.value = _dpfData.value.copy(cooldownSecondsLeft = s)
                delay(1_000L)
            }
            // Countdown done — mark as complete (0 = safe to turn off)
            _dpfData.value = _dpfData.value.copy(cooldownSecondsLeft = 0)
            // Show "done" for 15 s, then silently reset
            delay(15_000L)
            resetCooldown()
        }
    }

    /** Cancels any running countdown and clears all cooldown state. */
    private fun resetCooldown() {
        cooldownJob?.cancel()
        cooldownJob = null
        wasHot = false
        _dpfData.value = _dpfData.value.copy(cooldownSecondsLeft = -1)
    }
}
