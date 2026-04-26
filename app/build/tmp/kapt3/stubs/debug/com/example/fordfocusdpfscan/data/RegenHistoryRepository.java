package com.example.fordfocusdpfscan.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\u0016\u001a\u00020\u00062\b\b\u0002\u0010\u0017\u001a\u00020\u0006H\u0086@\u00a2\u0006\u0002\u0010\u0018J\u0016\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0086@\u00a2\u0006\u0002\u0010\u001dJ\u001e\u0010\u001e\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\u0006H\u0086@\u00a2\u0006\u0002\u0010 J\u0016\u0010!\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0086@\u00a2\u0006\u0002\u0010\u001dR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\tR\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u00120\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015\u00a8\u0006\""}, d2 = {"Lcom/example/fordfocusdpfscan/data/RegenHistoryRepository;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "TAG", "", "currentSessionId", "", "Ljava/lang/Long;", "dao", "Lcom/example/fordfocusdpfscan/data/db/RegenDao;", "peakDeltaP", "", "peakEgt", "sessionStartTime", "sessions", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/example/fordfocusdpfscan/data/db/RegenSession;", "getSessions", "()Lkotlinx/coroutines/flow/Flow;", "generateHtmlReport", "vehicleInfo", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "onRegenDataPoint", "", "data", "Lcom/example/fordfocusdpfscan/data/DpfData;", "(Lcom/example/fordfocusdpfscan/data/DpfData;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "onRegenEnded", "result", "(Lcom/example/fordfocusdpfscan/data/DpfData;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "onRegenStarted", "app_debug"})
public final class RegenHistoryRepository {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String TAG = "FOCUS_History";
    @org.jetbrains.annotations.NotNull()
    private final com.example.fordfocusdpfscan.data.db.RegenDao dao = null;
    
    /**
     * Live list of all sessions, newest first. Observed by HistoryActivity.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.example.fordfocusdpfscan.data.db.RegenSession>> sessions = null;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Long currentSessionId;
    private long sessionStartTime = 0L;
    private float peakEgt = 0.0F;
    private float peakDeltaP = 0.0F;
    
    public RegenHistoryRepository(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    /**
     * Live list of all sessions, newest first. Observed by HistoryActivity.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.example.fordfocusdpfscan.data.db.RegenSession>> getSessions() {
        return null;
    }
    
    /**
     * Called when regen transitions to WARNING or ACTIVE.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object onRegenStarted(@org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.DpfData data, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Called every ~30 seconds while regen is ACTIVE.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object onRegenDataPoint(@org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.DpfData data, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Called when regen ends (COMPLETED or INTERRUPTED).
     * [result] = "COMPLETED" | "INTERRUPTED"
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object onRegenEnded(@org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.DpfData data, @org.jetbrains.annotations.NotNull()
    java.lang.String result, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Generates a complete HTML report string suitable for sharing with a mechanic.
     * Includes: vehicle info, summary stats, session table, and per-session details.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object generateHtmlReport(@org.jetbrains.annotations.NotNull()
    java.lang.String vehicleInfo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
}