package com.example.fordfocusdpfscan.data

// ═══════════════════════════════════════════════════════════════════════════════
// DpfData.kt — Data models and enumerations shared across the entire app.
//
// These are plain data holders with NO Android dependencies, making them easy
// to unit-test and reuse between the phone UI and the Android Auto screen.
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Snapshot of all DPF-related values read from the OBD2 dongle.
 * Emitted by [DpfRepository] as a [kotlinx.coroutines.flow.StateFlow].
 *
 * Default values of -1f signal "no data received yet" — the UI renders "– –".
 */
data class DpfData(

    // ── DPF particulate filter ────────────────────────────────────────────────
    /** DPF soot % (open-loop, combustion model estimate).
     *  Derived from PID 22 057B. Raw integer = % directly.
     *  Scale: 100% = PCM triggers dynamic regen, 320% = DPF replacement needed.
     *  Confirmed on Ford Focus Mk3 1.5 TDCi EDC17C70. -1f = no data yet. */
    val sootPercentage: Float = -1f,

    /** DPF load % (closed-loop, from pressure sensor).
     *  Derived from PID 22 0579. Raw integer = % directly.
     *  Scale: 100% = PCM triggers dynamic regen, 320% = DPF replacement needed.
     *  Confirmed on Ford Focus Mk3 1.5 TDCi EDC17C70. -1f = no data yet. */
    val loadPercentage: Float = -1f,

    /** DPF differential pressure (upstream – downstream) in kPa.
     *  Derived from PID 01 7A (SAE J1979), bytes B–C: (256×B+C)/100 kPa.
     *  Confirmed working on Ford Focus Mk3 1.5 TDCi EDC17C70.
     *  Typical values: ~0 kPa at idle, 5–15 kPa at motorway cruise.
     *  High values (>20 kPa) suggest filter blockage. -1f = no data yet. */
    val dpfDeltaPressureKpa: Float = -1f,

    // ── Temperatures ─────────────────────────────────────────────────────────
    /** Engine coolant temperature in °C. PID 01 05. Range: typically 70–110 °C. */
    val coolantTempC: Float = -1f,

    /** Exhaust Gas Temperature (EGT) in °C.
     *  PID 22 XXXX — replace with the real Ford Focus PID found via ForScan.
     *  Range: 150–700 °C. Critical for regen detection (Strategy B). */
    val egtCelsius: Float = -1f,

    // ── Regeneration ─────────────────────────────────────────────────────────
    /** Current regeneration status, computed by [DpfRepository]. */
    val regenStatus: RegenStatus = RegenStatus.INACTIVE,

    /** Which detection strategy produced the current [regenStatus]. */
    val regenStrategy: RegenStrategy = RegenStrategy.NONE,

    // ── Distance counters (live from ECU) ─────────────────────────────────────
    /** Km travelled since the last DPF regeneration.
     *  Derived from PID 22 050B. Confirmed on Ford Focus Mk3 1.5 TDCi EDC17C70.
     *  Example: 411 km after last regen. -1L = no data yet. */
    val kmSinceLastRegen: Long = -1L,

    /** Km travelled since the last engine oil change.
     *  Derived from PID 22 0542. Confirmed exact match (8979 km).
     *  Reset by the mechanic via Ford IDS at every service. -1L = no data yet. */
    val kmSinceOilChange: Long = -1L,

    /** Current odometer reading in km. Derived from PID 22 DD01. -1L = no data yet. */
    val odometerKm: Long = -1L,

    // ── Live engine sensors (standard OBD2 Mode 01) ───────────────────────────
    /** Engine RPM. PID 01 0C. Formula: ((A*256)+B)/4. -1f = no data. */
    val rpmValue: Float = -1f,

    /** Vehicle speed in km/h. PID 01 0D. Formula: A. -1f = no data. */
    val speedKmh: Float = -1f,

    /** Engine load as % (0–100). PID 01 04. Formula: A*100/255. -1f = no data. */
    val engineLoadPct: Float = -1f,

    /** Intake manifold absolute pressure in kPa. PID 01 0B.
     *  Subtract barometric pressure to get boost above atmosphere.
     *  Idle: ~100 kPa (atmo). Cruising: 120–170 kPa. Full load: 180–220 kPa.
     *  -1f = no data. */
    val intakeMapKpa: Float = -1f,

    /** Barometric (ambient) pressure in kPa. PID 01 33.
     *  Typically 95–103 kPa at sea level. -1f = no data. */
    val baroKpa: Float = -1f,

    /** Engine oil temperature in °C. PID 01 5C. Formula: A-40.
     *  Warm: 90–110°C. -1f = no data. */
    val oilTempC: Float = -1f,

    /** EGT post-DPF sensor (downstream) in °C. Extracted from PID 01 78 sensor 2.
     *  Delta vs pre-DPF indicates DPF efficiency. -1f = no data or sensor absent. */
    val egtPostDpfC: Float = -1f,

    // ── BLE connection ────────────────────────────────────────────────────────
    /** True when the GATT connection to Android-Vlink is active and services
     *  have been discovered. */
    val bleConnected: Boolean = false
)

// ═══════════════════════════════════════════════════════════════════════════════
// RegenStatus — the four states visible on the Android Auto screen
// ═══════════════════════════════════════════════════════════════════════════════

enum class RegenStatus {

    /**
     * EGT < 450 °C and no ECU flag.
     * Safe to turn off the engine — no regeneration in progress.
     */
    INACTIVE,

    /**
     * EGT ≥ 450 °C (passive thermal regen may be occurring) OR ECU flag set.
     * DO NOT turn off the engine — could interrupt the process.
     * Notification: CarToast + heads-up with custom chime.
     */
    WARNING,

    /**
     * EGT ≥ 550 °C sustained for 20+ seconds, OR ECU direct flag = 0x01.
     * Active (forced) regeneration confirmed.
     * DO NOT turn off the engine — post-injection fuel is being used.
     * Notification: CarToast + heads-up with custom chime (highest priority).
     */
    ACTIVE,

    /**
     * EGT dropped back below 400 °C after a WARNING or ACTIVE phase,
     * indicating the cycle has ended successfully.
     * Safe to turn off the engine.
     * Notification: CarToast + heads-up with custom chime.
     */
    COMPLETED
}

// ═══════════════════════════════════════════════════════════════════════════════
// RegenStrategy — tracks which logic path produced the status
// ═══════════════════════════════════════════════════════════════════════════════

enum class RegenStrategy {
    /** No detection in progress — status is INACTIVE. */
    NONE,

    /** Strategy A: ECU direct flag PID responded with 0x01. Most accurate. */
    DIRECT_FLAG,

    /** Strategy B: EGT threshold crossed (no direct flag available). */
    EGT_FALLBACK
}

// ═══════════════════════════════════════════════════════════════════════════════
// BLE connection states (used by BleManager and the phone UI)
// ═══════════════════════════════════════════════════════════════════════════════

enum class ConnectionState {
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED
}
