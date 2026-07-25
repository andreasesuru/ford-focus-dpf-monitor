package com.example.fordfocusdpfscan.ui

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fordfocusdpfscan.R
import com.example.fordfocusdpfscan.data.DpfRepository
import com.example.fordfocusdpfscan.data.MaintenanceRepository
import com.example.fordfocusdpfscan.data.RegenHistoryRepository
import com.example.fordfocusdpfscan.data.ai.AiMessage
import com.example.fordfocusdpfscan.data.ai.AiPrompts
import com.example.fordfocusdpfscan.data.ai.AiSettings
import com.example.fordfocusdpfscan.data.ai.ClaudeClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ═══════════════════════════════════════════════════════════════════════════════
// AiChatActivity.kt — Free-form diagnostic assistant.
//
// Builds a one-time context snapshot (live data + regen history + maintenance),
// puts it in the system prompt, and runs a multi-turn conversation with Claude.
// ═══════════════════════════════════════════════════════════════════════════════

class AiChatActivity : AppCompatActivity() {

    private lateinit var scroll: ScrollView
    private lateinit var container: LinearLayout
    private lateinit var etPrompt: EditText
    private lateinit var btnSend: Button

    private lateinit var settings: AiSettings
    private lateinit var client: ClaudeClient

    private val messages = mutableListOf<AiMessage>()
    private var systemPrompt: String = ""
    private var busy = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_chat)
        scroll    = findViewById(R.id.scrollChat)
        container = findViewById(R.id.messagesContainer)
        etPrompt  = findViewById(R.id.etPrompt)
        btnSend   = findViewById(R.id.btnSend)

        settings = AiSettings(this)
        client   = ClaudeClient(settings)

        btnSend.setOnClickListener { send() }
        setBusy(true)
        addBubble("Preparo il contesto dell'auto…", isUser = false)

        lifecycleScope.launch {
            val ctx = withContext(Dispatchers.IO) { runCatching { buildContext() }.getOrDefault("(dati non disponibili)") }
            systemPrompt = AiPrompts.assistantSystem(ctx)
            container.removeAllViews()
            addBubble(
                "Ciao! Sono l'assistente della tua Focus 1.5 TDCi. Ho letto i dati attuali e lo storico. " +
                "Chiedimi pure — es. \"come sta il DPF?\", \"perché tante regen interrotte?\", \"quando cambio l'olio?\".",
                isUser = false
            )
            if (!settings.hasKey) {
                addBubble("⚠️ Nessuna chiave API impostata. Aprila da «Impostazioni AI» per farmi rispondere.", isUser = false)
            }
            setBusy(false)
        }
    }

    private fun send() {
        if (busy) return
        val text = etPrompt.text?.toString()?.trim().orEmpty()
        if (text.isEmpty()) return
        etPrompt.setText("")
        addBubble(text, isUser = true)
        messages.add(AiMessage("user", text))

        setBusy(true)
        lifecycleScope.launch {
            val result = client.chat(systemPrompt, messages)
            setBusy(false)
            result
                .onSuccess {
                    messages.add(AiMessage("assistant", it))
                    addBubble(it, isUser = false)
                }
                .onFailure {
                    // Drop the un-answered user turn so the next send isn't malformed.
                    if (messages.isNotEmpty() && messages.last().role == "user") messages.removeAt(messages.size - 1)
                    addBubble("Errore: ${it.message}", isUser = false)
                }
        }
    }

    private suspend fun buildContext(): String {
        val d = DpfRepository.dpfData.value
        fun f(v: Float, u: String) = if (v >= 0f) "${v.toInt()}$u" else "n/d"

        val sb = StringBuilder()
        sb.append("DPF: soot ${f(d.sootPercentage, "%")}, load ${f(d.loadPercentage, "%")}, deltaP ")
        sb.append(if (d.dpfDeltaPressureKpa >= 0f) "%.1f kPa".format(d.dpfDeltaPressureKpa) else "n/d")
        sb.append(", EGT ingresso ${f(d.egtCelsius, "°C")}, EGT uscita ${f(d.egtPostDpfC, "°C")}.\n")
        sb.append("Motore: refrigerante ${f(d.coolantTempC, "°C")}, RPM ${f(d.rpmValue, "")}, ")
        sb.append("velocità ${f(d.speedKmh, " km/h")}, batteria ")
        sb.append(if (d.batteryVoltage >= 0f) "%.1f V".format(d.batteryVoltage) else "n/d").append(".\n")
        sb.append("Distanze: odometro ${d.odometerKm} km, km da ultima regen ${d.kmSinceLastRegen}, ")
        sb.append("km da cambio olio ${d.kmSinceOilChange}.\n")
        sb.append("Stato regen attuale: ${d.regenStatus}.\n\n")

        sb.append("STORICO REGEN:\n")
        sb.append(RegenHistoryRepository(this).aiHistorySummary()).append("\n\n")

        val reminders = MaintenanceRepository(this).getAll()
        if (reminders.isNotEmpty()) {
            sb.append("MANUTENZIONE:\n")
            reminders.forEach { r ->
                sb.append("- ${r.title}: intervallo ${r.intervalKm} km, fatto a ${r.lastDoneKm} km")
                if (d.odometerKm > 0L) sb.append(", mancano ${r.kmRemaining(d.odometerKm)} km")
                sb.append("\n")
            }
        }
        return sb.toString().trim()
    }

    // ── UI helpers ─────────────────────────────────────────────────────────────

    private fun setBusy(b: Boolean) {
        busy = b
        btnSend.isEnabled = !b
        btnSend.text = if (b) "…" else "Invia"
    }

    private fun addBubble(text: String, isUser: Boolean) {
        val tv = TextView(this).apply {
            this.text = text
            setTextColor(0xFFE6EDF5.toInt())
            textSize = 14f
            setPadding(dp(12), dp(10), dp(12), dp(10))
            setBackgroundColor(if (isUser) 0xFF10375C.toInt() else 0xFF12203A.toInt())
        }
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(dp(if (isUser) 40 else 4), dp(4), dp(if (isUser) 4 else 40), dp(4))
            gravity = if (isUser) Gravity.END else Gravity.START
        }
        tv.layoutParams = lp
        container.addView(tv)
        scroll.post { scroll.fullScroll(View.FOCUS_DOWN) }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
