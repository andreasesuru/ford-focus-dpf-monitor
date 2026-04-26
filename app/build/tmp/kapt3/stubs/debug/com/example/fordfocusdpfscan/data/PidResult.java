package com.example.fordfocusdpfscan.data;

/**
 * A single PID query result, pushed to the repository after each command.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BA\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\u0005\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u000b\u001a\u00020\f\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010\u001b\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010\u001e\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\fH\u00c6\u0003JS\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\u00052\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00c6\u0001J\u0013\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010$\u001a\u00020%H\u00d6\u0001J\t\u0010&\u001a\u00020\u0005H\u00d6\u0001R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000fR\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006\'"}, d2 = {"Lcom/example/fordfocusdpfscan/data/PidResult;", "", "phase", "Lcom/example/fordfocusdpfscan/data/ScanPhase;", "pidHex", "", "label", "status", "Lcom/example/fordfocusdpfscan/data/PidStatus;", "responseHex", "decodedValue", "timestamp", "", "(Lcom/example/fordfocusdpfscan/data/ScanPhase;Ljava/lang/String;Ljava/lang/String;Lcom/example/fordfocusdpfscan/data/PidStatus;Ljava/lang/String;Ljava/lang/String;J)V", "getDecodedValue", "()Ljava/lang/String;", "getLabel", "getPhase", "()Lcom/example/fordfocusdpfscan/data/ScanPhase;", "getPidHex", "getResponseHex", "getStatus", "()Lcom/example/fordfocusdpfscan/data/PidStatus;", "getTimestamp", "()J", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
public final class PidResult {
    @org.jetbrains.annotations.NotNull()
    private final com.example.fordfocusdpfscan.data.ScanPhase phase = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String pidHex = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String label = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.fordfocusdpfscan.data.PidStatus status = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String responseHex = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String decodedValue = null;
    private final long timestamp = 0L;
    
    public PidResult(@org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.ScanPhase phase, @org.jetbrains.annotations.NotNull()
    java.lang.String pidHex, @org.jetbrains.annotations.Nullable()
    java.lang.String label, @org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.PidStatus status, @org.jetbrains.annotations.NotNull()
    java.lang.String responseHex, @org.jetbrains.annotations.Nullable()
    java.lang.String decodedValue, long timestamp) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.fordfocusdpfscan.data.ScanPhase getPhase() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPidHex() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getLabel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.fordfocusdpfscan.data.PidStatus getStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getResponseHex() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getDecodedValue() {
        return null;
    }
    
    public final long getTimestamp() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.fordfocusdpfscan.data.ScanPhase component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.fordfocusdpfscan.data.PidStatus component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    public final long component7() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.fordfocusdpfscan.data.PidResult copy(@org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.ScanPhase phase, @org.jetbrains.annotations.NotNull()
    java.lang.String pidHex, @org.jetbrains.annotations.Nullable()
    java.lang.String label, @org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.PidStatus status, @org.jetbrains.annotations.NotNull()
    java.lang.String responseHex, @org.jetbrains.annotations.Nullable()
    java.lang.String decodedValue, long timestamp) {
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