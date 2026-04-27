package com.example.fordfocusdpfscan

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.fordfocusdpfscan.car.NotificationHelper
import com.example.fordfocusdpfscan.data.DpfRepository

// ═══════════════════════════════════════════════════════════════════════════════
// FocusApp.kt — Application class.
//
// Executed once when the process starts, before any Activity or Service.
// Responsible for one-time global initialisation:
//   1. DpfRepository — loads persisted history from SharedPreferences.
//   2. NotificationHelper — creates notification channels (idempotent).
//   3. DpfChimeGenerator — the chime WAV is generated lazily on first access.
// ═══════════════════════════════════════════════════════════════════════════════

class FocusApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Force dark mode regardless of system setting — the app uses a custom
        // dark-blue theme that must not be inverted by the DayNight mechanism.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        // Initialise the data repository with the application context
        // so it can access SharedPreferences for last-regen / last-service km
        DpfRepository.init(this)

        // Create notification channels — must happen before any notification is posted
        NotificationHelper.createChannels(this)

        // Pre-warm the Ford Focus icon bitmap on a background thread.
        // ic_ford_focus.png is 5 MB — decoding it on the main thread when the first
        // regen notification fires caused a ServiceANR. Doing it here at startup
        // (background thread) ensures it is ready before any notification fires.
        NotificationHelper.warmIconCache(this)
    }
}
