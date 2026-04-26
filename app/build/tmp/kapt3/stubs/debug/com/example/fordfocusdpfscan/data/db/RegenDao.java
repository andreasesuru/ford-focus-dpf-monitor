package com.example.fordfocusdpfscan.data.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\b\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0014\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\t0\bH\'J\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00060\tH\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u001c\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\t2\u0006\u0010\r\u001a\u00020\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u000e\u0010\u0010\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\u0014J\u0016\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\u0016\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010\u0018\u001a\u00020\u00032\u0006\u0010\u0016\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0017\u00a8\u0006\u0019"}, d2 = {"Lcom/example/fordfocusdpfscan/data/db/RegenDao;", "", "clearAll", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getActiveSession", "Lcom/example/fordfocusdpfscan/data/db/RegenSession;", "getAllSessions", "Lkotlinx/coroutines/flow/Flow;", "", "getAllSessionsOnce", "getDataPointsForSession", "Lcom/example/fordfocusdpfscan/data/db/RegenDataPoint;", "sessionId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTotalCount", "", "insertDataPoint", "point", "(Lcom/example/fordfocusdpfscan/data/db/RegenDataPoint;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertSession", "session", "(Lcom/example/fordfocusdpfscan/data/db/RegenSession;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateSession", "app_debug"})
@androidx.room.Dao()
public abstract interface RegenDao {
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertSession(@org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.db.RegenSession session, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateSession(@org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.db.RegenSession session, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Returns all sessions newest-first, as a live Flow for the UI.
     */
    @androidx.room.Query(value = "SELECT * FROM regen_sessions ORDER BY startTimestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.fordfocusdpfscan.data.db.RegenSession>> getAllSessions();
    
    /**
     * Returns all sessions for export (no live updates needed).
     */
    @androidx.room.Query(value = "SELECT * FROM regen_sessions ORDER BY startTimestamp DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllSessionsOnce(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.fordfocusdpfscan.data.db.RegenSession>> $completion);
    
    /**
     * Finds the most recent IN_PROGRESS session (should be at most one).
     */
    @androidx.room.Query(value = "SELECT * FROM regen_sessions WHERE result = \'IN_PROGRESS\' ORDER BY startTimestamp DESC LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getActiveSession(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.fordfocusdpfscan.data.db.RegenSession> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM regen_sessions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTotalCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "DELETE FROM regen_sessions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertDataPoint(@org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.db.RegenDataPoint point, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM regen_data_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getDataPointsForSession(long sessionId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.fordfocusdpfscan.data.db.RegenDataPoint>> $completion);
}