package com.example.fordfocusdpfscan.car

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import com.example.fordfocusdpfscan.service.DpfForegroundService

// ═══════════════════════════════════════════════════════════════════════════════
// DpfSession.kt — Android Auto session manager.
//
// A Session is created for each Android Auto connection lifecycle.
// Responsibilities:
//   1. Provide the first screen to display (DpfScreen).
//   2. Ensure the BLE foreground service is running when the car connects —
//      even if the user didn't open the phone app first.
// ═══════════════════════════════════════════════════════════════════════════════

class DpfSession : Session() {

    /**
     * Called when Android Auto needs the first screen to display.
     * This is equivalent to Activity.setContentView().
     *
     * We also ensure the BLE foreground service is started here, so that if the
     * user plugs in Android Auto without having opened the phone app, monitoring
     * still starts automatically.
     */
    override fun onCreateScreen(intent: Intent): Screen {
        // Start the BLE foreground service (safe to call if already running)
        val serviceIntent = Intent(carContext, DpfForegroundService::class.java).apply {
            action = DpfForegroundService.ACTION_CONNECT
        }
        carContext.startForegroundService(serviceIntent)

        // Return the main dashboard screen
        return DpfScreen(carContext)
    }
}
