package com.example.fordfocusdpfscan.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u000e\u001a\u00020\u000fJ\u0016\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\u0006\u0010\u0015\u001a\u00020\u000bJ\u0006\u0010\u0016\u001a\u00020\u000bJ\u0016\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0012R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t\u00a8\u0006\u001b"}, d2 = {"Lcom/example/fordfocusdpfscan/data/EcuScanRepository;", "", "()V", "_scanState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/example/fordfocusdpfscan/data/ScanState;", "scanState", "Lkotlinx/coroutines/flow/StateFlow;", "getScanState", "()Lkotlinx/coroutines/flow/StateFlow;", "addResult", "", "result", "Lcom/example/fordfocusdpfscan/data/PidResult;", "buildLogText", "", "incrementProgress", "progress", "", "status", "Lcom/example/fordfocusdpfscan/data/PidStatus;", "reset", "setCompleted", "setPhase", "phase", "Lcom/example/fordfocusdpfscan/data/ScanPhase;", "totalPids", "app_debug"})
public final class EcuScanRepository {
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<com.example.fordfocusdpfscan.data.ScanState> _scanState = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<com.example.fordfocusdpfscan.data.ScanState> scanState = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.fordfocusdpfscan.data.EcuScanRepository INSTANCE = null;
    
    private EcuScanRepository() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.fordfocusdpfscan.data.ScanState> getScanState() {
        return null;
    }
    
    /**
     * Resets all state and marks the scan as running.
     */
    public final void reset() {
    }
    
    /**
     * Signals the start of a new scan phase.
     */
    public final void setPhase(@org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.ScanPhase phase, int totalPids) {
    }
    
    /**
     * Appends a single [PidResult] to the live results list.
     */
    public final void addResult(@org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.PidResult result) {
    }
    
    /**
     * Increments the phase progress counter and the appropriate status bucket.
     * Called for every PID, even timeouts (so the progress bar stays accurate).
     */
    public final void incrementProgress(int progress, @org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.PidStatus status) {
    }
    
    /**
     * Marks the scan as finished.
     */
    public final void setCompleted() {
    }
    
    /**
     * Formats all results into a plain-text log suitable for sharing.
     * Sections are separated by phase headers.  Known PIDs are annotated
     * with their label and decoded value so you can immediately identify them.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String buildLogText() {
        return null;
    }
}