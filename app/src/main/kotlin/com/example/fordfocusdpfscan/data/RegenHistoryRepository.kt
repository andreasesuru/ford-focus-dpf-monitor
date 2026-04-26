package com.example.fordfocusdpfscan.data

import android.content.Context
import android.util.Log
import com.example.fordfocusdpfscan.data.db.RegenDataPoint
import com.example.fordfocusdpfscan.data.db.RegenDatabase
import com.example.fordfocusdpfscan.data.db.RegenSession
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

// ═══════════════════════════════════════════════════════════════════════════════
// RegenHistoryRepository.kt — Manages the lifecycle of regen sessions.
//
// Called by DpfForegroundService when the regen state changes:
//   • onRegenStarted()   → inserts a new IN_PROGRESS session with pre-regen snapshot
//   • onRegenDataPoint() → appends a periodic sample (~30 s) during ACTIVE regen
//   • onRegenEnded()     → finalises the session with post-regen data + result
//
// Also generates the HTML mechanic report via exportToHtml().
// ═══════════════════════════════════════════════════════════════════════════════

class RegenHistoryRepository(context: Context) {

    private val TAG = "FOCUS_History"

    private val dao = RegenDatabase.getInstance(context).regenDao()

    /** Live list of all sessions, newest first. Observed by HistoryActivity. */
    val sessions: Flow<List<RegenSession>> = dao.getAllSessions()

    // ── Active session state ──────────────────────────────────────────────────
    private var currentSessionId: Long? = null
    private var sessionStartTime: Long  = 0L
    private var peakEgt:    Float = 0f
    private var peakDeltaP: Float = 0f

    // ═════════════════════════════════════════════════════════════════════════
    // Session lifecycle
    // ═════════════════════════════════════════════════════════════════════════

    /** Called when regen transitions to WARNING or ACTIVE. */
    suspend fun onRegenStarted(data: DpfData) {
        if (currentSessionId != null) return   // already tracking a session

        val session = RegenSession(
            startTimestamp = System.currentTimeMillis(),
            preOdometerKm  = data.odometerKm,
            preSootPct     = data.sootPercentage,
            preLoadPct     = data.loadPercentage,
            preDeltaPKpa   = data.dpfDeltaPressureKpa,
            preEgtC        = data.egtCelsius,
            preCoolantC    = data.coolantTempC,
            regenType      = if (data.regenStrategy == RegenStrategy.DIRECT_FLAG) "ACTIVE" else "ACTIVE",
            result         = "IN_PROGRESS"
        )

        currentSessionId = dao.insertSession(session)
        sessionStartTime = session.startTimestamp
        peakEgt          = data.egtCelsius.coerceAtLeast(0f)
        peakDeltaP       = data.dpfDeltaPressureKpa.coerceAtLeast(0f)

        Log.d(TAG, "Regen session #$currentSessionId started")
    }

    /** Called every ~30 seconds while regen is ACTIVE. */
    suspend fun onRegenDataPoint(data: DpfData) {
        val sessionId = currentSessionId ?: return

        // Update running peaks
        if (data.egtCelsius > peakEgt)         peakEgt    = data.egtCelsius
        if (data.dpfDeltaPressureKpa > peakDeltaP) peakDeltaP = data.dpfDeltaPressureKpa

        val elapsed = ((System.currentTimeMillis() - sessionStartTime) / 1000).toInt()

        dao.insertDataPoint(
            RegenDataPoint(
                sessionId      = sessionId,
                timestamp      = System.currentTimeMillis(),
                elapsedSeconds = elapsed,
                sootPct        = data.sootPercentage,
                loadPct        = data.loadPercentage,
                deltaPKpa      = data.dpfDeltaPressureKpa,
                egtC           = data.egtCelsius,
                coolantC       = data.coolantTempC
            )
        )
    }

    /**
     * Called when regen ends (COMPLETED or INTERRUPTED).
     * [result] = "COMPLETED" | "INTERRUPTED"
     */
    suspend fun onRegenEnded(data: DpfData, result: String) {
        val sessionId = currentSessionId ?: return

        val endTime        = System.currentTimeMillis()
        val durationMinutes = ((endTime - sessionStartTime) / 60_000).toInt()

        val current = dao.getActiveSession() ?: run {
            Log.w(TAG, "onRegenEnded: no active session found in DB")
            currentSessionId = null
            return
        }

        dao.updateSession(
            current.copy(
                endTimestamp    = endTime,
                postSootPct     = data.sootPercentage,
                postLoadPct     = data.loadPercentage,
                postOdometerKm  = data.odometerKm,
                postEgtC        = data.egtCelsius,
                postCoolantC    = data.coolantTempC,
                peakEgtC        = peakEgt,
                peakDeltaPKpa   = peakDeltaP,
                durationMinutes = durationMinutes,
                result          = result
            )
        )

        Log.d(TAG, "Regen session #$sessionId ended → $result (${durationMinutes} min)")
        currentSessionId = null
    }

    // ═════════════════════════════════════════════════════════════════════════
    // HTML Export — mechanic report
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Generates a complete HTML report string suitable for sharing with a mechanic.
     * Includes: vehicle info, summary stats, session table, and per-session details.
     */
    suspend fun generateHtmlReport(vehicleInfo: String = "Ford Focus 1.5 TDCi (EDC17C70)"): String {
        val sessions   = dao.getAllSessionsOnce()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY)
        val genDate    = dateFormat.format(Date())

        val completed  = sessions.count { it.result == "COMPLETED" }
        val interrupted= sessions.count { it.result == "INTERRUPTED" }
        val avgDuration= sessions.filter { it.durationMinutes != null }.map { it.durationMinutes!! }
            .average().let { if (it.isNaN()) 0 else it.toInt() }
        val avgSootReduction = sessions
            .filter { it.preSootPct >= 0 && it.postSootPct != null }
            .map { it.preSootPct - it.postSootPct!! }
            .average().let { if (it.isNaN()) 0.0 else it }

        val sessionRows = sessions.joinToString("") { s ->
            val start   = dateFormat.format(Date(s.startTimestamp))
            val sootPre = if (s.preSootPct >= 0) "${"%.0f".format(s.preSootPct)}%" else "—"
            val sootPost= s.postSootPct?.let { "${"%.0f".format(it)}%" } ?: "—"
            val peakEgt = if (s.peakEgtC > 0) "${"%.0f".format(s.peakEgtC)} °C" else "—"
            val dur     = s.durationMinutes?.let { "$it min" } ?: "—"
            val odoPre  = if (s.preOdometerKm >= 0) "%,d km".format(s.preOdometerKm) else "—"
            val coolant = if (s.preCoolantC >= 0) "${"%.0f".format(s.preCoolantC)} °C" else "—"
            val deltaPre= if (s.preDeltaPKpa >= 0) "${"%.2f".format(s.preDeltaPKpa)} kPa" else "—"
            val deltaPost= s.postSootPct?.let { _ -> "${"%.2f".format(s.peakDeltaPKpa)} kPa" } ?: "—"
            val resultBadge = when (s.result) {
                "COMPLETED"   -> "<span class=\"ok\">✓ Completata</span>"
                "INTERRUPTED" -> "<span class=\"warn\">⚠ Interrotta</span>"
                "IN_PROGRESS" -> "<span class=\"info\">⟳ In corso</span>"
                else          -> s.result
            }
            val rowClass = if (s.result == "INTERRUPTED") " class=\"interrupted-row\"" else ""

            """
            <tr$rowClass>
              <td>$start</td>
              <td>$odoPre</td>
              <td>$dur</td>
              <td>$sootPre → $sootPost</td>
              <td>$peakEgt</td>
              <td>$deltaPre → $deltaPost</td>
              <td>$coolant</td>
              <td>$resultBadge</td>
            </tr>
            """.trimIndent()
        }

        return """
<!DOCTYPE html>
<html lang="it">
<head>
<meta charset="UTF-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<title>DPF Monitor — Report Rigenerazioni</title>
<style>
  body { font-family: 'Segoe UI', Arial, sans-serif; color: #1a1a2e; max-width: 900px; margin: 0 auto; padding: 24px; line-height: 1.5; }
  h1 { font-size: 24px; color: #0d1b4e; border-bottom: 3px solid #4F8EF7; padding-bottom: 10px; margin-bottom: 20px; }
  h2 { font-size: 16px; color: #0d1b4e; margin: 28px 0 10px; border-left: 4px solid #4F8EF7; padding-left: 10px; }
  .header-block { display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 16px; margin-bottom: 24px; }
  .vehicle-box { background: #f0f4ff; border: 1px solid #c5d3f0; border-radius: 10px; padding: 16px 20px; flex: 1; min-width: 240px; }
  .vehicle-box p { margin: 4px 0; font-size: 14px; }
  .vehicle-box b { color: #0d1b4e; }
  .summary-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 12px; margin-bottom: 28px; }
  .stat { background: #f8f9ff; border: 1px solid #dde3f5; border-radius: 10px; padding: 14px; text-align: center; }
  .stat-val { font-size: 28px; font-weight: 800; color: #4F8EF7; letter-spacing: -0.5px; }
  .stat-lbl { font-size: 11px; color: #6e7a9f; text-transform: uppercase; letter-spacing: 0.8px; margin-top: 4px; }
  table { width: 100%; border-collapse: collapse; font-size: 13px; margin-bottom: 32px; }
  thead th { background: #0d1b4e; color: #fff; padding: 10px 12px; text-align: left; font-weight: 600; }
  tbody td { padding: 9px 12px; border-bottom: 1px solid #edf0f8; vertical-align: middle; }
  tbody tr:nth-child(even) { background: #f8f9ff; }
  tbody tr:hover { background: #eef2ff; }
  .interrupted-row td { opacity: 0.7; }
  .ok   { color: #1a7f37; font-weight: 700; }
  .warn { color: #b35c00; font-weight: 700; }
  .info { color: #0550ae; font-weight: 700; }
  .note-box { background: #fff8e1; border: 1px solid #ffe082; border-radius: 8px; padding: 14px 18px; font-size: 13px; color: #5d4037; margin-bottom: 24px; }
  .note-box b { color: #3e2723; }
  .footer { margin-top: 32px; padding-top: 16px; border-top: 1px solid #dde3f5; font-size: 11px; color: #9eabc5; }
  @media print { body { max-width: 100%; padding: 10px; } }
</style>
</head>
<body>

<h1>📋 DPF Monitor — Report Rigenerazioni</h1>

<div class="header-block">
  <div class="vehicle-box">
    <p><b>Veicolo:</b> $vehicleInfo</p>
    <p><b>Centralina:</b> EDC17C70 (Bosch)</p>
    <p><b>OBD Dongle:</b> Android-Vlink (BLE/SPP)</p>
    <p><b>Report generato:</b> $genDate</p>
    <p><b>App:</b> DPF Monitor v2.2</p>
  </div>
</div>

<h2>Riepilogo</h2>
<div class="summary-grid">
  <div class="stat"><div class="stat-val">${sessions.size}</div><div class="stat-lbl">Regen totali</div></div>
  <div class="stat"><div class="stat-val">$completed</div><div class="stat-lbl">Completate</div></div>
  <div class="stat"><div class="stat-val">$interrupted</div><div class="stat-lbl">Interrotte</div></div>
  <div class="stat"><div class="stat-val">${avgDuration} min</div><div class="stat-lbl">Durata media</div></div>
  <div class="stat"><div class="stat-val">${"%.1f".format(avgSootReduction)}%</div><div class="stat-lbl">Riduz. soot media</div></div>
</div>

<div class="note-box">
  <b>Note per il meccanico:</b><br>
  • <b>Soot %</b>: livello particolato nel DPF (0–320%). Sopra 100% la centralina avvia la rigenerazione attiva, sopra 320% il DPF va sostituito.<br>
  • <b>EGT Picco</b>: temperatura massima raggiunta dai gas di scarico durante la regen. Range normale: 500–650 °C. Sopra 700 °C verificare iniettori.<br>
  • <b>Delta P</b>: pressione differenziale DPF (monte – valle). Valori elevati a fine regen indicano cenere residua (non eliminabile con la regen).<br>
  • <b>Regen interrotta</b>: il motore è stato spento durante la rigenerazione. Il soot non si è ridotto — valutare rigenerazione forzata con Ford IDS.
</div>

<h2>Storico rigenerazioni</h2>
<table>
  <thead>
    <tr>
      <th>Data e ora</th>
      <th>Odometro</th>
      <th>Durata</th>
      <th>Soot (prima → dopo)</th>
      <th>EGT picco</th>
      <th>Delta P (inizio → fine)</th>
      <th>Coolant</th>
      <th>Risultato</th>
    </tr>
  </thead>
  <tbody>
    $sessionRows
  </tbody>
</table>

<div class="footer">
  Report generato automaticamente da DPF Monitor v2.2 · Ford Focus 1.5 TDCi EDC17C70<br>
  I dati provengono dalla centralina motore tramite protocollo OBD2 (ISO 15765-4 CAN, ATSH7E0).
</div>

</body>
</html>
        """.trimIndent()
    }
}
