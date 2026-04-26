package com.example.fordfocusdpfscan.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.fordfocusdpfscan.ble.BleManagerHolder
import com.example.fordfocusdpfscan.ble.EcuPidScanner
import com.example.fordfocusdpfscan.data.EcuScanRepository
import com.example.fordfocusdpfscan.data.PidResult
import com.example.fordfocusdpfscan.data.PidStatus
import com.example.fordfocusdpfscan.data.ScanPhase
import com.example.fordfocusdpfscan.data.ScanState
import com.example.fordfocusdpfscan.databinding.ActivityEcuScanBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ═══════════════════════════════════════════════════════════════════════════════
// EcuScanActivity.kt — Phone UI for the ECU PID discovery scan.
//
// Responsibilities:
//   1. Display real-time scan progress (phase, counts, live log).
//   2. Start / stop the EcuPidScanner via BleManagerHolder.
//   3. Share the complete log file via system share sheet.
//
// The activity obtains the BleManager instance from BleManagerHolder (set by
// DpfForegroundService on connect).  If no instance is available it shows an
// error and finishes.
//
// The log is rendered in a monospace TextView inside a ScrollView and auto-
// scrolls to the bottom after each new line is appended.
// ═══════════════════════════════════════════════════════════════════════════════

class EcuScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEcuScanBinding

    private var scanner: EcuPidScanner? = null

    // Tracks the phase currently shown in the log so we can insert headers
    private var lastLogPhase: ScanPhase? = null
    // Count of results rendered so far — used to append only new lines
    private var renderedResultCount = 0

    // ═════════════════════════════════════════════════════════════════════════
    // Lifecycle
    // ═════════════════════════════════════════════════════════════════════════

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEcuScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupButtons()
        observeScanState()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel any running scan when the activity is closed so the normal
        // DPF polling loop resumes promptly.
        scanner?.cancelScan()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Setup
    // ═════════════════════════════════════════════════════════════════════════

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupButtons() {

        // ── Start scan ────────────────────────────────────────────────────────
        binding.btnStartScan.setOnClickListener {
            val bleManager = BleManagerHolder.instance
            if (bleManager == null) {
                Toast.makeText(this,
                    "OBD dongle not connected. Connect first from the main screen.",
                    Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Reset log state
            lastLogPhase = null
            renderedResultCount = 0
            binding.tvLog.text = ""

            // Create scanner and start
            scanner = EcuPidScanner(bleManager)
            scanner!!.startScan(lifecycleScope)

            // Update button visibility
            binding.btnStartScan.visibility = View.GONE
            binding.btnStopScan.visibility  = View.VISIBLE
            binding.btnShareLog.isEnabled   = false
        }

        // ── Stop scan ─────────────────────────────────────────────────────────
        binding.btnStopScan.setOnClickListener {
            scanner?.cancelScan()
            binding.btnStopScan.visibility  = View.GONE
            binding.btnStartScan.visibility = View.VISIBLE
            binding.tvCurrentPhase.text = "Scan cancelled"
        }

        // ── Share log — writes a .txt file and shares via FileProvider ───────
        // Using EXTRA_STREAM instead of EXTRA_TEXT avoids truncation by
        // WhatsApp / Drive / Gmail which cap inline text at ~250 KB.
        binding.btnShareLog.setOnClickListener {
            val logText = EcuScanRepository.buildLogText()
            try {
                val dateTag = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val dir = File(cacheDir, "scan_reports").also { it.mkdirs() }
                val file = File(dir, "FOCUS_scan_$dateTag.txt")
                file.writeText(logText, Charsets.UTF_8)

                val uri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.provider",
                    file
                )
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "FOCUS ECU PID Scan Report")
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(intent, "Condividi report ECU"))
            } catch (e: Exception) {
                Toast.makeText(this, "Errore salvataggio: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // State observer
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Collects [EcuScanRepository.scanState] and:
     *   • updates stats (phase label, progress bar, counts)
     *   • appends only NEW result lines to the log TextView
     *   • auto-scrolls the ScrollView to the bottom
     */
    private fun observeScanState() {
        lifecycleScope.launch {
            EcuScanRepository.scanState.collectLatest { state ->
                updateStats(state)
                appendNewResults(state)

                if (state.isCompleted) {
                    onScanCompleted(state)
                }
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // UI update helpers
    // ═════════════════════════════════════════════════════════════════════════

    private fun updateStats(state: ScanState) {
        // Phase label
        val phaseLabel = when {
            state.isCompleted -> "✅ Scan complete"
            state.isRunning && state.currentPhase != null ->
                "Phase: ${state.currentPhase.displayName}"
            else -> "Ready to scan"
        }
        binding.tvCurrentPhase.text = phaseLabel

        // Progress bar
        binding.progressPhase.progress = state.phaseProgressPercent

        // Count badges
        binding.tvCountResponded.text = state.totalResponded.toString()
        binding.tvCountNegative.text  = state.totalNegative.toString()
        binding.tvCountTimeout.text   = state.totalTimeout.toString()
    }

    /**
     * Appends only the results that have been added since the last render.
     * This avoids rebuilding the entire log on every emission.
     */
    private fun appendNewResults(state: ScanState) {
        val newResults = state.results.drop(renderedResultCount)
        if (newResults.isEmpty()) return

        val sb = StringBuilder()

        for (result in newResults) {
            // Insert phase header when phase changes
            if (result.phase != lastLogPhase) {
                if (lastLogPhase != null) sb.append("\n")
                sb.append("── ${result.phase.displayName} ──\n")
                lastLogPhase = result.phase
            }
            sb.append(formatResult(result))
            sb.append("\n")
        }

        binding.tvLog.append(sb.toString())
        renderedResultCount = state.results.size

        // Auto-scroll to bottom
        binding.scrollLog.post {
            binding.scrollLog.fullScroll(View.FOCUS_DOWN)
        }
    }

    /**
     * Formats a single [PidResult] line for the monospace log.
     * Format: [✓] 22 11 49  62 11 49 00 1E = ~1.5 g soot  [★★★ DPF Soot]
     */
    private fun formatResult(result: PidResult): String {
        val decoded = result.decodedValue?.let { " = $it" } ?: ""
        val label   = result.label?.let { "  [$it]" } ?: ""
        val pid     = result.pidHex.padEnd(8)
        val resp    = if (result.status == PidStatus.TIMEOUT) "(timeout)" else result.responseHex
        return "[${result.status.symbol}] $pid  $resp$decoded$label"
    }

    private fun onScanCompleted(state: ScanState) {
        binding.btnStopScan.visibility  = View.GONE
        binding.btnStartScan.visibility = View.VISIBLE
        binding.btnShareLog.isEnabled   = true
        binding.progressPhase.progress  = 100

        // Append summary footer to log
        binding.tvLog.append(
            "\n\n══ SCAN COMPLETE ══\n" +
            "  Replied : ${state.totalResponded}\n" +
            "  Negative: ${state.totalNegative}\n" +
            "  Timeout : ${state.totalTimeout}\n" +
            "\nTap [Share Log] to send the report."
        )

        binding.scrollLog.post {
            binding.scrollLog.fullScroll(View.FOCUS_DOWN)
        }

        Toast.makeText(this, "Scan complete! ${state.totalResponded} PIDs responded.", Toast.LENGTH_LONG).show()
    }
}
