package com.example.fordfocusdpfscan.car;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u000f\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\u0014\u001a\u00020\u0015J\u0010\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0017\u001a\u00020\u0015H\u0002J\u000e\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u0012\u001a\u00020\u0013J\u0016\u0010\u001b\u001a\u00020\u00192\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u001c\u001a\u00020\u0004J\u000e\u0010\u001d\u001a\u00020\u00192\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u0010\u001e\u001a\u00020\u00192\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u0010\u001f\u001a\u00020\u00192\u0006\u0010\u0012\u001a\u00020\u0013J(\u0010 \u001a\u00020\u00192\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010!\u001a\u00020\u00042\u0006\u0010\"\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\nH\u0002J2\u0010$\u001a\u00020\u00192\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010!\u001a\u00020\u00042\u0006\u0010\"\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\n2\b\b\u0002\u0010%\u001a\u00020\nH\u0002J\u0016\u0010&\u001a\u00020\u00192\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\'\u001a\u00020\u0011R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\nX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lcom/example/fordfocusdpfscan/car/NotificationHelper;", "", "()V", "CHANNEL_CONNECTION", "", "CHANNEL_PERSISTENT", "CHANNEL_REGEN", "EVENT_TIMEOUT_MS", "", "NOTIF_ID_CONNECTION", "", "NOTIF_ID_EVENT", "NOTIF_ID_PERSISTENT", "TAG", "channelsCreated", "", "buildPersistentNotification", "Landroid/app/Notification;", "context", "Landroid/content/Context;", "dpfData", "Lcom/example/fordfocusdpfscan/data/DpfData;", "buildPersistentSummary", "data", "createChannels", "", "notifyActive", "notifyBleConnected", "deviceName", "notifyBleLost", "notifyCompleted", "notifyWarning", "postConnectionNotification", "title", "text", "color", "postEventNotification", "priority", "updatePersistentNotification", "notification", "app_debug"})
public final class NotificationHelper {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "FOCUS_Notif";
    public static final int NOTIF_ID_PERSISTENT = 1001;
    public static final int NOTIF_ID_EVENT = 1002;
    public static final int NOTIF_ID_CONNECTION = 1003;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String CHANNEL_PERSISTENT = "focus_persistent";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String CHANNEL_REGEN = "focus_regen";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String CHANNEL_CONNECTION = "focus_connection";
    private static final long EVENT_TIMEOUT_MS = 5000L;
    private static boolean channelsCreated = false;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.fordfocusdpfscan.car.NotificationHelper INSTANCE = null;
    
    private NotificationHelper() {
        super();
    }
    
    /**
     * Creates both notification channels. Safe to call multiple times (idempotent).
     * Call from Application.onCreate() or before the first notification.
     */
    public final void createChannels(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    /**
     * Builds (or rebuilds) the always-on foreground service notification.
     * Updated every time DpfRepository emits new data.
     */
    @org.jetbrains.annotations.NotNull()
    public final android.app.Notification buildPersistentNotification(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.example.fordfocusdpfscan.data.DpfData dpfData) {
        return null;
    }
    
    private final java.lang.String buildPersistentSummary(com.example.fordfocusdpfscan.data.DpfData data) {
        return null;
    }
    
    public final void updatePersistentNotification(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.app.Notification notification) {
    }
    
    /**
     * EGT ≥ 450°C — Do not stop engine (could be passive regen).
     */
    public final void notifyWarning(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    /**
     * EGT ≥ 550°C or ECU flag — Active regeneration confirmed.
     */
    public final void notifyActive(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    /**
     * Regen cycle completed — EGT dropped below safe threshold.
     */
    public final void notifyCompleted(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    /**
     * BLE connection to OBD dongle lost — silent popup, no sound.
     */
    public final void notifyBleLost(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    /**
     * BLE successfully connected — silent popup, no sound.
     */
    public final void notifyBleConnected(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String deviceName) {
    }
    
    /**
     * Posts a heads-up regen notification on CHANNEL_REGEN.
     * Plays dpf_monitor_sound.mp3 automatically (set on the channel).
     * Auto-dismisses after [EVENT_TIMEOUT_MS].
     */
    private final void postEventNotification(android.content.Context context, java.lang.String title, java.lang.String text, int color, int priority) {
    }
    
    /**
     * Posts a silent connection notification on CHANNEL_CONNECTION.
     * No sound, no vibration — just a heads-up popup.
     */
    private final void postConnectionNotification(android.content.Context context, java.lang.String title, java.lang.String text, int color) {
    }
}