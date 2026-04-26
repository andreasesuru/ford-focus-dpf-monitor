package com.example.fordfocusdpfscan.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fordfocusdpfscan.R
import com.example.fordfocusdpfscan.data.RegenHistoryRepository
import com.example.fordfocusdpfscan.data.db.RegenSession
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// ═══════════════════════════════════════════════════════════════════════════════
// HistoryActivity.kt — "📋 Storico" tab.
//
// Displays all recorded DPF regeneration sessions in a RecyclerView.
// Export button generates an HTML mechanic report and opens the share sheet.
// ═══════════════════════════════════════════════════════════════════════════════

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyRepo: RegenHistoryRepository
    private lateinit var adapter: SessionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        historyRepo = RegenHistoryRepository(this)

        // ── RecyclerView ─────────────────────────────────────────────────────
        val recycler = findViewById<RecyclerView>(R.id.rvSessions)
        adapter = SessionAdapter()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // ── Observe sessions ──────────────────────────────────────────────────
        lifecycleScope.launch {
            historyRepo.sessions.collectLatest { sessions ->
                adapter.submitList(sessions)

                // Show/hide empty state
                val emptyView  = findViewById<View>(R.id.viewEmptyState)
                val statsGroup = findViewById<View>(R.id.layoutStats)
                val isEmpty    = sessions.isEmpty()
                emptyView.visibility  = if (isEmpty) View.VISIBLE else View.GONE
                statsGroup.visibility = if (isEmpty) View.GONE   else View.VISIBLE

                if (sessions.isNotEmpty()) updateStats(sessions)
            }
        }

        // ── Tab bar navigation ────────────────────────────────────────────────
        // Mark this tab as active
        findViewById<TextView>(R.id.tabStorico).apply {
            setBackgroundResource(R.drawable.bg_tab_active)
            setTextColor(getColor(R.color.text_primary))
        }

        findViewById<View>(R.id.tabMonitor).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
        findViewById<View>(R.id.tabDiagnostica).setOnClickListener {
            startActivity(Intent(this, DiagnosticaActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        // ── Export button ─────────────────────────────────────────────────────
        findViewById<View>(R.id.btnExport).setOnClickListener {
            exportReport()
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Stats bar
    // ═════════════════════════════════════════════════════════════════════════

    private fun updateStats(sessions: List<RegenSession>) {
        val completed  = sessions.count { it.result == "COMPLETED" }
        val avgDur     = sessions.filter { it.durationMinutes != null }
            .map { it.durationMinutes!! }.average()
            .let { if (it.isNaN()) 0 else it.toInt() }
        val avgSoot    = sessions
            .filter { it.preSootPct >= 0 && it.postSootPct != null }
            .map { it.preSootPct - it.postSootPct!! }.average()
            .let { if (it.isNaN()) 0.0 else it }

        findViewById<TextView>(R.id.tvTotalRegens).text   = sessions.size.toString()
        findViewById<TextView>(R.id.tvCompleted).text     = completed.toString()
        findViewById<TextView>(R.id.tvAvgDuration).text   = "${avgDur} min"
        findViewById<TextView>(R.id.tvAvgSootReduction).text = "−${"%.1f".format(avgSoot)}%"
    }

    // ═════════════════════════════════════════════════════════════════════════
    // HTML export + share
    // ═════════════════════════════════════════════════════════════════════════

    private fun exportReport() {
        lifecycleScope.launch {
            try {
                val html = historyRepo.generateHtmlReport()
                val fileName = "DPF_Report_${
                    SimpleDateFormat("yyyyMMdd_HHmm", Locale.ITALY).format(Date())
                }.html"
                val file = File(cacheDir, fileName)
                file.writeText(html, Charsets.UTF_8)

                val uri = FileProvider.getUriForFile(
                    this@HistoryActivity,
                    "${packageName}.provider",
                    file
                )

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type     = "text/html"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "Report DPF — Ford Focus 1.5 TDCi")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(intent, "Invia report al meccanico"))

            } catch (e: Exception) {
                Toast.makeText(this@HistoryActivity, "Errore export: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // RecyclerView Adapter
    // ═════════════════════════════════════════════════════════════════════════

    inner class SessionAdapter : RecyclerView.Adapter<SessionAdapter.VH>() {

        private var items: List<RegenSession> = emptyList()

        fun submitList(list: List<RegenSession>) {
            items = list
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_regen_session, parent, false)
            return VH(v)
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(items[position], position + 1)
        }

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {

            private val fmt = SimpleDateFormat("dd MMM yyyy · HH:mm", Locale.ITALY)

            fun bind(s: RegenSession, num: Int) {
                val v = itemView
                val total = items.size

                v.findViewById<TextView>(R.id.tvSessionNum).text   = "#${total - num + 1}"
                v.findViewById<TextView>(R.id.tvSessionDate).text  = fmt.format(Date(s.startTimestamp))
                v.findViewById<TextView>(R.id.tvSessionOdo).text   =
                    if (s.preOdometerKm >= 0) "📍 %,d km · ${s.durationMinutes ?: "—"} min".format(s.preOdometerKm)
                    else "📍 — · ${s.durationMinutes ?: "—"} min"

                // Soot before → after
                val sootPre  = if (s.preSootPct  >= 0) "${"%.0f".format(s.preSootPct)}%" else "—"
                val sootPost = s.postSootPct?.let { "${"%.0f".format(it)}%" } ?: "—"
                v.findViewById<TextView>(R.id.tvSootChange).text = "$sootPre → $sootPost"

                // Peak EGT
                v.findViewById<TextView>(R.id.tvPeakEgt).text =
                    if (s.peakEgtC > 0) "${"%.0f".format(s.peakEgtC)} °C" else "—"

                // Coolant (indicates engine was warm enough)
                v.findViewById<TextView>(R.id.tvCoolant).text =
                    if (s.preCoolantC >= 0) "${"%.0f".format(s.preCoolantC)} °C" else "—"

                // Result badge
                val tvResult = v.findViewById<TextView>(R.id.tvResult)
                when (s.result) {
                    "COMPLETED"   -> { tvResult.text = "✓ Completata"; tvResult.setTextColor(0xFF34C759.toInt()) }
                    "INTERRUPTED" -> { tvResult.text = "⚠ Interrotta"; tvResult.setTextColor(0xFFFF9F0A.toInt()) }
                    "IN_PROGRESS" -> { tvResult.text = "⟳ In corso";   tvResult.setTextColor(0xFF4F8EF7.toInt()) }
                    else           -> { tvResult.text = s.result }
                }

                // Warning note for interrupted regens
                val warnView = v.findViewById<TextView>(R.id.tvWarningNote)
                if (s.result == "INTERRUPTED") {
                    warnView.visibility = View.VISIBLE
                    warnView.text = "⚠ Regen interrotta — soot non ridotto. Valutare regen forzata con Ford IDS."
                } else {
                    warnView.visibility = View.GONE
                }
            }
        }
    }
}
