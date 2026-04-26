package com.example.fordfocusdpfscan.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u0000 \'2\u00020\u0001:\u0001\'B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0011\u001a\u00020\u0012H\u0002J\b\u0010\u0013\u001a\u00020\u0012H\u0002J\u0012\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0016J\b\u0010\u0018\u001a\u00020\u0012H\u0016J\b\u0010\u0019\u001a\u00020\u0012H\u0016J\u0018\u0010\u001a\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0002J\u0018\u0010\u001f\u001a\u00020\u00122\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020!H\u0002J\"\u0010#\u001a\u00020$2\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010%\u001a\u00020$2\u0006\u0010&\u001a\u00020$H\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0005\u001a\u00020\u00068FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR\u001b\u0010\u000b\u001a\u00020\f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000f\u0010\n\u001a\u0004\b\r\u0010\u000eR\u000e\u0010\u0010\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lcom/example/fordfocusdpfscan/service/DpfForegroundService;", "Landroidx/lifecycle/LifecycleService;", "()V", "DATA_POINT_INTERVAL_MS", "", "bleManager", "Lcom/example/fordfocusdpfscan/ble/BleManager;", "getBleManager", "()Lcom/example/fordfocusdpfscan/ble/BleManager;", "bleManager$delegate", "Lkotlin/Lazy;", "historyRepo", "Lcom/example/fordfocusdpfscan/data/RegenHistoryRepository;", "getHistoryRepo", "()Lcom/example/fordfocusdpfscan/data/RegenHistoryRepository;", "historyRepo$delegate", "lastDataPointTime", "observeConnectionState", "", "observeDpfData", "onBind", "Landroid/os/IBinder;", "intent", "Landroid/content/Intent;", "onCreate", "onDestroy", "onRegenSessionEvent", "event", "", "data", "Lcom/example/fordfocusdpfscan/data/DpfData;", "onRegenStatusChanged", "old", "Lcom/example/fordfocusdpfscan/data/RegenStatus;", "new", "onStartCommand", "", "flags", "startId", "Companion", "app_debug"})
public final class DpfForegroundService extends androidx.lifecycle.LifecycleService {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "FOCUS_Service";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_CONNECT = "com.example.fordfocusdpfscan.CONNECT";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_DISCONNECT = "com.example.fordfocusdpfscan.DISCONNECT";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_RECONNECT = "com.example.fordfocusdpfscan.RECONNECT";
    
    /**
     * Connect to a specific device by MAC address (from the device picker).
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_CONNECT_ADDRESS = "com.example.fordfocusdpfscan.CONNECT_ADDRESS";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String EXTRA_DEVICE_ADDRESS = "extra_device_address";
    
    /**
     * The BLE manager instance — created once per service lifetime.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy bleManager$delegate = null;
    
    /**
     * Regen history repository — owns the Room DB and the HTML export.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy historyRepo$delegate = null;
    
    /**
     * Throttle DATA_POINT events: only record one sample every 30 seconds.
     */
    private long lastDataPointTime = 0L;
    private final long DATA_POINT_INTERVAL_MS = 30000L;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.fordfocusdpfscan.service.DpfForegroundService.Companion Companion = null;
    
    public DpfForegroundService() {
        super();
    }
    
    /**
     * The BLE manager instance — created once per service lifetime.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.example.fordfocusdpfscan.ble.BleManager getBleManager() {
        return null;
    }
    
    /**
     * Regen history repository — owns the Room DB and the HTML export.
     */
    private final com.example.fordfocusdpfscan.data.RegenHistoryRepository getHistoryRepo() {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.os.IBinder onBind(@org.jetbrains.annotations.NotNull()
    android.content.Intent intent) {
        return null;
    }
    
    /**
     * Refreshes the persistent notification content every time DpfData changes.
     */
    private final void observeDpfData() {
    }
    
    /**
     * Fires silent BLE connection/disconnection notifications.
     */
    private final void observeConnectionState() {
    }
    
    /**
     * Called by DpfRepository on every status transition.
     * Fires the appropriate heads-up notification + MP3 sound for regen events.
     */
    private final void onRegenStatusChanged(com.example.fordfocusdpfscan.data.RegenStatus old, com.example.fordfocusdpfscan.data.RegenStatus p1_54480) {
    }
    
    /**
     * Routes regen session events from DpfRepository to RegenHistoryRepository.
     * DATA_POINT events are throttled to one sample every 30 seconds.
     */
    private final void onRegenSessionEvent(java.lang.String event, com.example.fordfocusdpfscan.data.DpfData data) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/example/fordfocusdpfscan/service/DpfForegroundService$Companion;", "", "()V", "ACTION_CONNECT", "", "ACTION_CONNECT_ADDRESS", "ACTION_DISCONNECT", "ACTION_RECONNECT", "EXTRA_DEVICE_ADDRESS", "TAG", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}