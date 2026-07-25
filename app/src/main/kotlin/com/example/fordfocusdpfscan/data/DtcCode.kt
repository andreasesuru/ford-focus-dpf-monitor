package com.example.fordfocusdpfscan.data

// ═══════════════════════════════════════════════════════════════════════════════
// DtcCode.kt — Diagnostic Trouble Code model + description database.
//
// DTCs are read from the engine PCM (header 7E0) via OBD services:
//   03 — stored / confirmed (MIL on)
//   07 — pending (current drive cycle, not yet confirmed)
//   0A — permanent (cannot be cleared until the ECU re-verifies the repair)
//
// Descriptions are in Italian and focus on the codes most relevant to the
// Ford Focus 1.5 TDCi (EDC17C70): DPF, EGR, common-rail, turbo/boost, glow plugs.
// Unknown codes fall back to a generic message with the raw code.
// ═══════════════════════════════════════════════════════════════════════════════

/** A single decoded DTC, e.g. "P2002". */
data class DtcCode(val code: String) {
    val description: String get() = DtcDatabase.describe(code)
}

/** Result of a full DTC read across the three services. */
data class DtcResult(
    val stored: List<DtcCode>,
    val pending: List<DtcCode>,
    val permanent: List<DtcCode>
) {
    val total: Int get() = stored.size + pending.size + permanent.size
    val isEmpty: Boolean get() = total == 0
}

object DtcDatabase {

    // Common powertrain codes for a diesel Focus. Not exhaustive by design —
    // unknown codes still display with a helpful fallback message.
    private val DESCRIPTIONS: Map<String, String> = mapOf(
        // ── DPF / particolato ────────────────────────────────────────────────
        "P2002" to "Efficienza DPF sotto soglia (banco 1)",
        "P2003" to "Efficienza DPF sotto soglia (banco 2)",
        "P242F" to "DPF — accumulo cenere eccessivo",
        "P2452" to "Sensore pressione differenziale DPF — circuito",
        "P2453" to "Sensore pressione differenziale DPF — segnale anomalo",
        "P2454" to "Sensore pressione differenziale DPF — segnale basso",
        "P2455" to "Sensore pressione differenziale DPF — segnale alto",
        "P244A" to "Pressione differenziale DPF troppo bassa",
        "P244B" to "Pressione differenziale DPF troppo alta",
        "P2458" to "Durata rigenerazione DPF anomala",
        "P2459" to "Frequenza rigenerazione DPF anomala",
        "P2463" to "DPF — accumulo particolato eccessivo (soot)",
        "P1206" to "Volume particolato DPF elevato — rigenerazione necessaria",
        // ── EGR ──────────────────────────────────────────────────────────────
        "P0401" to "Flusso EGR insufficiente",
        "P0402" to "Flusso EGR eccessivo",
        "P0403" to "Circuito valvola EGR",
        "P0404" to "Valvola EGR — range/prestazioni",
        "P0405" to "Sensore posizione EGR — segnale basso",
        "P0406" to "Sensore posizione EGR — segnale alto",
        "P0409" to "Sensore posizione EGR — circuito",
        "P046C" to "Sensore posizione EGR 'A' — prestazioni",
        // ── Common rail / iniezione ──────────────────────────────────────────
        "P0087" to "Pressione rail/sistema carburante troppo bassa",
        "P0088" to "Pressione rail/sistema carburante troppo alta",
        "P0089" to "Regolatore pressione carburante — prestazioni",
        "P0090" to "Regolatore pressione carburante 1 — circuito",
        "P0093" to "Perdita grande nel sistema carburante rilevata",
        "P0094" to "Piccola perdita nel sistema carburante rilevata",
        "P0191" to "Sensore pressione rail — range/prestazioni",
        "P0192" to "Sensore pressione rail — segnale basso",
        "P0193" to "Sensore pressione rail — segnale alto",
        "P0201" to "Circuito iniettore cilindro 1",
        "P0202" to "Circuito iniettore cilindro 2",
        "P0203" to "Circuito iniettore cilindro 3",
        "P0204" to "Circuito iniettore cilindro 4",
        "P0263" to "Cilindro 1 — bilanciamento/contributo",
        "P0266" to "Cilindro 2 — bilanciamento/contributo",
        "P0269" to "Cilindro 3 — bilanciamento/contributo",
        "P0272" to "Cilindro 4 — bilanciamento/contributo",
        "P1290" to "Pressione carburante — sovrapressione (Ford)",
        // ── Turbo / sovralimentazione ────────────────────────────────────────
        "P0234" to "Sovralimentazione turbo eccessiva (overboost)",
        "P0235" to "Sensore pressione turbo 'A' — circuito",
        "P0236" to "Sensore pressione turbo 'A' — range/prestazioni",
        "P0243" to "Attuatore wastegate turbo — circuito",
        "P0245" to "Solenoide wastegate turbo 'A' — segnale basso",
        "P0246" to "Solenoide wastegate turbo 'A' — segnale alto",
        "P0299" to "Sottopressione turbo (underboost)",
        "P132B" to "Controllo pressione turbo — sovrapressione (Ford)",
        // ── Aria / MAF / MAP ─────────────────────────────────────────────────
        "P0100" to "Circuito sensore portata aria (MAF)",
        "P0101" to "Sensore portata aria (MAF) — range/prestazioni",
        "P0102" to "Sensore portata aria (MAF) — segnale basso",
        "P0103" to "Sensore portata aria (MAF) — segnale alto",
        "P0107" to "Sensore pressione collettore (MAP) — segnale basso",
        "P0108" to "Sensore pressione collettore (MAP) — segnale alto",
        "P0112" to "Sensore temperatura aria aspirata — segnale basso",
        "P0113" to "Sensore temperatura aria aspirata — segnale alto",
        // ── Candelette / avviamento ──────────────────────────────────────────
        "P0670" to "Modulo controllo candelette — circuito",
        "P0671" to "Candeletta cilindro 1 — circuito",
        "P0672" to "Candeletta cilindro 2 — circuito",
        "P0673" to "Candeletta cilindro 3 — circuito",
        "P0674" to "Candeletta cilindro 4 — circuito",
        // ── Temperature gas di scarico (EGT) ─────────────────────────────────
        "P0544" to "Sensore temperatura gas scarico 1 — circuito",
        "P0546" to "Sensore temperatura gas scarico 1 — segnale alto",
        "P242C" to "Sensore temperatura gas scarico 3 — segnale basso",
        "P242D" to "Sensore temperatura gas scarico 3 — segnale alto",
        // ── Refrigerante / termostato ────────────────────────────────────────
        "P0128" to "Termostato — refrigerante sotto temperatura regolazione",
        "P0116" to "Sensore temperatura refrigerante — range/prestazioni",
        "P0117" to "Sensore temperatura refrigerante — segnale basso",
        "P0118" to "Sensore temperatura refrigerante — segnale alto",
        // ── Generici ─────────────────────────────────────────────────────────
        "P0016" to "Correlazione albero motore/albero a camme (banco 1)",
        "P0335" to "Sensore posizione albero motore 'A' — circuito",
        "P0340" to "Sensore posizione albero a camme 'A' — circuito",
        "P1000" to "Monitor OBD non completati (test di prontezza in corso)",
        "U0100" to "Persa comunicazione con la centralina motore (ECM/PCM)"
    )

    fun describe(code: String): String =
        DESCRIPTIONS[code.uppercase()] ?: "Codice non in archivio — verifica in officina (cerca \"$code\")"
}
