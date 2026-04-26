package com.example.fordfocusdpfscan.car;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u000b\u001a\u00020\fH\u0002J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0006H\u0002J\u0010\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0006H\u0002J\u0010\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0006H\u0002J\u0010\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0006H\u0002J\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0002J\b\u0010\u0019\u001a\u00020\u001aH\u0016J\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\b2\u0006\u0010\u001e\u001a\u00020\bH\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001f"}, d2 = {"Lcom/example/fordfocusdpfscan/car/DpfScreen;", "Landroidx/car/app/Screen;", "carContext", "Landroidx/car/app/CarContext;", "(Landroidx/car/app/CarContext;)V", "currentData", "Lcom/example/fordfocusdpfscan/data/DpfData;", "previousRegenStatus", "Lcom/example/fordfocusdpfscan/data/RegenStatus;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "buildActionStrip", "Landroidx/car/app/model/ActionStrip;", "buildHistoryRow", "Landroidx/car/app/model/Row;", "data", "buildLoadRow", "buildRegenRow", "buildTemperaturesRow", "coloredSpan", "Landroid/text/SpannableString;", "text", "", "color", "Landroidx/car/app/model/CarColor;", "onGetTemplate", "Landroidx/car/app/model/Template;", "showCarToast", "", "old", "new", "app_debug"})
public final class DpfScreen extends androidx.car.app.Screen {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull()
    private com.example.fordfocusdpfscan.data.RegenStatus previousRegenStatus = com.example.fordfocusdpfscan.data.RegenStatus.INACTIVE;
    @org.jetbrains.annotations.NotNull()
    private com.example.fordfocusdpfscan.data.DpfData currentData;
    
    public DpfScreen(@org.jetbrains.annotations.NotNull()
    androidx.car.app.CarContext carContext) {
        super(null);
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public androidx.car.app.model.Template onGetTemplate() {
        return null;
    }
    
    /**
     * Row 1 — DPF Load percentage with color-coded value.
     */
    private final androidx.car.app.model.Row buildLoadRow(com.example.fordfocusdpfscan.data.DpfData data) {
        return null;
    }
    
    /**
     * Row 2 — Regeneration status with color and safety message.
     */
    private final androidx.car.app.model.Row buildRegenRow(com.example.fordfocusdpfscan.data.DpfData data) {
        return null;
    }
    
    /**
     * Row 3 — Coolant temperature + EGT side by side.
     */
    private final androidx.car.app.model.Row buildTemperaturesRow(com.example.fordfocusdpfscan.data.DpfData data) {
        return null;
    }
    
    /**
     * Row 4 — Last regen km + last service km.
     */
    private final androidx.car.app.model.Row buildHistoryRow(com.example.fordfocusdpfscan.data.DpfData data) {
        return null;
    }
    
    private final androidx.car.app.model.ActionStrip buildActionStrip() {
        return null;
    }
    
    private final void showCarToast(com.example.fordfocusdpfscan.data.RegenStatus old, com.example.fordfocusdpfscan.data.RegenStatus p1_54480) {
    }
    
    private final android.text.SpannableString coloredSpan(java.lang.String text, androidx.car.app.model.CarColor color) {
        return null;
    }
}