package com.example.fordfocusdpfscan.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

// ═══════════════════════════════════════════════════════════════════════════════
// EcuScanRepository.kt — State holder for the ECU PID discovery scan.
//
// Consumed by:
//   • EcuPidScanner  — pushes results in real-time
//   • EcuScanActivity — collects ScanState to update the UI
//
// The entire scan state is collapsed into a single StateFlow<ScanState> so the
// Activity only needs one observer.  buildLogText() formats the full report
// for the share intent.
// ═══════════════════════════════════════════════════════════════════════════════

// ─────────────────────────────────────────────────────────────────────────────
// Enums
// ─────────────────────────────────────────────────────────────────────────────

/** Classification of the ECU's reply to a single PID query. */
enum class PidStatus(val symbol: String) {
    RESPONDED("✓"),   // Positive response (41 / 62 / 49)
    NEGATIVE("✗"),    // Negative response (7F)
    TIMEOUT("–")      // No reply within the timeout window
}

/** The scan phases executed in order by EcuPidScanner. */
enum class ScanPhase(val displayName: String) {
    PRIORITY      ("Priority PIDs (known/important)"),
    MODE01_SWEEP  ("Mode 01 — Full sweep 00–FF"),
    MODE09_INFO   ("Mode 09 — Vehicle info (VIN…)"),
    UDS_05xx      ("UDS 22 05xx — DPF Load/Soot area (2.0 TDCi analogue)"),
    UDS_09xx      ("UDS 22 09xx — DPF pressure community PIDs"),
    UDS_10xx      ("UDS 22 11xx (DPF / Engine — confirmed range)"),
    UDS_40xx      ("UDS 22 40xx–41xx (TPMS / BCM)"),
    UDS_DDxx      ("UDS 22 DDxx (Odometer + counters — full range)"),
    UDS_F1xx      ("UDS 22 F1xx (Standard UDS IDs)"),
    UDS_FDxx      ("UDS 22 FDxx — Regen counters (last regen km, failed regens…)")
}

// ─────────────────────────────────────────────────────────────────────────────
// Data classes
// ─────────────────────────────────────────────────────────────────────────────

/** A single PID query result, pushed to the repository after each command. */
data class PidResult(
    val phase        : ScanPhase,
    val pidHex       : String,         // e.g. "22 11 49"
    val label        : String?,        // human-readable name if known, else null
    val status       : PidStatus,
    val responseHex  : String,         // raw response bytes as hex, "—" on timeout
    val decodedValue : String?,        // human-readable decoded value if available
    val timestamp    : Long            // System.currentTimeMillis()
)

/**
 * Snapshot of the entire scan state — emitted by [EcuScanRepository.scanState]
 * on every change so the UI always has a consistent picture.
 */
data class ScanState(
    val isRunning       : Boolean         = false,
    val isCompleted     : Boolean         = false,
    val currentPhase    : ScanPhase?      = null,
    val phaseTotalPids  : Int             = 0,
    val phaseProgress   : Int             = 0,
    val totalResponded  : Int             = 0,
    val totalNegative   : Int             = 0,
    val totalTimeout    : Int             = 0,
    val results         : List<PidResult> = emptyList()
) {
    /** Overall count of PIDs queried so far. */
    val totalQueried: Int get() = totalResponded + totalNegative + totalTimeout

    /** Phase progress as 0–100 for a ProgressBar. */
    val phaseProgressPercent: Int
        get() = if (phaseTotalPids > 0) (phaseProgress * 100 / phaseTotalPids) else 0
}

// ─────────────────────────────────────────────────────────────────────────────
// Repository singleton
// ─────────────────────────────────────────────────────────────────────────────

object EcuScanRepository {

    private val _scanState = MutableStateFlow(ScanState())
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    // ── Called by EcuPidScanner ───────────────────────────────────────────────

    /** Resets all state and marks the scan as running. */
    fun reset() {
        _scanState.value = ScanState(isRunning = true)
    }

    /** Signals the start of a new scan phase. */
    fun setPhase(phase: ScanPhase, totalPids: Int) {
        _scanState.value = _scanState.value.copy(
            currentPhase   = phase,
            phaseTotalPids = totalPids,
            phaseProgress  = 0
        )
    }

    /** Appends a single [PidResult] to the live results list. */
    fun addResult(result: PidResult) {
        val current = _scanState.value
        _scanState.value = current.copy(results = current.results + result)
    }

    /**
     * Increments the phase progress counter and the appropriate status bucket.
     * Called for every PID, even timeouts (so the progress bar stays accurate).
     */
    fun incrementProgress(progress: Int, status: PidStatus) {
        val s = _scanState.value
        _scanState.value = s.copy(
            phaseProgress  = progress,
            totalResponded = s.totalResponded + if (status == PidStatus.RESPONDED) 1 else 0,
            totalNegative  = s.totalNegative  + if (status == PidStatus.NEGATIVE)  1 else 0,
            totalTimeout   = s.totalTimeout   + if (status == PidStatus.TIMEOUT)   1 else 0
        )
    }

    /** Marks the scan as finished. */
    fun setCompleted() {
        _scanState.value = _scanState.value.copy(isRunning = false, isCompleted = true)
    }

    // ── Log generation ────────────────────────────────────────────────────────

    /**
     * Formats all results into a plain-text log suitable for sharing.
     * Sections are separated by phase headers.  Known PIDs are annotated
     * with their label and decoded value so you can immediately identify them.
     */
    fun buildLogText(): String {
        val state   = _scanState.value
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        return buildString {
            appendLine("═══════════════════════════════════════════════════════")
            appendLine("  FOCUS — ECU PID SCAN REPORT")
            appendLine("  Generated: $dateStr")
            appendLine("═══════════════════════════════════════════════════════")
            appendLine()
            appendLine("  SUMMARY")
            appendLine("  ─────────────────────────────────────────────────────")
            appendLine("  PIDs queried    : ${state.totalQueried}")
            appendLine("  Positive replies: ${state.totalResponded}  ✓")
            appendLine("  Negative replies: ${state.totalNegative}  ✗")
            appendLine("  Timeouts        : ${state.totalTimeout}  –")
            appendLine()

            // Group results by phase for readability
            var lastPhase: ScanPhase? = null
            for (result in state.results) {
                if (result.phase != lastPhase) {
                    appendLine()
                    appendLine("  ── ${result.phase.displayName} ──")
                    lastPhase = result.phase
                }

                val decoded = result.decodedValue?.let { " = $it" } ?: ""
                val label   = result.label?.let { "  [$it]" } ?: ""
                appendLine("  [${result.status.symbol}] ${result.pidHex.padEnd(8)}  ${result.responseHex}$decoded$label")
            }

            appendLine()
            appendLine("═══════════════════════════════════════════════════════")
            appendLine("  END OF REPORT")
            appendLine()
            appendLine("  HOW TO USE THIS LOG:")
            appendLine("  1. Look for ✓ lines — these are PIDs your ECU supports.")
            appendLine("  2. ★★★ labels = priority candidates for EGT / regen flag.")
            appendLine("  3. Send this file to the developer to map real PID values.")
        }
    }
}
