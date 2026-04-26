package com.example.fordfocusdpfscan.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fordfocusdpfscan.R
import com.example.fordfocusdpfscan.data.DpfData
import com.example.fordfocusdpfscan.data.DpfRepository
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
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

class DiagnosticaActivity : AppCompatActivity() {

    // ── Cell handle ──────────────────────────────────────────────────────────
    private inner class Cell(root: View) {
        val label: TextView = root.findViewById(R.id.tvCellLabel)
        val value: TextView = root.findViewById(R.id.tvCellValue)
        val unit:  TextView = root.findViewById(R.id.tvCellUnit)
        val pid:   TextView = root.findViewById(R.id.tvCellPid)

        fun setup(labelText: String, unitText: String, pidText: String) {
            label.text = labelText
            unit.text  = unitText
            pid.text   = pidText
            value.text = "—"
        }

        fun set(v: Float, decimals: Int = 0, pendingText: String = "—") {
            value.text = if (v < 0f) pendingText
                         else "%.*f".format(decimals, v)
        }

        fun setLong(v: Long, pendingText: String = "—") {
            value.text = if (v < 0L) pendingText else "%,d".format(v)
        }

        fun setColor(color: Int) { value.setTextColor(color) }
    }

    // ── Cell references (bound after setContentView) ──────────────────────────
    // DPF Avanzato
    private lateinit var cSoot:     Cell
    private lateinit var cLoad:     Cell
    private lateinit var cDeltaP:   Cell
    private lateinit var cEgtPre:   Cell
    private lateinit var cEgtPost:  Cell
    private lateinit var cDeltaEgt: Cell
    // Motore Live
    private lateinit var cRpm:      Cell
    private lateinit var cSpeed:    Cell
    private lateinit var cEngLoad:  Cell
    private lateinit var cBoost:    Cell
    private lateinit var cCoolant:  Cell
    private lateinit var cOilTemp:  Cell
    // Distanze
    private lateinit var cKmRegen:  Cell
    private lateinit var cKmOil:    Cell
    private lateinit var cOdometer: Cell
    private lateinit var cBaro:     Cell
    // In attesa
    private lateinit var cAsh:      Cell
    private lateinit var cEgr:      Cell
    private lateinit var cRegenCnt: Cell
    private lateinit var cFuelDil:  Cell

    // ── Connection banner ─────────────────────────────────────────────────────
    private lateinit var notConnectedBanner: View

    // ── Live chart ────────────────────────────────────────────────────────────
    private lateinit var liveChart: LineChart
    private val egtBuffer  = ArrayDeque<Float>()   // EGT °C — last 40 readings
    private val loadBuffer = ArrayDeque<Float>()   // Engine load % — last 40 readings
    private val BUFFER_MAX = 40

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
        liveChart          = findViewById(R.id.chartLiveSensors)
        bindCells()
        setupLabels()
        setupLiveChart()
        setupTabBar()
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
        cDeltaEgt = Cell(findViewById(R.id.cellDeltaEgt))

        cRpm      = Cell(findViewById(R.id.cellRpm))
        cSpeed    = Cell(findViewById(R.id.cellSpeed))
        cEngLoad  = Cell(findViewById(R.id.cellEngineLoad))
        cBoost    = Cell(findViewById(R.id.cellBoost))
        cCoolant  = Cell(findViewById(R.id.cellCoolant))
        cOilTemp  = Cell(findViewById(R.id.cellOilTemp))

        cKmRegen  = Cell(findViewById(R.id.cellKmRegen))
        cKmOil    = Cell(findViewById(R.id.cellKmOil))
        cOdometer = Cell(findViewById(R.id.cellOdometer))
        cBaro     = Cell(findViewById(R.id.cellBaro))

        cAsh      = Cell(findViewById(R.id.cellAshLevel))
        cEgr      = Cell(findViewById(R.id.cellEgr))
        cRegenCnt = Cell(findViewById(R.id.cellRegenCounter))
        cFuelDil  = Cell(findViewById(R.id.cellFuelDilution))
    }

    private fun setupLabels() {
        cSoot    .setup("SOOT %",        "%",    "PID 22 057B")
        cLoad    .setup("LOAD %",         "%",   "PID 22 0579")
        cDeltaP  .setup("DELTA P",        "kPa", "PID 01 7A")
        cEgtPre  .setup("EGT INGRESSO",   "°C",  "PID 01 78 S1")
        cEgtPost .setup("EGT USCITA",     "°C",  "PID 01 78 S2")
        cDeltaEgt.setup("ΔT EGT",         "°C",  "Pre − Post")

        cRpm    .setup("RPM",             "rpm", "PID 01 0C")
        cSpeed  .setup("VELOCITÀ",        "km/h","PID 01 0D")
        cEngLoad.setup("ENGINE LOAD",     "%",   "PID 01 04")
        cBoost  .setup("BOOST",           "bar", "PID 01 0B")
        cCoolant.setup("REFRIGERANTE",    "°C",  "PID 01 05")
        cOilTemp.setup("OLIO",            "°C",  "PID 01 5C")

        cKmRegen .setup("KM DA REGEN",    "km",  "PID 22 050B")
        cKmOil   .setup("KM DA TAGLIANDO","km",  "PID 22 0542")
        cOdometer.setup("ODOMETRO",       "km",  "PID 22 DD01")
        cBaro    .setup("PRESSIONE BARO", "kPa", "PID 01 33")

        cAsh    .setup("ASH LEVEL",       "%",   "PID 22 0578")
        cEgr    .setup("EGR RATE",        "%",   "PID 22 0583")
        cRegenCnt.setup("TOT. REGEN",     "",    "PID 22 05xx")
        cFuelDil.setup("FUEL IN OIL",     "%",   "PID 22 05xx")
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Live chart
    // ═════════════════════════════════════════════════════════════════════════

    private fun setupLiveChart() {
        liveChart.apply {
            description.isEnabled  = false
            legend.isEnabled       = false   // custom legend in XML
            setTouchEnabled(false)
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            setNoDataText("In attesa dati OBD2…")
            setNoDataTextColor(0xFF7A8FA6.toInt())

            // X axis — just a time index, no labels
            xAxis.isEnabled = false

            // Left Y axis — EGT °C  (0–800)
            axisLeft.apply {
                textColor    = 0xFF7A8FA6.toInt()
                textSize     = 9f
                axisMinimum  = 0f
                axisMaximum  = 800f
                setDrawGridLines(true)
                gridColor    = 0xFF1A1A2A.toInt()
                gridLineWidth = 0.5f
                setLabelCount(5, true)
            }

            // Right Y axis — Load %  (0–100)
            axisRight.apply {
                isEnabled    = true
                textColor    = 0xFF7A8FA6.toInt()
                textSize     = 9f
                axisMinimum  = 0f
                axisMaximum  = 100f
                setDrawGridLines(false)
                setLabelCount(5, true)
            }
        }
    }

    private fun updateLiveChart(data: DpfData) {
        // Append new readings to rolling buffers
        if (data.egtCelsius >= 0f) {
            if (egtBuffer.size >= BUFFER_MAX) egtBuffer.removeFirst()
            egtBuffer.addLast(data.egtCelsius)
        }
        if (data.engineLoadPct >= 0f) {
            if (loadBuffer.size >= BUFFER_MAX) loadBuffer.removeFirst()
            loadBuffer.addLast(data.engineLoadPct)
        }

        if (egtBuffer.isEmpty()) return

        // EGT line — left Y axis, orange
        val egtSet = LineDataSet(
            egtBuffer.mapIndexed { i, v -> Entry(i.toFloat(), v) }, "EGT"
        ).apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color          = 0xFFFF9500.toInt()
            lineWidth      = 2f
            setDrawCircles(false)
            setDrawValues(false)
            mode           = LineDataSet.Mode.CUBIC_BEZIER
            fillAlpha      = 40
            fillColor      = 0xFFFF9500.toInt()
            setDrawFilled(true)
        }

        // Engine load line — right Y axis, blue
        val loadSet = LineDataSet(
            loadBuffer.mapIndexed { i, v -> Entry(i.toFloat(), v) }, "Load"
        ).apply {
            axisDependency = YAxis.AxisDependency.RIGHT
            color          = 0xFF4499DD.toInt()
            lineWidth      = 1.5f
            setDrawCircles(false)
            setDrawValues(false)
            mode           = LineDataSet.Mode.CUBIC_BEZIER
        }

        liveChart.data = LineData(egtSet, loadSet)
        liveChart.notifyDataSetChanged()
        liveChart.invalidate()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Data observation
    // ═════════════════════════════════════════════════════════════════════════

    private fun observeData() {
        lifecycleScope.launch {
            DpfRepository.dpfData.collectLatest { data ->
                // Show/hide "not connected" banner
                notConnectedBanner.visibility =
                    if (data.bleConnected) View.GONE else View.VISIBLE

                updateLiveChart(data)
                updateDpfSection(data)
                updateEngineSection(data)
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

        // ΔT EGT = pre − post.
        // delta > 0 → pre > post: normal (gas cools slightly through DPF housing).
        // delta < 0 → post > pre: DPF generating heat = regen in progress.
        // delta < −50 → strong active regen burn.
        if (d.egtCelsius >= 0f && d.egtPostDpfC >= 0f) {
            val delta = d.egtCelsius - d.egtPostDpfC
            cDeltaEgt.value.text = "%+.0f".format(delta)
            cDeltaEgt.setColor(when {
                delta >= 0f    -> colorOk      // pre ≥ post: normal cooling
                delta >= -50f  -> colorWarn    // post slightly hotter: regen warming
                else           -> colorDanger  // post >> pre: active regen burn
            })
        } else {
            cDeltaEgt.value.text = "—"
            cDeltaEgt.setColor(colorMuted)
        }
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

        // Oil temp
        cOilTemp.set(d.oilTempC, 0)
        cOilTemp.setColor(when {
            d.oilTempC < 0f   -> colorMuted
            d.oilTempC < 80f  -> colorWarn
            d.oilTempC < 115f -> colorOk
            else              -> colorDanger
        })
    }

    private fun updateDistanceSection(d: DpfData) {
        cKmRegen.setLong(d.kmSinceLastRegen)
        cKmRegen.setColor(when {
            d.kmSinceLastRegen < 0L    -> colorMuted
            d.kmSinceLastRegen < 500L  -> colorOk
            d.kmSinceLastRegen < 800L  -> colorWarn
            else                       -> colorDanger
        })

        cKmOil.setLong(d.kmSinceOilChange)
        cKmOil.setColor(when {
            d.kmSinceOilChange < 0L     -> colorMuted
            d.kmSinceOilChange < 8000L  -> colorOk
            d.kmSinceOilChange < 10000L -> colorWarn
            else                        -> colorDanger
        })

        cOdometer.setLong(d.odometerKm)
        cOdometer.setColor(if (d.odometerKm < 0L) colorMuted else colorPrimary)

        cBaro.set(d.baroKpa, 0)
        cBaro.setColor(if (d.baroKpa < 0f) colorMuted else colorPrimary)
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Tab bar navigation
    // ═════════════════════════════════════════════════════════════════════════

    private fun setupTabBar() {
        // Mark this tab as active
        findViewById<TextView>(R.id.tabDiagnostica).apply {
            setBackgroundResource(R.drawable.bg_tab_active)
            setTextColor(getColor(R.color.text_primary))
        }

        // Navigate to other tabs
        findViewById<View>(R.id.tabMonitor).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
        findViewById<View>(R.id.tabStorico).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }
}
