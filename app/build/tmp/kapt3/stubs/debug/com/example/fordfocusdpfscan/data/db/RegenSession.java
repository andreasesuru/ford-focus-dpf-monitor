package com.example.fordfocusdpfscan.data.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\f\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b4\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B\u00d1\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\b\b\u0002\u0010\n\u001a\u00020\b\u0012\b\b\u0002\u0010\u000b\u001a\u00020\b\u0012\b\b\u0002\u0010\f\u001a\u00020\b\u0012\b\b\u0002\u0010\r\u001a\u00020\b\u0012\b\b\u0002\u0010\u000e\u001a\u00020\b\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\b\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\b\u0012\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\b\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\b\u0012\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0015\u0012\b\b\u0002\u0010\u0016\u001a\u00020\u0017\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0017\u00a2\u0006\u0002\u0010\u0019J\t\u00106\u001a\u00020\u0003H\u00c6\u0003J\t\u00107\u001a\u00020\bH\u00c6\u0003J\t\u00108\u001a\u00020\bH\u00c6\u0003J\u0010\u00109\u001a\u0004\u0018\u00010\bH\u00c6\u0003\u00a2\u0006\u0002\u0010&J\u0010\u0010:\u001a\u0004\u0018\u00010\bH\u00c6\u0003\u00a2\u0006\u0002\u0010&J\u0010\u0010;\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u001eJ\u0010\u0010<\u001a\u0004\u0018\u00010\bH\u00c6\u0003\u00a2\u0006\u0002\u0010&J\u0010\u0010=\u001a\u0004\u0018\u00010\bH\u00c6\u0003\u00a2\u0006\u0002\u0010&J\u0010\u0010>\u001a\u0004\u0018\u00010\u0015H\u00c6\u0003\u00a2\u0006\u0002\u0010\u001bJ\t\u0010?\u001a\u00020\u0017H\u00c6\u0003J\t\u0010@\u001a\u00020\u0017H\u00c6\u0003J\t\u0010A\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010B\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u001eJ\t\u0010C\u001a\u00020\u0003H\u00c6\u0003J\t\u0010D\u001a\u00020\bH\u00c6\u0003J\t\u0010E\u001a\u00020\bH\u00c6\u0003J\t\u0010F\u001a\u00020\bH\u00c6\u0003J\t\u0010G\u001a\u00020\bH\u00c6\u0003J\t\u0010H\u001a\u00020\bH\u00c6\u0003J\u00da\u0001\u0010I\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\b2\b\b\u0002\u0010\u000b\u001a\u00020\b2\b\b\u0002\u0010\f\u001a\u00020\b2\b\b\u0002\u0010\r\u001a\u00020\b2\b\b\u0002\u0010\u000e\u001a\u00020\b2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u0017H\u00c6\u0001\u00a2\u0006\u0002\u0010JJ\u0013\u0010K\u001a\u00020L2\b\u0010M\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010N\u001a\u00020\u0015H\u00d6\u0001J\t\u0010O\u001a\u00020\u0017H\u00d6\u0001R\u0015\u0010\u0014\u001a\u0004\u0018\u00010\u0015\u00a2\u0006\n\n\u0002\u0010\u001c\u001a\u0004\b\u001a\u0010\u001bR\u0015\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u001f\u001a\u0004\b\u001d\u0010\u001eR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0011\u0010\u000e\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0011\u0010\r\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010#R\u0015\u0010\u0013\u001a\u0004\u0018\u00010\b\u00a2\u0006\n\n\u0002\u0010\'\u001a\u0004\b%\u0010&R\u0015\u0010\u0012\u001a\u0004\u0018\u00010\b\u00a2\u0006\n\n\u0002\u0010\'\u001a\u0004\b(\u0010&R\u0015\u0010\u0010\u001a\u0004\u0018\u00010\b\u00a2\u0006\n\n\u0002\u0010\'\u001a\u0004\b)\u0010&R\u0015\u0010\u0011\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u001f\u001a\u0004\b*\u0010\u001eR\u0015\u0010\u000f\u001a\u0004\u0018\u00010\b\u00a2\u0006\n\n\u0002\u0010\'\u001a\u0004\b+\u0010&R\u0011\u0010\f\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010#R\u0011\u0010\n\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010#R\u0011\u0010\u000b\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010#R\u0011\u0010\t\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u0010#R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u0010!R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u0010#R\u0011\u0010\u0016\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u00103R\u0011\u0010\u0018\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b4\u00103R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b5\u0010!\u00a8\u0006P"}, d2 = {"Lcom/example/fordfocusdpfscan/data/db/RegenSession;", "", "id", "", "startTimestamp", "endTimestamp", "preOdometerKm", "preSootPct", "", "preLoadPct", "preDeltaPKpa", "preEgtC", "preCoolantC", "peakEgtC", "peakDeltaPKpa", "postSootPct", "postLoadPct", "postOdometerKm", "postEgtC", "postCoolantC", "durationMinutes", "", "regenType", "", "result", "(JJLjava/lang/Long;JFFFFFFFLjava/lang/Float;Ljava/lang/Float;Ljava/lang/Long;Ljava/lang/Float;Ljava/lang/Float;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;)V", "getDurationMinutes", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getEndTimestamp", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getId", "()J", "getPeakDeltaPKpa", "()F", "getPeakEgtC", "getPostCoolantC", "()Ljava/lang/Float;", "Ljava/lang/Float;", "getPostEgtC", "getPostLoadPct", "getPostOdometerKm", "getPostSootPct", "getPreCoolantC", "getPreDeltaPKpa", "getPreEgtC", "getPreLoadPct", "getPreOdometerKm", "getPreSootPct", "getRegenType", "()Ljava/lang/String;", "getResult", "getStartTimestamp", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(JJLjava/lang/Long;JFFFFFFFLjava/lang/Float;Ljava/lang/Float;Ljava/lang/Long;Ljava/lang/Float;Ljava/lang/Float;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;)Lcom/example/fordfocusdpfscan/data/db/RegenSession;", "equals", "", "other", "hashCode", "toString", "app_debug"})
@androidx.room.Entity(tableName = "regen_sessions")
public final class RegenSession {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final long id = 0L;
    private final long startTimestamp = 0L;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long endTimestamp = null;
    private final long preOdometerKm = 0L;
    private final float preSootPct = 0.0F;
    private final float preLoadPct = 0.0F;
    private final float preDeltaPKpa = 0.0F;
    private final float preEgtC = 0.0F;
    private final float preCoolantC = 0.0F;
    private final float peakEgtC = 0.0F;
    private final float peakDeltaPKpa = 0.0F;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Float postSootPct = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Float postLoadPct = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long postOdometerKm = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Float postEgtC = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Float postCoolantC = null;
    
    /**
     * Total regen duration in minutes.
     */
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer durationMinutes = null;
    
    /**
     * "ACTIVE" (EGT-confirmed) or "WARNING" (EGT warning only).
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String regenType = null;
    
    /**
     * "IN_PROGRESS", "COMPLETED", or "INTERRUPTED" (engine turned off mid-regen).
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String result = null;
    
    public RegenSession(long id, long startTimestamp, @org.jetbrains.annotations.Nullable()
    java.lang.Long endTimestamp, long preOdometerKm, float preSootPct, float preLoadPct, float preDeltaPKpa, float preEgtC, float preCoolantC, float peakEgtC, float peakDeltaPKpa, @org.jetbrains.annotations.Nullable()
    java.lang.Float postSootPct, @org.jetbrains.annotations.Nullable()
    java.lang.Float postLoadPct, @org.jetbrains.annotations.Nullable()
    java.lang.Long postOdometerKm, @org.jetbrains.annotations.Nullable()
    java.lang.Float postEgtC, @org.jetbrains.annotations.Nullable()
    java.lang.Float postCoolantC, @org.jetbrains.annotations.Nullable()
    java.lang.Integer durationMinutes, @org.jetbrains.annotations.NotNull()
    java.lang.String regenType, @org.jetbrains.annotations.NotNull()
    java.lang.String result) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    public final long getStartTimestamp() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getEndTimestamp() {
        return null;
    }
    
    public final long getPreOdometerKm() {
        return 0L;
    }
    
    public final float getPreSootPct() {
        return 0.0F;
    }
    
    public final float getPreLoadPct() {
        return 0.0F;
    }
    
    public final float getPreDeltaPKpa() {
        return 0.0F;
    }
    
    public final float getPreEgtC() {
        return 0.0F;
    }
    
    public final float getPreCoolantC() {
        return 0.0F;
    }
    
    public final float getPeakEgtC() {
        return 0.0F;
    }
    
    public final float getPeakDeltaPKpa() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float getPostSootPct() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float getPostLoadPct() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getPostOdometerKm() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float getPostEgtC() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float getPostCoolantC() {
        return null;
    }
    
    /**
     * Total regen duration in minutes.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getDurationMinutes() {
        return null;
    }
    
    /**
     * "ACTIVE" (EGT-confirmed) or "WARNING" (EGT warning only).
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRegenType() {
        return null;
    }
    
    /**
     * "IN_PROGRESS", "COMPLETED", or "INTERRUPTED" (engine turned off mid-regen).
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getResult() {
        return null;
    }
    
    public RegenSession() {
        super();
    }
    
    public final long component1() {
        return 0L;
    }
    
    public final float component10() {
        return 0.0F;
    }
    
    public final float component11() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float component13() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component14() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float component15() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float component16() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component17() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component18() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component19() {
        return null;
    }
    
    public final long component2() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component3() {
        return null;
    }
    
    public final long component4() {
        return 0L;
    }
    
    public final float component5() {
        return 0.0F;
    }
    
    public final float component6() {
        return 0.0F;
    }
    
    public final float component7() {
        return 0.0F;
    }
    
    public final float component8() {
        return 0.0F;
    }
    
    public final float component9() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.fordfocusdpfscan.data.db.RegenSession copy(long id, long startTimestamp, @org.jetbrains.annotations.Nullable()
    java.lang.Long endTimestamp, long preOdometerKm, float preSootPct, float preLoadPct, float preDeltaPKpa, float preEgtC, float preCoolantC, float peakEgtC, float peakDeltaPKpa, @org.jetbrains.annotations.Nullable()
    java.lang.Float postSootPct, @org.jetbrains.annotations.Nullable()
    java.lang.Float postLoadPct, @org.jetbrains.annotations.Nullable()
    java.lang.Long postOdometerKm, @org.jetbrains.annotations.Nullable()
    java.lang.Float postEgtC, @org.jetbrains.annotations.Nullable()
    java.lang.Float postCoolantC, @org.jetbrains.annotations.Nullable()
    java.lang.Integer durationMinutes, @org.jetbrains.annotations.NotNull()
    java.lang.String regenType, @org.jetbrains.annotations.NotNull()
    java.lang.String result) {
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