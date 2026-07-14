# ═══════════════════════════════════════════════════════════════════════════════
# ProGuard / R8 rules for DPF Monitor.
#
# NOTE: build.gradle references this file, but code shrinking is only applied when
#       `minifyEnabled true` is set on the release buildType. It is currently OFF.
#
#       To ship a smaller APK, flip `minifyEnabled true` in app/build.gradle and
#       run a FULL smoke test on the car (BLE connect, Android Auto screen, regen
#       notification, history export) — R8 can strip reflectively-used code, and
#       these rules cover the known cases below.
# ═══════════════════════════════════════════════════════════════════════════════

# ── Android Auto / Car App Library ──────────────────────────────────────────────
# The host discovers the CarAppService via the manifest and instantiates Screens
# reflectively through the Session. Keep the entry points intact.
-keep class com.example.fordfocusdpfscan.car.DpfCarAppService { *; }
-keep class com.example.fordfocusdpfscan.car.** { *; }
-keep class * extends androidx.car.app.CarAppService { *; }
-keep class * extends androidx.car.app.Screen { *; }

# ── Room entities & DAOs ────────────────────────────────────────────────────────
# Room generates implementations that reference entity fields by name.
-keep class com.example.fordfocusdpfscan.data.db.** { *; }

# ── MPAndroidChart ──────────────────────────────────────────────────────────────
# The charting library uses some reflection internally.
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**

# ── Classic Bluetooth SPP reflection fallback (BleManager) ──────────────────────
# createRfcommSocket(int) is called via reflection on the framework class
# android.bluetooth.BluetoothDevice. Framework classes are not shrunk, so no keep
# is strictly required, but suppress any related warnings.
-dontwarn android.bluetooth.**

# Kotlin metadata (safe default; keeps reflection-friendly signatures).
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
