package com.example.fordfocusdpfscan.sound

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.DataOutputStream
import java.io.File
import kotlin.math.PI
import kotlin.math.sin

// ═══════════════════════════════════════════════════════════════════════════════
// DpfChimeGenerator.kt — Generates a unique 3-tone ascending chime WAV file.
//
// Why generate programmatically?
//   • No need to bundle a binary audio file in assets.
//   • The sound is 100% unique to this app — never matches the user's chosen
//     notification ringtone.
//   • Generated once on first launch, reused on subsequent launches.
//
// Sound design:
//   • Three ascending musical tones: E5 (659 Hz) → A5 (880 Hz) → C#6 (1109 Hz)
//   • This forms a major-third + major-third pattern — pleasant car-chime feel.
//   • Each tone has a smooth attack (10 ms) and release (60 ms) envelope.
//   • Total duration: ~0.95 s — short enough not to distract, long enough to notice.
//   • 44100 Hz sample rate, 16-bit PCM mono WAV — universally supported.
// ═══════════════════════════════════════════════════════════════════════════════

object DpfChimeGenerator {

    private const val TAG = "FOCUS_Chime"
    private const val FILE_NAME = "dpf_chime.wav"
    private const val SAMPLE_RATE = 44100

    /**
     * Returns the [Uri] of the chime WAV file, generating it on first call.
     * The file is stored in [Context.filesDir] and exposed via [FileProvider].
     *
     * @return A content:// URI usable as notification sound, or null on failure.
     */
    fun getOrCreate(context: Context): Uri? {
        return try {
            val file = File(context.filesDir, FILE_NAME)
            if (!file.exists() || file.length() == 0L) {
                Log.d(TAG, "Generating chime WAV for the first time…")
                generateWav(file)
                Log.d(TAG, "Chime WAV created at ${file.absolutePath} (${file.length()} bytes)")
            }
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate chime: ${e.message}")
            null
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // WAV generation
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Synthesises the 3-tone chime and writes it as a standard PCM WAV file.
     *
     * Tone sequence:
     *   1. E5 — 659 Hz, 280 ms   (first ding)
     *   2. A5 — 880 Hz, 280 ms   (second ding)
     *   3. C#6 — 1109 Hz, 390 ms (resonant closing tone)
     * Silence gap between tones: 40 ms
     */
    private fun generateWav(outputFile: File) {
        data class Tone(val freq: Double, val durationMs: Int)

        val tones = listOf(
            Tone(659.0,  280),   // E5
            Tone(880.0,  280),   // A5
            Tone(1109.0, 390)    // C#6
        )
        val gapMs = 40

        // Build the full sample buffer
        val allSamples = mutableListOf<Short>()

        for ((index, tone) in tones.withIndex()) {
            val numSamples = SAMPLE_RATE * tone.durationMs / 1000
            val attackSamples  = (SAMPLE_RATE * 0.010).toInt()  // 10 ms attack
            val releaseSamples = (SAMPLE_RATE * 0.060).toInt()  // 60 ms release

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE

                // Smooth amplitude envelope (attack → sustain → release)
                val envelope = when {
                    i < attackSamples  -> i.toDouble() / attackSamples
                    i > numSamples - releaseSamples ->
                        (numSamples - i).toDouble() / releaseSamples
                    else -> 1.0
                }.coerceIn(0.0, 1.0)

                val sample = (envelope * 0.72 * Short.MAX_VALUE *
                              sin(2.0 * PI * tone.freq * t)).toInt()
                allSamples.add(sample.toShort())
            }

            // Add silence gap between tones (not after the last one)
            if (index < tones.lastIndex) {
                val gapSamples = SAMPLE_RATE * gapMs / 1000
                repeat(gapSamples) { allSamples.add(0) }
            }
        }

        // Write WAV container (little-endian PCM)
        writeWavFile(outputFile, allSamples)
    }

    /**
     * Writes [samples] into a valid WAV file at [outputFile].
     * Format: RIFF / WAVE / fmt  (PCM, mono, 44100 Hz, 16-bit) / data.
     */
    private fun writeWavFile(outputFile: File, samples: List<Short>) {
        val bitsPerSample  = 16
        val channels       = 1
        val byteRate       = SAMPLE_RATE * channels * bitsPerSample / 8
        val blockAlign     = channels * bitsPerSample / 8
        val dataSize       = samples.size * 2          // 2 bytes per 16-bit sample
        val riffChunkSize  = 36 + dataSize             // total file size - 8 bytes

        DataOutputStream(outputFile.outputStream().buffered()).use { out ->
            // ── RIFF chunk descriptor ──────────────────────────────────────────
            out.writeBytes("RIFF")
            out.writeIntLE(riffChunkSize)
            out.writeBytes("WAVE")

            // ── fmt sub-chunk ─────────────────────────────────────────────────
            out.writeBytes("fmt ")
            out.writeIntLE(16)               // PCM fmt sub-chunk size
            out.writeShortLE(1)              // AudioFormat = PCM (linear quantization)
            out.writeShortLE(channels)
            out.writeIntLE(SAMPLE_RATE)
            out.writeIntLE(byteRate)
            out.writeShortLE(blockAlign)
            out.writeShortLE(bitsPerSample)

            // ── data sub-chunk ────────────────────────────────────────────────
            out.writeBytes("data")
            out.writeIntLE(dataSize)

            // PCM sample data — little-endian 16-bit signed
            for (sample in samples) {
                out.write(sample.toInt() and 0xFF)
                out.write((sample.toInt() shr 8) and 0xFF)
            }
        }
    }

    // ── Little-endian write helpers ───────────────────────────────────────────

    private fun DataOutputStream.writeIntLE(value: Int) {
        write(value         and 0xFF)
        write((value shr 8)  and 0xFF)
        write((value shr 16) and 0xFF)
        write((value shr 24) and 0xFF)
    }

    private fun DataOutputStream.writeShortLE(value: Int) {
        write(value        and 0xFF)
        write((value shr 8) and 0xFF)
    }
}
