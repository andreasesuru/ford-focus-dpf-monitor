package com.example.fordfocusdpfscan.car

import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

// ═══════════════════════════════════════════════════════════════════════════════
// DpfCarAppService.kt — Android Auto entry point.
//
// The CarAppService is the equivalent of an Activity for Android Auto.
// It is declared in AndroidManifest.xml with the CarAppService intent-filter,
// and Android Auto instantiates it when the user opens the app in the car.
//
// All it does is return a new DpfSession — the session manages the screen stack.
// ═══════════════════════════════════════════════════════════════════════════════

class DpfCarAppService : CarAppService() {

    /**
     * Returns a [HostValidator] that allows connections from all Android Auto hosts.
     * For production / Play Store release, restrict to known Google hosts using
     * [HostValidator.ALLOW_ALL_HOSTS_VALIDATOR] only during development.
     */
    override fun createHostValidator(): HostValidator =
        HostValidator.ALLOW_ALL_HOSTS_VALIDATOR

    /**
     * Called by the Android Auto host when a new session starts
     * (i.e., the user connects to the car or opens the app in AA).
     */
    override fun onCreateSession(): Session = DpfSession()
}
