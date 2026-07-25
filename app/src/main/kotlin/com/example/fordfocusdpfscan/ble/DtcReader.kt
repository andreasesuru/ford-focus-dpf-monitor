package com.example.fordfocusdpfscan.ble

import android.util.Log
import com.example.fordfocusdpfscan.data.DtcCode
import com.example.fordfocusdpfscan.data.DtcResult
import kotlinx.coroutines.delay

// ═══════════════════════════════════════════════════════════════════════════════
// DtcReader.kt — Reads and clears engine (PCM) Diagnostic Trouble Codes.
//
// Uses the same ELM327 transport as the poller via BleManager.sendRawCommand().
// The live polling loop MUST be paused first (done here) so the responses aren't
// consumed by the shared response channel.
//
// Services (header already set to 7E0 = PCM during ELM init):
//   03 → stored/confirmed   07 → pending   0A → permanent   04 → clear
//
// CAN (ISO 15765-4) response layout for 03/07/0A:
//   <mode+0x40> <count> <DTC1_hi> <DTC1_lo> ... (00 00 padding ignored)
// ═══════════════════════════════════════════════════════════════════════════════

class DtcReader(private val bleManager: BleManager) {

    companion object {
        private const val TAG = "FOCUS_DTC"
        private const val TIMEOUT_MS = 3000L
    }

    /** Reads stored (03), pending (07) and permanent (0A) codes. */
    suspend fun readAll(): DtcResult {
        bleManager.pausePolling()
        delay(300)   // let the in-flight poll response drain (matches EcuPidScanner)   // let the current in-flight command settle
        try {
            val stored    = readMode("03")
            delay(80)
            val pending   = readMode("07")
            delay(80)
            val permanent = readMode("0A")
            return DtcResult(stored, pending, permanent)
        } finally {
            bleManager.resumePolling()
        }
    }

    /** Clears all stored codes, freeze frames and resets the MIL (service 04). */
    suspend fun clearCodes(): Boolean {
        bleManager.pausePolling()
        delay(300)   // let the in-flight poll response drain (matches EcuPidScanner)
        try {
            val resp = bleManager.sendRawCommand("04", TIMEOUT_MS) ?: return false
            // Positive response is 0x44.
            val ok = bleManager.hexStringToBytes(resp)
                ?.any { (it.toInt() and 0xFF) == 0x44 } ?: false
            Log.d(TAG, "Clear codes → '$resp' (ok=$ok)")
            return ok
        } finally {
            bleManager.resumePolling()
        }
    }

    private suspend fun readMode(mode: String): List<DtcCode> {
        val resp = bleManager.sendRawCommand(mode, TIMEOUT_MS) ?: return emptyList()
        val codes = parse(mode, resp)
        Log.d(TAG, "Mode $mode → '$resp' → $codes")
        return codes
    }

    /**
     * Parses a service-03/07/0A response into DTC strings.
     * Locates the mode-response byte (mode + 0x40), skips the CAN count byte,
     * then decodes the remaining bytes in pairs (skipping 00 00 padding).
     */
    private fun parse(mode: String, resp: String): List<DtcCode> {
        val bytes = bleManager.hexStringToBytes(resp) ?: return emptyList()
        if (bytes.isEmpty()) return emptyList()

        val respMode = (mode.toInt(16) + 0x40) and 0xFF
        val modeIdx = bytes.indexOfFirst { (it.toInt() and 0xFF) == respMode }
        if (modeIdx < 0) return emptyList()

        // Skip the mode byte and the CAN DTC-count byte that follows it,
        // then read the rest in 2-byte pairs (ignoring 00 00 padding).
        var i = modeIdx + 2
        val out = mutableListOf<DtcCode>()
        while (i + 1 < bytes.size) {
            val a = bytes[i].toInt() and 0xFF
            val b = bytes[i + 1].toInt() and 0xFF
            i += 2
            if (a == 0 && b == 0) continue          // padding
            out.add(DtcCode(decode(a, b)))
        }
        return out.distinctBy { it.code }
    }

    /** Decodes a 2-byte DTC into its "P0171"-style string. */
    private fun decode(a: Int, b: Int): String {
        val type = when (a shr 6) {
            0 -> "P"; 1 -> "C"; 2 -> "B"; else -> "U"
        }
        val d1 = (a shr 4) and 0x3
        val d2 = a and 0xF
        val d3 = (b shr 4) and 0xF
        val d4 = b and 0xF
        return "%s%d%X%X%X".format(type, d1, d2, d3, d4)
    }
}
