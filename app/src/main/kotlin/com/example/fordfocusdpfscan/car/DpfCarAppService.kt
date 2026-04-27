package com.example.fordfocusdpfscan.car

import android.util.Log
import androidx.car.app.AppInfo
import androidx.car.app.CarAppApiLevels
import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import com.example.fordfocusdpfscan.BuildConfig

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

    private val TAG = "DpfCarAppService"

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

    /**
     * Override getAppInfo() to work around a persistent issue where
     * Car App Library 1.4.0 fails to read androidx.car.app.minCarApiLevel
     * from the manifest meta-data via Bundle.getInt(), throwing:
     *   IllegalArgumentException: Min API level not declared in manifest
     *
     * Primary path: calls super (reads from manifest as normal).
     * Fallback: if the manifest read fails, constructs AppInfo directly
     * via reflection so the session can still start.
     */
    override fun getAppInfo(): AppInfo {
        return try {
            super.getAppInfo()
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "getAppInfo() manifest read failed, using reflection fallback: ${e.message}")
            try {
                // AppInfo has a package-private constructor:
                // AppInfo(int minCarAppApiLevel, int latestCarAppApiLevel, String libraryDisplayVersion)
                val constructor = AppInfo::class.java.getDeclaredConstructor(
                    Int::class.javaPrimitiveType!!,
                    Int::class.javaPrimitiveType!!,
                    String::class.java
                )
                constructor.isAccessible = true
                constructor.newInstance(
                    2,                          // minCarAppApiLevel — must match manifest
                    CarAppApiLevels.LATEST,     // latestCarAppApiLevel — from the library
                    BuildConfig.VERSION_NAME    // libraryDisplayVersion — shown in AA logs
                ) as AppInfo
            } catch (reflectEx: Exception) {
                Log.e(TAG, "Reflection fallback also failed: ${reflectEx.message}")
                throw e  // rethrow original if reflection also fails
            }
        }
    }
}
