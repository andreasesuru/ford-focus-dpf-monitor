package com.example.fordfocusdpfscan.data

import org.junit.Assert.assertEquals
import org.junit.Test
import com.example.fordfocusdpfscan.data.RegenEvaluator.Companion.EGT_ACTIVE_CONFIRM_COUNT

// ═══════════════════════════════════════════════════════════════════════════════
// RegenEvaluatorTest.kt — Unit tests for the multi-signal regen state machine.
//
// Run on the JVM (no emulator): ./gradlew testDebugUnitTest
// ═══════════════════════════════════════════════════════════════════════════════

class RegenEvaluatorTest {

    private val ev = RegenEvaluator()

    private fun sample(
        egtPre: Float,
        egtPost: Float = -1f,
        soot: Float = -1f,
        coolant: Float = 90f,
        t: Long = 0L
    ) = RegenSample(egtPre, egtPost, soot, coolant, t)

    /** Feeds a hot inlet-EGT sample [times] times, returns the final status. */
    private fun feedHot(egt: Float, times: Int, start: RegenStatus): RegenStatus {
        var s = start
        repeat(times) { s = ev.evaluate(sample(egt), s).status }
        return s
    }

    // ── INACTIVE / normal ─────────────────────────────────────────────────────

    @Test fun coldEngine_staysInactive() {
        assertEquals(RegenStatus.INACTIVE, ev.evaluate(sample(90f), RegenStatus.INACTIVE).status)
    }

    @Test fun mildWarmth_belowWarning_staysInactive() {
        assertEquals(RegenStatus.INACTIVE, ev.evaluate(sample(420f), RegenStatus.INACTIVE).status)
    }

    // ── WARNING + hysteresis ──────────────────────────────────────────────────

    @Test fun warningThreshold_entersWarning() {
        val r = ev.evaluate(sample(500f), RegenStatus.INACTIVE)
        assertEquals(RegenStatus.WARNING, r.status)
        assertEquals(RegenStrategy.EGT_TEMP, r.strategy)
    }

    @Test fun warning_staysWithinHysteresisBand_noFlapping() {
        // 430 °C would previously flap back to INACTIVE; hysteresis keeps it WARNING.
        assertEquals(RegenStatus.WARNING, ev.evaluate(sample(430f), RegenStatus.WARNING).status)
    }

    @Test fun warning_dropsOutBelowExitThreshold() {
        assertEquals(RegenStatus.INACTIVE, ev.evaluate(sample(415f), RegenStatus.WARNING).status)
    }

    // ── ACTIVE via sustained inlet EGT ────────────────────────────────────────

    @Test fun singleHotSample_isOnlyWarning() {
        assertEquals(RegenStatus.WARNING, ev.evaluate(sample(600f), RegenStatus.INACTIVE).status)
    }

    @Test fun sustainedHeat_confirmsActive() {
        assertEquals(RegenStatus.WARNING, feedHot(600f, EGT_ACTIVE_CONFIRM_COUNT - 1, RegenStatus.INACTIVE))
        val r = ev.evaluate(sample(600f), RegenStatus.WARNING)
        assertEquals(RegenStatus.ACTIVE, r.status)
        assertEquals(RegenStrategy.EGT_TEMP, r.strategy)
    }

    @Test fun onceActive_staysActiveThroughWarningBand() {
        val s = feedHot(600f, EGT_ACTIVE_CONFIRM_COUNT, RegenStatus.INACTIVE)
        assertEquals(RegenStatus.ACTIVE, s)
        assertEquals(RegenStatus.ACTIVE, ev.evaluate(sample(480f), RegenStatus.ACTIVE).status)
    }

    // ── ACTIVE via falling soot (the case EGT-only logic missed) ───────────────

    @Test fun fallingSootWhileWarm_confirmsActive() {
        ev.evaluate(sample(320f, soot = 20f, coolant = 90f, t = 0L), RegenStatus.INACTIVE)
        ev.evaluate(sample(320f, soot = 18f, coolant = 90f, t = 30_000L), RegenStatus.INACTIVE)
        ev.evaluate(sample(320f, soot = 16f, coolant = 90f, t = 60_000L), RegenStatus.INACTIVE)
        val r = ev.evaluate(sample(320f, soot = 15f, coolant = 90f, t = 95_000L), RegenStatus.INACTIVE)
        assertEquals(RegenStatus.ACTIVE, r.status)
        assertEquals(RegenStrategy.SOOT_DROP, r.strategy)
    }

    @Test fun fallingSootWhileCold_doesNotTrigger() {
        ev.evaluate(sample(100f, soot = 20f, coolant = 30f, t = 0L), RegenStatus.INACTIVE)
        val r = ev.evaluate(sample(100f, soot = 15f, coolant = 30f, t = 95_000L), RegenStatus.INACTIVE)
        assertEquals(RegenStatus.INACTIVE, r.status)
    }

    @Test fun fallingSoot_tooShortWindow_doesNotTrigger() {
        ev.evaluate(sample(320f, soot = 20f, coolant = 90f, t = 0L), RegenStatus.INACTIVE)
        val r = ev.evaluate(sample(320f, soot = 15f, coolant = 90f, t = 50_000L), RegenStatus.INACTIVE)
        assertEquals(RegenStatus.INACTIVE, r.status)
    }

    // ── ACTIVE via exotherm across the filter ─────────────────────────────────

    @Test fun exothermAcrossFilter_confirmsActive() {
        val r = ev.evaluate(sample(egtPre = 350f, egtPost = 480f, coolant = 90f), RegenStatus.INACTIVE)
        assertEquals(RegenStatus.ACTIVE, r.status)
        assertEquals(RegenStrategy.EXOTHERM, r.strategy)
    }

    // ── COMPLETED (cool-down) ─────────────────────────────────────────────────

    @Test fun coolingDownFromActive_completes() {
        assertEquals(RegenStatus.COMPLETED, ev.evaluate(sample(300f), RegenStatus.ACTIVE).status)
    }

    @Test fun coolingDownFromWarning_completes() {
        assertEquals(RegenStatus.COMPLETED, ev.evaluate(sample(300f), RegenStatus.WARNING).status)
    }

    @Test fun completed_isHeldBrieflyWhileCool() {
        assertEquals(RegenStatus.COMPLETED, ev.evaluate(sample(120f), RegenStatus.COMPLETED).status)
    }

    // ── Counter / reset ───────────────────────────────────────────────────────

    @Test fun counterResets_whenHeatNotSustained() {
        feedHot(600f, EGT_ACTIVE_CONFIRM_COUNT - 1, RegenStatus.INACTIVE)
        ev.evaluate(sample(200f), RegenStatus.WARNING)
        assertEquals(0, ev.activeCounter)
        assertEquals(RegenStatus.WARNING, ev.evaluate(sample(600f), RegenStatus.INACTIVE).status)
    }

    @Test fun reset_clearsState() {
        feedHot(600f, 3, RegenStatus.INACTIVE)
        ev.reset()
        assertEquals(0, ev.activeCounter)
    }
}
