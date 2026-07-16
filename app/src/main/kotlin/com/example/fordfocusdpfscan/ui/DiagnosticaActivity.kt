package com.example.fordfocusdpfscan.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.fordfocusdpfscan.R
import com.example.fordfocusdpfscan.data.DpfData
import com.example.fordfocusdpfscan.data.DpfRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// ─── Note on connection banner ────────────────────────────────────────────────
// activity_diagnostica.xml contains a tvNotConnectedBanner TextView (id:
// tvNotConnectedBanner) that is VISIBLE when BLE is disconnected and GONE once
// data starts arriving.  This avoids the confusing "all dashes" empty state.
// ─────────────────────────────────────────────────────────────────────────────

// ═══════════════════════════════════════════════════════════════════════════════
// DiagnosticaActivity.kt — "📡 Diagnostica" tab.
//
// Displays live engine sensor data from the OBD2 dongle in a grid layout.
// Observes DpfRepository.dpfData and updates all cells in real-time.
//
// Cell layout: each `<include layout="@layout/item_diag_cell">` has:
//   tvCellLabel  — sensor name (uppercase, muted)
//   tvCellValue  — live numeric value (large bold)
//   tvCellUnit   — unit string (small, muted)
//   tvCellPid    — PID reference (tiny, hint color)
// ═══════════════════════════════════════════════════════════════════════════════

class DiagnosticaActivity : BaseTabActivity() {

    override val tabIndex = 1

    // ── Cell handle ──────────────────────────────────────────────────────────
    private inner class Cell(root: View) {
        val label: TextView = root.findViewById(R.id.tvCellLabel)
        val value: TextView = root.findViewById(R.id.tvCellValue)
        val unit:  TextView = root.findViewById(R.id.tvCellUnit)
        val hint:  TextView = root.findViewById(R.id.tvCellHint)
        val bar:   View     = root.findViewById(R.id.vCellStatusBar)

        fun setup(labelText: String, unitText: String, hintText: String) {
            label.text = labelText
            unit.text  = unitText
            hint.text  = hintText
            value.text = "—"
            bar.setBackgroundColor(0xFF1E2A3A.toInt())   // default muted
        }

        fun set(v: Float, decimals: Int = 0, pendingText: String = "—") {
            value.text = if (v < 0f) pendingText
                         else "%.${decimals}f".format(v)
        }

        fun setLong(v: Long, pendingText: String = "—") {
            value.text = if (v < 0L) pendingText else "%,d".format(v)
        }

        /** Colors both the value text and the bottom status bar. */
        fun setColor(color: Int) {
            value.setTextColor(color)
            bar.setBackgroundColor(color)
        }
    }

    // ── Cell references (bound after setContentView) ──────────────────────────
    // DPF Avanzato
    private lateinit var cSoot:     Cell
    private lateinit var cLoad:     Cell
    private lateinit var cDeltaP:   Cell
    private lateinit var cEgtPre:   Cell
    private lateinit var cEgtPost:  Cell
    // Motore Live
    private lateinit var cRpm:      Cell
    private lateinit var cSpeed:    Cell
    private lateinit var cEngLoad:  Cell
    private lateinit var cBoost:    Cell
    private lateinit var cCoolant:  Cell
    // Alimentazione / Elettrico
    private lateinit var cBattery:   Cell
    private lateinit var cRail:      Cell
    private lateinit var cIntakeAir: Cell
    // Distanze
    private lateinit var cKmRegen:  Cell
    private lateinit var cKmOil:    Cell
    private lateinit var cOdometer: Cell

    // ── Connection banner ─────────────────────────────────────────────────────
    private lateinit var notConnectedBanner: View

    // ── Colors ────────────────────────────────────────────────────────────────
    private val colorPrimary by lazy  { getColor(R.color.text_primary)  }
    private val colorOk      by lazy  { 0xFF34C759.toInt() }   // green
    private val colorWarn    by lazy  { 0xFFFF9F0A.toInt() }   // amber
    private val colorDanger  by lazy  { 0xFFFF3B30.toInt() }   // red
    private val colorMuted   by lazy  { getColor(R.color.text_secondary) }

    // ═════════════════════════════════════════════════════════════════════════
    // Lifecycle
    // ═════════════════════════════════════════════════════════════════════════

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnostica)
        notConnectedBanner = findViewById(R.id.tvNotConnectedBanner)
        bindCells()
        setupLabels()
        observeData()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Cell binding
    // ═════════════════════════════════════════════════════════════════════════

    private fun bindCells() {
        cSoot     = Cell(findViewById(R.id.cellSoot))
        cLoad     = Cell(findViewById(R.id.cellLoad))
        cDeltaP   = Cell(findViewById(R.id.cellDeltaP))
        cEgtPre   = Cell(findViewById(R.id.cellEgtPre))
        cEgtPost  = Cell(findViewById(R.id.cellEgtPost))

        cRpm      = Cell(findViewById(R.id.cellRpm))
        cSpeed    = Cell(findViewById(R.id.cellSpeed))
        cEngLoad  = Cell(findViewById(R.id.cellEngineLoad))
        cBoost    = Cell(findViewById(R.id.cellBoost))
        cCoolant  = Cell(findViewById(R.id.cellCoolant))

        cBattery   = Cell(findViewById(R.id.cellBattery))
        cRail      = Cell(findViewById(R.id.cellRail))
        cIntakeAir = Cell(findViewById(R.id.cellIntakeAir))

        cKmRegen  = Cell(findViewById(R.id.cellKmRegen))
        cKmOil    = Cell(findViewById(R.id.cellKmOil))
        cOdometer = Cell(findViewById(R.id.cellOdometer))
    }

    private fun setupLabels() {
        cSoot    .setup("SOOT",          "%",    "normale < 50% · critico > 80%")
        cLoad    .setup("LOAD DPF",      "%",    "normale < 50% · critico > 80%")
        cDeltaP  .setup("DELTA P",       "kPa",  "idle ~0 · marcia 5-15 kPa")
        cEgtPre  .setup("EGT INGRESSO",  "°C",   "normale < 500°C · regen 600-700°C")
        cEgtPost .setup("EGT USCITA",    "°C",   "secondo sensore EGT di scarico")

        cRpm    .setup("RPM",            "rpm",  "minimo ~800 · normale 1.000-3.000")
        cSpeed  .setup("VELOCITÀ",       "km/h", "")
        cEngLoad.setup("CARICO MOTORE",  "%",    "normale < 60% · alto > 85%")
        cBoost  .setup("BOOST",          "bar",  "normale 0-1,5 bar")
        cCoolant.setup("REFRIGERANTE",   "°C",   "ottimale 70-110°C")

        cBattery  .setup("BATTERIA",       "V",   "acceso 13,8-14,4 V · debole < 12 V")
        cRail     .setup("PRESSIONE RAIL", "bar", "minimo ~250 · pieno carico ~1.600")
        cIntakeAir.setup("TEMP ARIA",      "°C",  "aria aspirata al collettore")

        cKmRegen .setup("KM ALLA REGEN", "km",   "stima ECU · scende guidando · ~0 = regen vicina")
        cKmOil   .setup("KM TAGLIANDO",  "km",   "cambio olio ogni 10-12.000 km")
        cOdometer.setup("ODOMETRO",      "km",   "chilometri totali dalla ECU")
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Data observation
    // ═════════════════════════════════════════════════════════════════════════

    private fun observeData() {
        lifecycleScope.launch {
            DpfRepository.dpfData.collectLatest { data ->
                notConnectedBanner.visibility =
                    if (data.bleConnected) View.GONE else View.VISIBLE
                updateDpfSection(data)
                updateEngineSection(data)
                updateElectricalSection(data)
                updateDistanceSection(data)
            }
        }
    }

    private fun updateDpfSection(d: DpfData) {
        // Soot %
        cSoot.set(d.sootPercentage, 0)
        cSoot.setColor(when {
            d.sootPercentage < 0f   -> colorMuted
            d.sootPercentage < 50f  -> colorOk
            d.sootPercentage < 80f  -> colorWarn
            else                    -> colorDanger
        })

        // Load %
        cLoad.set(d.loadPercentage, 0)
        cLoad.setColor(when {
            d.loadPercentage < 0f   -> colorMuted
            d.loadPercentage < 50f  -> colorOk
            d.loadPercentage < 80f  -> colorWarn
            else                    -> colorDanger
        })

        // Delta P
        cDeltaP.set(d.dpfDeltaPressureKpa, 1)
        cDeltaP.setColor(when {
            d.dpfDeltaPressureKpa < 0f   -> colorMuted
            d.dpfDeltaPressureKpa < 5f   -> colorOk
            d.dpfDeltaPressureKpa < 15f  -> colorWarn
            else                         -> colorDanger
        })

        // EGT pre-DPF
        cEgtPre.set(d.egtCelsius, 0)
        cEgtPre.setColor(when {
            d.egtCelsius < 0f    -> colorMuted
            d.egtCelsius < 500f  -> colorOk
            d.egtCelsius < 700f  -> colorWarn
            else                 -> colorDanger
        })

        // EGT post-DPF
        cEgtPost.set(d.egtPostDpfC, 0)
        cEgtPost.setColor(when {
            d.egtPostDpfC < 0f   -> colorMuted
            d.egtPostDpfC < 500f -> colorOk
            d.egtPostDpfC < 700f -> colorWarn
            else                 -> colorDanger
        })

    }

    private fun updateEngineSection(d: DpfData) {
        // RPM
        cRpm.set(d.rpmValue, 0)
        cRpm.setColor(if (d.rpmValue < 0f) colorMuted else colorPrimary)

        // Speed
        cSpeed.set(d.speedKmh, 0)
        cSpeed.setColor(if (d.speedKmh < 0f) colorMuted else colorPrimary)

        // Engine load %
        cEngLoad.set(d.engineLoadPct, 0)
        cEngLoad.setColor(when {
            d.engineLoadPct < 0f   -> colorMuted
            d.engineLoadPct < 60f  -> colorOk
            d.engineLoadPct < 85f  -> colorWarn
            else                   -> colorDanger
        })

        // Boost: MAP (absolute) - baro = relative boost in kPa → bar
        if (d.intakeMapKpa >= 0f) {
            val baro  = if (d.baroKpa > 0f) d.baroKpa else 101f   // default 1 atm
            val boost = (d.intakeMapKpa - baro) / 100f             // kPa → bar
            cBoost.value.text = "%.2f".format(boost.coerceAtLeast(0f))
            cBoost.setColor(when {
                boost < 0.3f  -> colorOk
                boost < 1.5f  -> colorOk
                boost < 2.0f  -> colorWarn
                else          -> colorDanger
            })
        } else {
            cBoost.value.text = "—"
            cBoost.setColor(colorMuted)
        }

        // Coolant
        cCoolant.set(d.coolantTempC, 0)
        cCoolant.setColor(when {
            d.coolantTempC < 0f   -> colorMuted
            d.coolantTempC < 70f  -> colorWarn   // engine cold
            d.coolantTempC < 110f -> colorOk
            else                  -> colorDanger
        })

    }

    private fun updateElectricalSection(d: DpfData) {
        // Battery / charging voltage
        cBattery.set(d.batteryVoltage, 1)
        cBattery.setColor(when {
            d.batteryVoltage < 0f     -> colorMuted
            d.batteryVoltage < 12.0f  -> colorDanger   // scarica / non in carica
            d.batteryVoltage < 13.0f  -> colorWarn
            d.batteryVoltage <= 15.0f -> colorOk
            else                      -> colorWarn      // sovraccarica
        })

        // Fuel rail pressure — stored in kPa, shown in bar
        if (d.fuelRailPressureKpa >= 0f) {
            cRail.value.text = "%.0f".format(d.fuelRailPressureKpa / 100f)
            cRail.setColor(colorPrimary)
        } else {
            cRail.value.text = "—"
            cRail.setColor(colorMuted)
        }

        // Intake air temperature
        cIntakeAir.set(d.intakeAirTempC, 0)
        cIntakeAir.setColor(if (d.intakeAirTempC < 0f) colorMuted else colorPrimary)
    }

    private fun updateDistanceSection(d: DpfData) {
        // 22 050B is a live ECU countdown toward the next regen (falls as you drive,
        // resets up after a regen) — so low = regen imminent, not "overdue".
        cKmRegen.setLong(d.kmSinceLastRegen)
        cKmRegen.setColor(when {
            d.kmSinceLastRegen < 0L   -> colorMuted
            d.kmSinceLastRegen < 50L  -> colorWarn   // regen imminente
            else                      -> colorOk
        })

        cKmOil.setLong(d.kmSinceOilChange)
        cKmOil.setColor(when {
            d.kmSinceOilChange < 0L     -> colorMuted
            d.kmSinceOilChange < 9000L  -> colorOk
            d.kmSinceOilChange < 11000L -> colorWarn   // 9k–11k: avviso imminente
            else                        -> colorDanger  // >11k: scaduto
        })

        cOdometer.setLong(d.odometerKm)
        cOdometer.setColor(if (d.odometerKm < 0L) colorMuted else colorPrimary)
    }

    // Tab bar highlighting + click listeners are handled centrally by BaseTabActivity.
}
