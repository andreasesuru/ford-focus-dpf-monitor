package com.example.fordfocusdpfscan.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0002J\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\b\u0010\u0013\u001a\u00020\fH\u0002J\u0012\u0010\u0014\u001a\u00020\f2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0014J\b\u0010\u0017\u001a\u00020\fH\u0014J\u0010\u0010\u0018\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0002J\b\u0010\u0019\u001a\u00020\fH\u0002J\b\u0010\u001a\u001a\u00020\fH\u0002J\u0010\u0010\u001b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/example/fordfocusdpfscan/ui/EcuScanActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/example/fordfocusdpfscan/databinding/ActivityEcuScanBinding;", "lastLogPhase", "Lcom/example/fordfocusdpfscan/data/ScanPhase;", "renderedResultCount", "", "scanner", "Lcom/example/fordfocusdpfscan/ble/EcuPidScanner;", "appendNewResults", "", "state", "Lcom/example/fordfocusdpfscan/data/ScanState;", "formatResult", "", "result", "Lcom/example/fordfocusdpfscan/data/PidResult;", "observeScanState", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onScanCompleted", "setupButtons", "setupToolbar", "updateStats", "app_debug"})
public final class EcuScanActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.example.fordfocusdpfscan.databinding.ActivityEcuScanBinding binding;
    @org.jetbrains.annotations.Nullable()
    private com.example.fordfocusdpfscan.ble.EcuPidScanner scanner;
    @org.jetbrains.annotations.Nullable()
    private com.example.fordfocusdpfscan.data.ScanPhase lastLogPhase;
    private int renderedResultCount = 0;
    
    public EcuScanActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    private final void setupToolbar() {
    }
    
    private final void setupButtons() {
    }
    
    /**
     * Collects [EcuScanRepository.scanState] and:
     *  • updates stats (phase label, progress bar, counts)
     *  • appends only NEW result lines to the log TextView
     *  • auto-scrolls the ScrollView to the bottom
     */
    private final void observeScanState() {
    }
    
    private final void updateStats(com.example.fordfocusdpfscan.data.ScanState state) {
    }
    
    /**
     * Appends only the results that have been added since the last render.
     * This avoids rebuilding the entire log on every emission.
     */
    private final void appendNewResults(com.example.fordfocusdpfscan.data.ScanState state) {
    }
    
    /**
     * Formats a single [PidResult] line for the monospace log.
     * Format: [✓] 22 11 49  62 11 49 00 1E = ~1.5 g soot  [★★★ DPF Soot]
     */
    private final java.lang.String formatResult(com.example.fordfocusdpfscan.data.PidResult result) {
        return null;
    }
    
    private final void onScanCompleted(com.example.fordfocusdpfscan.data.ScanState state) {
    }
}