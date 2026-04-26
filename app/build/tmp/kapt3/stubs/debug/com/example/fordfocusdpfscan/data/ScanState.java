package com.example.fordfocusdpfscan.data;

/**
 * Snapshot of the entire scan state — emitted by [EcuScanRepository.scanState]
 * on every change so the UI always has a consistent picture.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u001e\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001Bg\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\b\b\u0002\u0010\n\u001a\u00020\b\u0012\b\b\u0002\u0010\u000b\u001a\u00020\b\u0012\b\b\u0002\u0010\f\u001a\u00020\b\u0012\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e\u00a2\u0006\u0002\u0010\u0010J\t\u0010 \u001a\u00020\u0003H\u00c6\u0003J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\"\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003J\t\u0010#\u001a\u00020\bH\u00c6\u0003J\t\u0010$\u001a\u00020\bH\u00c6\u0003J\t\u0010%\u001a\u00020\bH\u00c6\u0003J\t\u0010&\u001a\u00020\bH\u00c6\u0003J\t\u0010\'\u001a\u00020\bH\u00c6\u0003J\u000f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u00c6\u0003Jk\u0010)\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\b2\b\b\u0002\u0010\u000b\u001a\u00020\b2\b\b\u0002\u0010\f\u001a\u00020\b2\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u00c6\u0001J\u0013\u0010*\u001a\u00020\u00032\b\u0010+\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010,\u001a\u00020\bH\u00d6\u0001J\t\u0010-\u001a\u00020.H\u00d6\u0001R\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0013R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0013R\u0011\u0010\t\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0016\u001a\u00020\b8F\u00a2\u0006\u0006\u001a\u0004\b\u0017\u0010\u0015R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0015R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u000b\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0015R\u0011\u0010\u001c\u001a\u00020\b8F\u00a2\u0006\u0006\u001a\u0004\b\u001d\u0010\u0015R\u0011\u0010\n\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0015R\u0011\u0010\f\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0015\u00a8\u0006/"}, d2 = {"Lcom/example/fordfocusdpfscan/data/ScanState;", "", "isRunning", "", "isCompleted", "currentPhase", "Lcom/example/fordfocusdpfscan/data/ScanPhase;", "phaseTotalPids", "", "phaseProgress", "totalResponded", "totalNegative", "totalTimeout", "results", "", "Lcom/example/fordfocusdpfscan/data/PidResult;", "(ZZLcom/example/fordfocusdpfscan/data/ScanPhase;IIIIILjava/util/List;)V", "getCurrentPhase", "()Lcom/example/fordfocusdpfscan/data/ScanPhase;", "()Z", "getPhaseProgress", "()I", "phaseProgressPercent", "getPhaseProgressPercent", "getPhaseTotalPids", "getResults", "()Ljava/util/List;", "getTotalNegative", "totalQueried", "getTotalQueried", "getTotalResponded", "getTotalTimeout", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "", "app_debug"})
public final class ScanState {
    private final boolean isRunning = false;
    private final boolean isCompleted = false;
    @org.jetbrains.annotations.Nullable()
    private final com.example.fordfocusdpfscan.data.ScanPhase currentPhase = null;
    private final int phaseTotalPids = 0;
    private final int phaseProgress = 0;
    private final int totalResponded = 0;
    private final int totalNegative = 0;
    private final int totalTimeout = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.fordfocusdpfscan.data.PidResult> results = null;
    
    public ScanState(boolean isRunning, boolean isCompleted, @org.jetbrains.annotations.Nullable()
    com.example.fordfocusdpfscan.data.ScanPhase currentPhase, int phaseTotalPids, int phaseProgress, int totalResponded, int totalNegative, int totalTimeout, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.fordfocusdpfscan.data.PidResult> results) {
        super();
    }
    
    public final boolean isRunning() {
        return false;
    }
    
    public final boolean isCompleted() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.fordfocusdpfscan.data.ScanPhase getCurrentPhase() {
        return null;
    }
    
    public final int getPhaseTotalPids() {
        return 0;
    }
    
    public final int getPhaseProgress() {
        return 0;
    }
    
    public final int getTotalResponded() {
        return 0;
    }
    
    public final int getTotalNegative() {
        return 0;
    }
    
    public final int getTotalTimeout() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.fordfocusdpfscan.data.PidResult> getResults() {
        return null;
    }
    
    public final int getTotalQueried() {
        return 0;
    }
    
    public final int getPhaseProgressPercent() {
        return 0;
    }
    
    public ScanState() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    public final boolean component2() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.fordfocusdpfscan.data.ScanPhase component3() {
        return null;
    }
    
    public final int component4() {
        return 0;
    }
    
    public final int component5() {
        return 0;
    }
    
    public final int component6() {
        return 0;
    }
    
    public final int component7() {
        return 0;
    }
    
    public final int component8() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.fordfocusdpfscan.data.PidResult> component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.fordfocusdpfscan.data.ScanState copy(boolean isRunning, boolean isCompleted, @org.jetbrains.annotations.Nullable()
    com.example.fordfocusdpfscan.data.ScanPhase currentPhase, int phaseTotalPids, int phaseProgress, int totalResponded, int totalNegative, int totalTimeout, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.fordfocusdpfscan.data.PidResult> results) {
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