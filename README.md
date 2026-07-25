# 🚗 DPF Monitor — Ford Focus 1.5 TDCi

<p align="center">
  <img src="screenshots/splash.jpg" width="220" alt="Splash Screen"/>
</p>

<p align="center">
  App Android per il monitoraggio in tempo reale del filtro antiparticolato (DPF)<br/>
  della Ford Focus 1.5 TDCi con centralina EDC17C70, tramite dongle OBD2 Bluetooth.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/versione-4.20-blue"/>
  <img src="https://img.shields.io/badge/Android-8.0%2B-green"/>
  <img src="https://img.shields.io/badge/Kotlin-2.x-purple"/>
  <img src="https://img.shields.io/badge/Android%20Auto-✓-orange"/>
</p>

---

## 📱 Screenshot

<p align="center">
  <img src="screenshots/monitor.jpg" width="220" alt="Monitor"/>
  <img src="screenshots/diagnostica.jpg" width="220" alt="Diagnostica"/>
  <img src="screenshots/storico.jpg" width="220" alt="Storico"/>
  <img src="screenshots/manutenzione.jpg" width="220" alt="Servizi"/>
</p>

## 🚗 Android Auto — Ford Focus Sync 3

<p align="center">
  <img src="screenshots/sync3.jpg" width="480" alt="DPF Monitor su Ford Focus Sync 3"/>
</p>

---

## ✨ Funzionalità

### 🔍 Monitor
- Gauge circolari per **DPF Load %** e **DPF Soot %** con colori in tempo reale
- **Delta P** (pressione differenziale) con range idle/marcia
- **Stato rigenerazione** automatico: Inattiva / Warning / Attiva / Completata
- Rilevamento regen **multi-segnale**: EGT ingresso ≥550 °C, **calo del soot** nel tempo, o esotermia del filtro (uscita ≫ ingresso) — cattura anche le regen a temperatura moderata
- Temperature: EGT, refrigerante
- Distanze ECU: odometro, km da ultima regen, km da ultimo tagliando

### 📡 Diagnostica
- Sensori motore live: RPM, velocità, carico motore, boost (MAP)
- Sezione DPF avanzata: Soot %, Load %, Delta P, EGT, ΔT pre-post DPF
- Sezione Alimentazione: **tensione batteria**, **pressione rail carburante**, **temperatura aria aspirata**
- Barra colorata di stato per ogni cella (verde / ambra / rossa)
- Hint contestuali con range normali per ogni parametro

### 🩺 Codici errore (DTC)
- Lettura spia motore dalla centralina: codici **confermati / in sospeso / permanenti**
- Descrizioni in italiano dei codici più comuni (DPF, EGR, common-rail, turbo, candelette)
- Cancellazione codici con conferma (spegne la spia motore)

### 🤖 Assistente AI (opzionale)
- Integrazione **Claude API** con chiave inserita dall'utente (resta solo sul telefono)
- **Spiega codici errore** in linguaggio naturale, con il contesto dei dati live
- **Analizza scansione ECU** per identificare PID sconosciuti (es. diluizione olio) e proporne la formula
- **Monitoraggio costi** in-app (stima locale dai token di ogni risposta)
- Modello selezionabile: Haiku 4.5 / Sonnet 5 / Opus 4.8

### 📋 Storico
- Registrazione automatica di ogni sessione di rigenerazione
- Grafico a barre Soot prima/dopo per le ultime 8 sessioni
- Card sessione con: data, km, rilevamento (🌡 via temperatura EGT), EGT picco, risultato
- **Salute DPF**: distanza media tra le rigenerazioni con indicatore di stato
- Export report HTML per il meccanico tramite share sheet

### 🔧 Servizi
- Promemoria manutenzione con card colorate: **verde / arancio / rosso** in base ai km rimanenti
- Barra di avanzamento km usati / intervallo per ogni promemoria
- **Tagliando olio gestito automaticamente** dalla centralina (PID 22 0542): si aggiorna da solo ad ogni connessione, senza input manuale — con **intervallo di avviso modificabile**
- Odometro robusto: ultimo valore valido memorizzato, disponibile anche offline, con filtro anti-glitch
- Aggiunta promemoria personalizzati (titolo, intervallo km, ultimo intervento)
- Pulsante **Fatto** con dialogo di conferma + registrazione km per azzerare il countdown
- Pulsanti **Modifica** ed **Elimina** per ogni promemoria
- Notifiche push a 1000 km, 500 km e a scadenza raggiunta (una sola volta per intervallo)

### 🔔 Notifiche
- Notifica persistente con stato DPF durante il monitoraggio (aggiornata ogni 5s)
- Allerta vibrazione + suono personalizzato su regen WARNING e ACTIVE
- Promemoria manutenzione a 1000 km / 500 km / scaduto
- Notifica silenziosa su connessione/disconnessione dongle
- Notifica timer cooldown turbo post-regen

### 🚗 Android Auto / Sync 3
- Dashboard con 3 righe: **Filtro DPF**, **Rigenerazione**, **Info Motore**
- Riga **Info Motore**: EGT ingresso · EGT uscita · temperatura motore
- Valori colorati (verde/giallo/rosso) in base alle soglie
- CarToast su ogni transizione di stato regen
- Tasto **Ricollega** per riconnettere il dongle senza toccare il telefono

---

## 🛠 Stack tecnico

| Componente | Tecnologia |
|---|---|
| Linguaggio | Kotlin |
| Connettività | Bluetooth LE (BLE) + SPP |
| Protocollo | OBD2 — ELM327 (PIDs Mode 01 + Ford 22xx) |
| Database | Room (SQLite) — v3 |
| Architettura | StateFlow + LifecycleService |
| UI | View Binding — RecyclerView, MPAndroidChart |
| Auto | Android Car App Library 1.4 (categoria IOT) |
| Notifiche | NotificationCompat — 4 canali |

---

## 🔌 Come funziona

```
Dongle OBD2 (ELM327 BLE)
        ↓ Bluetooth
BleManager — polling ogni ~1.5s
        ↓
DpfRepository (StateFlow<DpfData>)
        ↓              ↓              ↓
MainActivity    DpfScreen (Auto)  DpfForegroundService
  (gauge UI)    (ListTemplate)    (notifiche + storico + manutenzione)
```

1. Il dongle OBD2 si collega alla presa diagnostica della Focus
2. L'app interroga la ECU ogni ~1.5 secondi via BLE
3. I dati aggiornano la UI in tempo reale tramite StateFlow
4. Se viene rilevata una rigenerazione, viene registrata nel database Room
5. Il promemoria tagliando si aggiorna automaticamente dai km ECU
6. Su Android Auto, la dashboard è visibile sul display Sync 3 dell'auto

---

## 📋 PID OBD2 utilizzati

| PID | Descrizione | Confermato |
|---|---|---|
| `22 057B` | DPF Soot % | ✅ |
| `22 0579` | DPF Load % | ✅ |
| `01 7A` | Delta P (pressione differenziale) | ✅ |
| `22 050B` | Km dall'ultima rigenerazione | ✅ |
| `22 0542` | Km dall'ultimo cambio olio | ✅ |
| `22 DD01` | Odometro ECU | ✅ |
| `01 0C` | RPM | ✅ |
| `01 0D` | Velocità | ✅ |
| `01 05` | Temperatura refrigerante | ✅ |
| `01 0B` | Pressione collettore (boost) | ✅ |

---

## ⚙️ Requisiti

- Android 8.0+ (API 26)
- Dongle OBD2 Bluetooth (testato con **Android-Vlink** ELM327 BLE)
- Ford Focus 1.5 TDCi con centralina **EDC17C70**
- Per Android Auto: app installata tramite Google Play (Internal Testing)

---

## 👨‍💻 Sviluppatore

**Andrea Sesuru** · [github.com/andreasesuru](https://github.com/andreasesuru)

---

*Progetto personale — sviluppato per uso privato sul proprio veicolo.*
