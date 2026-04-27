package com.example.fordfocusdpfscan.car

import android.util.Log
import androidx.car.app.AppInfo
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
//
// WORKAROUND — Car App Library 1.4.0 manifest read bug:
//   CarAppService.getAppInfo() reads androidx.car.app.minCarApiLevel via
//   Bundle.getInt() — this returns Integer.MIN_VALUE on certain device/host
//   combinations even when the meta-data is correctly declared.
//   getAppInfo() is package-private and cannot be overridden from outside
//   the library package.  Instead, we inject a pre-built AppInfo into the
//   parent's cached field via reflection inside init{}, before the host
//   ever calls getAppInfo().
//
//   AppInfo(minCarAppApiLevel, latestCarAppApiLevel, libraryDisplayVersion)
//     • min    = 2  → matches <meta-data android:value="@integer/min_car_api_level">
//     • latest = 4  → conservative cap (Car App Library 1.4.0)
//     • version     → BuildConfig.VERSION_NAME (visible in AA host logs)
// ═══════════════════════════════════════════════════════════════════════════════

class DpfCarAppService : CarAppService() {

    private val TAG = "DpfCarAppService"

    init {
        injectAppInfo()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Reflection injection — bypasses the broken Bundle.getInt() manifest read.
    //
    // AppInfo's (int, int, String) constructor is package-private.  Since Car
    // App Library lives in the APK's own classpath (not the Android framework
    // boot classpath), hidden-API restrictions do NOT apply — setAccessible(true)
    // succeeds at runtime on all Android versions ≥ 8.0.
    //
    // We search by field type (AppInfo) rather than by name so the approach
    // survives minor internal renames across library patch releases.
    // ─────────────────────────────────────────────────────────────────────────
    private fun injectAppInfo() {
        try {
            // Build AppInfo(minLevel=2, latestLevel=4, displayVersion)
            val ctor = AppInfo::class.java.getDeclaredConstructor(
                Int::class.javaPrimitiveType!!,
                Int::class.javaPrimitiveType!!,
                String::class.java
            )
            ctor.isAccessible = true
            val appInfo = ctor.newInstance(2, 4, BuildConfig.VERSION_NAME) as AppInfo

            // Walk the CarAppService class hierarchy searching for the AppInfo
            // cache field, then set it so getAppInfo() returns our value without
            // ever touching the (broken) manifest read path.
            var clazz: Class<*>? = CarAppService::class.java
            var injected = false
            while (clazz != null && !injected) {
                for (field in clazz.declaredFields) {
                    if (field.type == AppInfo::class.java) {
                        field.isAccessible = true
                        field.set(this, appInfo)
                        Log.d(TAG, "AppInfo injected into '${field.name}' " +
                                "(min=2, latest=4, v=${BuildConfig.VERSION_NAME})")
                        injected = true
                        break
                    }
                }
                clazz = clazz.superclass
            }

            if (!injected) {
                Log.w(TAG, "No AppInfo field found in CarAppService hierarchy — " +
                        "falling back to manifest read (may crash)")
            }

        } catch (e: Exception) {
            Log.e(TAG, "AppInfo injection failed — manifest read will be attempted: ${e.message}")
        }
    }

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
