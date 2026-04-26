package com.example.fordfocusdpfscan.data;

/**
 * Snapshot of all DPF-related values read from the OBD2 dongle.
 * Emitted by [DpfRepository] as a [kotlinx.coroutines.flow.StateFlow].
 *
 * Default values of -1f signal "no data received yet" — the UI renders "– –".
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b.\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u00b9\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\r\u0012\b\b\u0002\u0010\u000f\u001a\u00020\r\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0014\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0016\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0017\u001a\u00020\u0018\u00a2\u0006\u0002\u0010\u0019J\t\u00101\u001a\u00020\u0003H\u00c6\u0003J\t\u00102\u001a\u00020\rH\u00c6\u0003J\t\u00103\u001a\u00020\u0003H\u00c6\u0003J\t\u00104\u001a\u00020\u0003H\u00c6\u0003J\t\u00105\u001a\u00020\u0003H\u00c6\u0003J\t\u00106\u001a\u00020\u0003H\u00c6\u0003J\t\u00107\u001a\u00020\u0003H\u00c6\u0003J\t\u00108\u001a\u00020\u0003H\u00c6\u0003J\t\u00109\u001a\u00020\u0003H\u00c6\u0003J\t\u0010:\u001a\u00020\u0018H\u00c6\u0003J\t\u0010;\u001a\u00020\u0003H\u00c6\u0003J\t\u0010<\u001a\u00020\u0003H\u00c6\u0003J\t\u0010=\u001a\u00020\u0003H\u00c6\u0003J\t\u0010>\u001a\u00020\u0003H\u00c6\u0003J\t\u0010?\u001a\u00020\tH\u00c6\u0003J\t\u0010@\u001a\u00020\u000bH\u00c6\u0003J\t\u0010A\u001a\u00020\rH\u00c6\u0003J\t\u0010B\u001a\u00020\rH\u00c6\u0003J\u00bd\u0001\u0010C\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\r2\b\b\u0002\u0010\u000f\u001a\u00020\r2\b\b\u0002\u0010\u0010\u001a\u00020\u00032\b\b\u0002\u0010\u0011\u001a\u00020\u00032\b\b\u0002\u0010\u0012\u001a\u00020\u00032\b\b\u0002\u0010\u0013\u001a\u00020\u00032\b\b\u0002\u0010\u0014\u001a\u00020\u00032\b\b\u0002\u0010\u0015\u001a\u00020\u00032\b\b\u0002\u0010\u0016\u001a\u00020\u00032\b\b\u0002\u0010\u0017\u001a\u00020\u0018H\u00c6\u0001J\u0013\u0010D\u001a\u00020\u00182\b\u0010E\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010F\u001a\u00020GH\u00d6\u0001J\t\u0010H\u001a\u00020IH\u00d6\u0001R\u0011\u0010\u0014\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0017\u001a\u00020\u0018\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001bR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001bR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001bR\u0011\u0010\u0016\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001bR\u0011\u0010\u0012\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001bR\u0011\u0010\u0013\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001bR\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u0011\u0010\u000e\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010%R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u001bR\u0011\u0010\u000f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010%R\u0011\u0010\u0015\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u001bR\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010+R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010-R\u0011\u0010\u0010\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010\u001bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u0010\u001bR\u0011\u0010\u0011\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u0010\u001b\u00a8\u0006J"}, d2 = {"Lcom/example/fordfocusdpfscan/data/DpfData;", "", "sootPercentage", "", "loadPercentage", "dpfDeltaPressureKpa", "coolantTempC", "egtCelsius", "regenStatus", "Lcom/example/fordfocusdpfscan/data/RegenStatus;", "regenStrategy", "Lcom/example/fordfocusdpfscan/data/RegenStrategy;", "kmSinceLastRegen", "", "kmSinceOilChange", "odometerKm", "rpmValue", "speedKmh", "engineLoadPct", "intakeMapKpa", "baroKpa", "oilTempC", "egtPostDpfC", "bleConnected", "", "(FFFFFLcom/example/fordfocusdpfscan/data/RegenStatus;Lcom/example/fordfocusdpfscan/data/RegenStrategy;JJJFFFFFFFZ)V", "getBaroKpa", "()F", "getBleConnected", "()Z", "getCoolantTempC", "getDpfDeltaPressureKpa", "getEgtCelsius", "getEgtPostDpfC", "getEngineLoadPct", "getIntakeMapKpa", "getKmSinceLastRegen", "()J", "getKmSinceOilChange", "getLoadPercentage", "getOdometerKm", "getOilTempC", "getRegenStatus", "()Lcom/example/fordfocusdpfscan/data/RegenStatus;", "getRegenStrategy", "()Lcom/example/fordfocusdpfscan/data/RegenStrategy;", "getRpmValue", "getSootPercentage", "getSpeedKmh", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "", "app_debug"})
public final class DpfData {
    
    /**
     * DPF soot % (open-loop, combustion model estimate).
     * Derived from PID 22 057B. Raw integer = % directly.
     * Scale: 100% = PCM triggers dynamic regen, 320% = DPF replacement needed.
     * Confirmed on Ford Focus Mk3 1.5 TDCi EDC17C70. -1f = no data yet.
     */
    private final float sootPercentage = 0.0F;
    
    /**
     * DPF load % (closed-loop, from pressure sensor).
     * Derived from PID 22 0579. Raw integer = % directly.
     * Scale: 100% = PCM triggers dynamic regen, 320% = DPF replacement needed.
     * Confirmed on Ford Focus Mk3 1.5 TDCi EDC17C70. -1f = no data yet.
     */
    private final float loadPercentage = 0.0F;
    
    /**
     * DPF differential pressure (upstream – downstream) in kPa.
     * Derived from PID 01 7A (SAE J1979), bytes B–C: (256×B+C)/100 kPa.
     * Confirmed working on Ford Focus Mk3 1.5 TDCi EDC17C70.
     * Typical values: ~0 kPa at idle, 5–15 kPa at motorway cruise.
     * High values (>20 kPa) suggest filter blockage. -1f = no data yet.
     */
    private final float dpfDeltaPressureKpa = 0.0F;
    
    /**
     * Engine coolant temperature in °C. PID 01 05. Range: typically 70–110 °C.
     */
    private final float coolantTempC = 0.0F;
    
    /**
     * Exhaust Gas Temperature (EGT) in °C.
     * PID 22 XXXX — replace with the real Ford Focus PID found via ForScan.
     * Range: 150–700 °C. Critical for regen detection (Strategy B).
     */
    private final float egtCelsius = 0.0F;
    
    /**
     * Current regeneration status, computed by [DpfRepository].
     */
    @org.jetbrains.annotations.NotNull()
    private final com.example.fordfocusdpfscan.data.RegenStatus regenStatus = null;
    
    /**
     * Which detection strategy produced the current [regenStatus].
     */
    @org.jetbrains.annotations.NotNull()
    private final com.example.fordfocusdpfscan.data.RegenStrategy regenStrategy = null;
    
    /**
     * Km travelled since the last DPF regeneration.
     * Derived from PID 22 050B. Confirmed on Ford Focus Mk3 1.5 TDCi EDC17C70.
     * Example: 411 km after last regen. -1L = no data yet.
     */
    private final long kmSinceLastRegen = 0L;
    
    /**
     * Km travelled since the last engine oil change.
     * Derived from PID 22 0542. Confirmed exact match (8979 km).
     * Reset by the mechanic via Ford IDS at every service. -1L = no data yet.
     */
    private final long kmSinceOilChange = 0L;
    
    /**
     * Current odometer reading in km. Derived from PID 22 DD01. -1L = no data yet.
     */
    private final long odometerKm = 0L;
    
    /**
     * Engine RPM. PID 01 0C. Formula: ((A*256)+B)/4. -1f = no data.
     */
    private final float rpmValue = 0.0F;
    
    /**
     * Vehicle speed in km/h. PID 01 0D. Formula: A. -1f = no data.
     */
    private final float speedKmh = 0.0F;
    
    /**
     * Engine load as % (0–100). PID 01 04. Formula: A*100/255. -1f = no data.
     */
    private final float engineLoadPct = 0.0F;
    
    /**
     * Intake manifold absolute pressure in kPa. PID 01 0B.
     * Subtract barometric pressure to get boost above atmosphere.
     * Idle: ~100 kPa (atmo). Cruising: 120–170 kPa. Full load: 180–220 kPa.
     * -1f = no data.
     */
    private final float intakeMapKpa = 0.0F;
    
    /**
     * Barometric (ambient) pressure in kPa. PID 01 33.
     * Typically 95–103 kPa at sea level. -1f = no data.
     */
    private final float baroKpa = 0.0F;
    
    /**
     * Engine oil temperature in °C. PID 01 5C. Formula: A-40.
     * Warm: 90–110°C. -1f = no data.
     */
    private final float oilTempC = 0.0F;
    
    /**
     * EGT post-DPF sensor (downstream) in °C. Extracted from PID 01 78 sensor 2.
     * Delta vs pre-DPF indicates DPF efficiency. -1f = no data or sensor absent.
     */
    private final float egtPostDpfC = 0.0F;
    
    /**
     * True when the GATT connection to Android-Vlink is active and services
     * have been discovered.
     */
    private final boolean bleConnected = false;
    
    public DpfData(float sootPercentage, float loadPercentage, float dpfDeltaPressureKpa, float coolantTempC, float egtCelsius, @org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.RegenStatus regenStatus, @org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.RegenStrategy regenStrategy, long kmSinceLastRegen, long kmSinceOilChange, long odometerKm, float rpmValue, float speedKmh, float engineLoadPct, float intakeMapKpa, float baroKpa, float oilTempC, float egtPostDpfC, boolean bleConnected) {
        super();
    }
    
    /**
     * DPF soot % (open-loop, combustion model estimate).
     * Derived from PID 22 057B. Raw integer = % directly.
     * Scale: 100% = PCM triggers dynamic regen, 320% = DPF replacement needed.
     * Confirmed on Ford Focus Mk3 1.5 TDCi EDC17C70. -1f = no data yet.
     */
    public final float getSootPercentage() {
        return 0.0F;
    }
    
    /**
     * DPF load % (closed-loop, from pressure sensor).
     * Derived from PID 22 0579. Raw integer = % directly.
     * Scale: 100% = PCM triggers dynamic regen, 320% = DPF replacement needed.
     * Confirmed on Ford Focus Mk3 1.5 TDCi EDC17C70. -1f = no data yet.
     */
    public final float getLoadPercentage() {
        return 0.0F;
    }
    
    /**
     * DPF differential pressure (upstream – downstream) in kPa.
     * Derived from PID 01 7A (SAE J1979), bytes B–C: (256×B+C)/100 kPa.
     * Confirmed working on Ford Focus Mk3 1.5 TDCi EDC17C70.
     * Typical values: ~0 kPa at idle, 5–15 kPa at motorway cruise.
     * High values (>20 kPa) suggest filter blockage. -1f = no data yet.
     */
    public final float getDpfDeltaPressureKpa() {
        return 0.0F;
    }
    
    /**
     * Engine coolant temperature in °C. PID 01 05. Range: typically 70–110 °C.
     */
    public final float getCoolantTempC() {
        return 0.0F;
    }
    
    /**
     * Exhaust Gas Temperature (EGT) in °C.
     * PID 22 XXXX — replace with the real Ford Focus PID found via ForScan.
     * Range: 150–700 °C. Critical for regen detection (Strategy B).
     */
    public final float getEgtCelsius() {
        return 0.0F;
    }
    
    /**
     * Current regeneration status, computed by [DpfRepository].
     */
    @org.jetbrains.annotations.NotNull()
    public final com.example.fordfocusdpfscan.data.RegenStatus getRegenStatus() {
        return null;
    }
    
    /**
     * Which detection strategy produced the current [regenStatus].
     */
    @org.jetbrains.annotations.NotNull()
    public final com.example.fordfocusdpfscan.data.RegenStrategy getRegenStrategy() {
        return null;
    }
    
    /**
     * Km travelled since the last DPF regeneration.
     * Derived from PID 22 050B. Confirmed on Ford Focus Mk3 1.5 TDCi EDC17C70.
     * Example: 411 km after last regen. -1L = no data yet.
     */
    public final long getKmSinceLastRegen() {
        return 0L;
    }
    
    /**
     * Km travelled since the last engine oil change.
     * Derived from PID 22 0542. Confirmed exact match (8979 km).
     * Reset by the mechanic via Ford IDS at every service. -1L = no data yet.
     */
    public final long getKmSinceOilChange() {
        return 0L;
    }
    
    /**
     * Current odometer reading in km. Derived from PID 22 DD01. -1L = no data yet.
     */
    public final long getOdometerKm() {
        return 0L;
    }
    
    /**
     * Engine RPM. PID 01 0C. Formula: ((A*256)+B)/4. -1f = no data.
     */
    public final float getRpmValue() {
        return 0.0F;
    }
    
    /**
     * Vehicle speed in km/h. PID 01 0D. Formula: A. -1f = no data.
     */
    public final float getSpeedKmh() {
        return 0.0F;
    }
    
    /**
     * Engine load as % (0–100). PID 01 04. Formula: A*100/255. -1f = no data.
     */
    public final float getEngineLoadPct() {
        return 0.0F;
    }
    
    /**
     * Intake manifold absolute pressure in kPa. PID 01 0B.
     * Subtract barometric pressure to get boost above atmosphere.
     * Idle: ~100 kPa (atmo). Cruising: 120–170 kPa. Full load: 180–220 kPa.
     * -1f = no data.
     */
    public final float getIntakeMapKpa() {
        return 0.0F;
    }
    
    /**
     * Barometric (ambient) pressure in kPa. PID 01 33.
     * Typically 95–103 kPa at sea level. -1f = no data.
     */
    public final float getBaroKpa() {
        return 0.0F;
    }
    
    /**
     * Engine oil temperature in °C. PID 01 5C. Formula: A-40.
     * Warm: 90–110°C. -1f = no data.
     */
    public final float getOilTempC() {
        return 0.0F;
    }
    
    /**
     * EGT post-DPF sensor (downstream) in °C. Extracted from PID 01 78 sensor 2.
     * Delta vs pre-DPF indicates DPF efficiency. -1f = no data or sensor absent.
     */
    public final float getEgtPostDpfC() {
        return 0.0F;
    }
    
    /**
     * True when the GATT connection to Android-Vlink is active and services
     * have been discovered.
     */
    public final boolean getBleConnected() {
        return false;
    }
    
    public DpfData() {
        super();
    }
    
    public final float component1() {
        return 0.0F;
    }
    
    public final long component10() {
        return 0L;
    }
    
    public final float component11() {
        return 0.0F;
    }
    
    public final float component12() {
        return 0.0F;
    }
    
    public final float component13() {
        return 0.0F;
    }
    
    public final float component14() {
        return 0.0F;
    }
    
    public final float component15() {
        return 0.0F;
    }
    
    public final float component16() {
        return 0.0F;
    }
    
    public final float component17() {
        return 0.0F;
    }
    
    public final boolean component18() {
        return false;
    }
    
    public final float component2() {
        return 0.0F;
    }
    
    public final float component3() {
        return 0.0F;
    }
    
    public final float component4() {
        return 0.0F;
    }
    
    public final float component5() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.fordfocusdpfscan.data.RegenStatus component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.fordfocusdpfscan.data.RegenStrategy component7() {
        return null;
    }
    
    public final long component8() {
        return 0L;
    }
    
    public final long component9() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.fordfocusdpfscan.data.DpfData copy(float sootPercentage, float loadPercentage, float dpfDeltaPressureKpa, float coolantTempC, float egtCelsius, @org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.RegenStatus regenStatus, @org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.RegenStrategy regenStrategy, long kmSinceLastRegen, long kmSinceOilChange, long odometerKm, float rpmValue, float speedKmh, float engineLoadPct, float intakeMapKpa, float baroKpa, float oilTempC, float egtPostDpfC, boolean bleConnected) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}