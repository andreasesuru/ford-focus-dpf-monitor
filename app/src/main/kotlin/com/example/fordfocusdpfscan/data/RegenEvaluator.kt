package com.example.fordfocusdpfscan.data

// ═══════════════════════════════════════════════════════════════════════════════
// RegenEvaluator.kt — Multi-signal DPF regeneration state machine.
//
// Pure (no Android deps) → fully unit-testable (see RegenEvaluatorTest).
//
// A regeneration is declared ACTIVE when ANY of three independent signals fires;
// this catches regens the old EGT-only logic missed (e.g. a regen running with a
// only moderate inlet EGT, visible only as soot slowly falling):
//
//   1. EGT_TEMP  — inlet EGT ≥ 550 °C sustained (classic post-injection regen).
//   2. SOOT_DROP — soot % falling steadily while the engine is warm. Diesel soot
//                  only decreases during regeneration, so a sustained drop is a
//                  direct proof that a cycle is in progress.
//   3. EXOTHERM  — outlet EGT much hotter than inlet (soot burning inside the
//                  filter generates heat), even when the inlet reads moderate.
//
// Hysteresis on the WARNING band (enter 450 °C, leave only below 420 °C) stops the
// status from flapping when the EGT hovers around 450 °C during normal hot driving
// — that flapping used to fire a heads-up + chime every poll cycle.
// ═══════════════════════════════════════════════════════════════════════════════

class RegenEvaluator {

    companion object {
        /** Below this → cool enough for the cycle to be considered finished. */
        const val EGT_SAFE_THRESHOLD: Float   = 400f
        /** At/above this → enter WARNING. */
        const val EGT_WARNING_ENTER: Float    = 450f
        /** Stay in WARNING until EGT drops below this (hysteresis, prevents flapping). */
        const val EGT_WARNING_EXIT: Float     = 420f
        /** At/above this (sustained) → ACTIVE via inlet temperature. */
        const val EGT_ACTIVE_THRESHOLD: Float = 550f
        /** Consecutive hot samples required before EGT_TEMP declares ACTIVE. */
        const val EGT_ACTIVE_CONFIRM_COUNT = 7

        // ── Soot-drop detection ─────────────────────────────────────────────────
        /** Sliding window over which the soot trend is measured. */
        const val SOOT_DROP_WINDOW_MS = 120_000L
        /** The window must span at least this long before a drop is trusted. */
        const val SOOT_DROP_MIN_SPAN_MS = 90_000L
        /** Minimum net soot drop (percentage points) across the window → regen. */
        const val SOOT_DROP_MIN_PCT = 2f

        // ── "Engine warm" gate for soot-drop (avoids cold-start model noise) ─────
        const val WARM_COOLANT_C = 60f
        const val WARM_EGT_C     = 200f

        // ── Exotherm across the filter (outlet − inlet) ──────────────────────────
        const val EXOTHERM_MIN_DELTA_C = 100f
        const val EXOTHERM_MIN_POST_C  = 450f
    }

    /** Running count of consecutive samples above [EGT_ACTIVE_THRESHOLD]. */
    var activeCounter = 0
        private set

    /** Rolling (timestamp, soot%) history used for the soot-drop trend. */
    private val sootHistory = ArrayDeque<Pair<Long, Float>>()

    /**
     * Computes the next [RegenResult] from the latest [sample] and the [current]
     * status. Mutates internal state (counter + soot history) as a side effect.
     */
    fun evaluate(sample: RegenSample, current: RegenStatus): RegenResult {
        recordSoot(sample.timestampMs, sample.soot)

        // ── 1. Active-regen signals ───────────────────────────────────────────
        val egtHigh = if (sample.egtPre >= EGT_ACTIVE_THRESHOLD) {
            activeCounter++
            activeCounter >= EGT_ACTIVE_CONFIRM_COUNT
        } else {
            activeCounter = 0
            false
        }

        val warm = sample.coolant >= WARM_COOLANT_C || sample.egtPre >= WARM_EGT_C
        val sootDrop = warm && sootDropInWindow(sample.timestampMs) >= SOOT_DROP_MIN_PCT

        val exotherm = sample.egtPost >= 0f && sample.egtPre >= 0f &&
            sample.egtPost >= EXOTHERM_MIN_POST_C &&
            (sample.egtPost - sample.egtPre) >= EXOTHERM_MIN_DELTA_C

        val trigger = when {
            egtHigh  -> RegenStrategy.EGT_TEMP
            sootDrop -> RegenStrategy.SOOT_DROP
            exotherm -> RegenStrategy.EXOTHERM
            else     -> null
        }
        if (trigger != null) return RegenResult(RegenStatus.ACTIVE, trigger)

        // ── 2. No active signal → cool-down / WARNING / INACTIVE (hysteresis) ──
        val egt = sample.egtPre
        val wasHot = current == RegenStatus.ACTIVE || current == RegenStatus.WARNING
        return when {
            // Was hot, now cooled below the safe threshold → cycle completed.
            wasHot && egt >= 0f && egt < EGT_SAFE_THRESHOLD ->
                RegenResult(RegenStatus.COMPLETED, RegenStrategy.NONE)

            // Active but still hot (signal dropped) → stay ACTIVE until it cools.
            current == RegenStatus.ACTIVE && egt >= EGT_SAFE_THRESHOLD ->
                RegenResult(RegenStatus.ACTIVE, RegenStrategy.EGT_TEMP)

            // Enter WARNING.
            egt >= EGT_WARNING_ENTER ->
                RegenResult(RegenStatus.WARNING, RegenStrategy.EGT_TEMP)

            // Stay in WARNING within the hysteresis band (420–449 °C).
            current == RegenStatus.WARNING && egt >= EGT_WARNING_EXIT ->
                RegenResult(RegenStatus.WARNING, RegenStrategy.EGT_TEMP)

            // Hold COMPLETED briefly while still cool.
            current == RegenStatus.COMPLETED && egt >= 0f && egt < EGT_SAFE_THRESHOLD ->
                RegenResult(RegenStatus.COMPLETED, RegenStrategy.NONE)

            else ->
                RegenResult(RegenStatus.INACTIVE, RegenStrategy.NONE)
        }
    }

    private fun recordSoot(now: Long, soot: Float) {
        if (soot < 0f) return
        sootHistory.addLast(now to soot)
        val cutoff = now - SOOT_DROP_WINDOW_MS
        while (sootHistory.isNotEmpty() && sootHistory.first().first < cutoff) {
            sootHistory.removeFirst()
        }
    }

    /**
     * Net soot drop (percentage points) across the retained window: positive means
     * soot fell. Returns 0 until the window spans [SOOT_DROP_MIN_SPAN_MS], so a
     * momentary dip can't trigger a regen.
     */
    private fun sootDropInWindow(now: Long): Float {
        if (sootHistory.size < 2) return 0f
        val oldest = sootHistory.first()
        val newest = sootHistory.last()
        if (newest.first - oldest.first < SOOT_DROP_MIN_SPAN_MS) return 0f
        return oldest.second - newest.second
    }

    /** Clears all state (call on disconnect so a new session starts clean). */
    fun reset() {
        activeCounter = 0
        sootHistory.clear()
    }
}

/** One reading fed to [RegenEvaluator]. -1f fields mean "no data". */
data class RegenSample(
    val egtPre: Float,
    val egtPost: Float,
    val soot: Float,
    val coolant: Float,
    val timestampMs: Long
)

/** The evaluator's verdict: the status and which signal produced it. */
data class RegenResult(
    val status: RegenStatus,
    val strategy: RegenStrategy
)
