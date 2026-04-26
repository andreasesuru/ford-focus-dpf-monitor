package com.example.fordfocusdpfscan.sound;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\n\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002J\u0010\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000e\u001a\u00020\u000fJ\u001e\u0010\u0010\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012H\u0002J\u0014\u0010\u0014\u001a\u00020\t*\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0006H\u0002J\u0014\u0010\u0017\u001a\u00020\t*\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/example/fordfocusdpfscan/sound/DpfChimeGenerator;", "", "()V", "FILE_NAME", "", "SAMPLE_RATE", "", "TAG", "generateWav", "", "outputFile", "Ljava/io/File;", "getOrCreate", "Landroid/net/Uri;", "context", "Landroid/content/Context;", "writeWavFile", "samples", "", "", "writeIntLE", "Ljava/io/DataOutputStream;", "value", "writeShortLE", "app_debug"})
public final class DpfChimeGenerator {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "FOCUS_Chime";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String FILE_NAME = "dpf_chime.wav";
    private static final int SAMPLE_RATE = 44100;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.fordfocusdpfscan.sound.DpfChimeGenerator INSTANCE = null;
    
    private DpfChimeGenerator() {
        super();
    }
    
    /**
     * Returns the [Uri] of the chime WAV file, generating it on first call.
     * The file is stored in [Context.filesDir] and exposed via [FileProvider].
     *
     * @return A content:// URI usable as notification sound, or null on failure.
     */
    @org.jetbrains.annotations.Nullable()
    public final android.net.Uri getOrCreate(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    /**
     * Synthesises the 3-tone chime and writes it as a standard PCM WAV file.
     *
     * Tone sequence:
     *  1. E5 — 659 Hz, 280 ms   (first ding)
     *  2. A5 — 880 Hz, 280 ms   (second ding)
     *  3. C#6 — 1109 Hz, 390 ms (resonant closing tone)
     * Silence gap between tones: 40 ms
     */
    private final void generateWav(java.io.File outputFile) {
    }
    
    /**
     * Writes [samples] into a valid WAV file at [outputFile].
     * Format: RIFF / WAVE / fmt  (PCM, mono, 44100 Hz, 16-bit) / data.
     */
    private final void writeWavFile(java.io.File outputFile, java.util.List<java.lang.Short> samples) {
    }
    
    private final void writeIntLE(java.io.DataOutputStream $this$writeIntLE, int value) {
    }
    
    private final void writeShortLE(java.io.DataOutputStream $this$writeShortLE, int value) {
    }
}