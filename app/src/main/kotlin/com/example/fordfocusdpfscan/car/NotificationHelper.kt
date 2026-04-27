package com.example.fordfocusdpfscan.car

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import android.graphics.BitmapFactory
import androidx.car.app.notification.CarAppExtender
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fordfocusdpfscan.R
import com.example.fordfocusdpfscan.data.DpfData
import com.example.fordfocusdpfscan.data.RegenStatus
import com.example.fordfocusdpfscan.ui.MainActivity

// ═══════════════════════════════════════════════════════════════════════════════
// NotificationHelper.kt — Central factory for all app notifications.
//
// Three notification levels:
//   1. Persistent (NOTIF_ID_PERSISTENT) — always visible, updated with live data.
//   2. Regen event heads-up (NOTIF_ID_EVENT) — WARNING / ACTIVE / COMPLETED.
//      Plays the user's custom MP3 (res/raw/dpf_monitor_sound.mp3).
//   3. Connection event (NOTIF_ID_CONNECTION) — BLE connected / disconnected.
//      Silent — no sound, just a pop-up heads-up notification.
//   4. CarToast — fired in-car via the Android Auto Screen (see DpfScreen).
//
// Notification channels:
//   • CHANNEL_PERSISTENT  — IMPORTANCE_LOW  (silent status bar — always visible)
//   • CHANNEL_REGEN       — IMPORTANCE_HIGH + dpf_monitor_sound.mp3 + vibration
//   • CHANNEL_CONNECTION  — IMPORTANCE_DEFAULT, no sound (silent popup)
// ═══════════════════════════════════════════════════════════════════════════════

object NotificationHelper {

    private const val TAG = "FOCUS_Notif"

    // ── Notification IDs ──────────────────────────────────────────────────────
    const val NOTIF_ID_PERSISTENT  = 1001
    const val NOTIF_ID_EVENT       = 1002
    const val NOTIF_ID_CONNECTION  = 1003
    const val NOTIF_ID_SERVICE     = 1004   // tagliando / service reminder

    // ── Channel IDs ───────────────────────────────────────────────────────────
    private const val CHANNEL_PERSISTENT  = "focus_persistent"
    private const val CHANNEL_REGEN       = "focus_regen"        // custom MP3 sound
    private const val CHANNEL_CONNECTION  = "focus_connection"   // silent
    private const val CHANNEL_SERVICE     = "focus_service"      // silent, service reminders

    // ── Notification timeout ──────────────────────────────────────────────────
    private const val EVENT_TIMEOUT_MS = 5_000L   // heads-up dismisses after 5 s

    // ── Whether channels have been created ────────────────────────────────────
    private var channelsCreated = false

    // ── Cached icon bitmap — decoded once, reused for all notifications ───────
    // ic_ford_focus.png is 5 MB on disk; decoding it on every notification
    // call caused visible UI lag. We scale it to 128×128 dp and cache it.
    private var cachedIconBitmap: android.graphics.Bitmap? = null
    private fun getIconBitmap(context: Context): android.graphics.Bitmap {
        return cachedIconBitmap ?: run {
            val size = (128 * context.resources.displayMetrics.density).toInt()
            val raw = BitmapFactory.decodeResource(context.resources, R.drawable.ic_ford_focus)
            val scaled = android.graphics.Bitmap.createScaledBitmap(raw, size, size, true)
            if (scaled !== raw) raw.recycle()
            cachedIconBitmap = scaled
            scaled
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Channel creation — must be called before posting any notification
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Creates both notification channels. Safe to call multiple times (idempotent).
     * Call from Application.onCreate() or before the first notification.
     */
    fun createChannels(context: Context) {
        if (channelsCreated) return
        channelsCreated = true

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

        // ── Channel 1: Persistent (silent status bar) ─────────────────────────
        val persistentChannel = NotificationChannel(
            CHANNEL_PERSISTENT,
            "DPF Monitoring",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Live DPF status — always visible while monitoring"
            setSound(null, null)
            enableVibration(false)
        }

        // ── Channel 2: Regen alerts (user MP3 + vibration + LED) ─────────────
        val regenSoundUri = Uri.parse(
            "android.resource://${context.packageName}/${R.raw.dpf_monitor_sound}"
        )
        val regenChannel = NotificationChannel(
            CHANNEL_REGEN,
            context.getString(R.string.notif_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notif_channel_desc)
            val audioAttr = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            setSound(regenSoundUri, audioAttr)
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 200, 100, 200)
            enableLights(true)
            lightColor = Color.RED
            Log.d(TAG, "Regen channel sound: $regenSoundUri")
        }

        // ── Channel 3: Connection events (silent popup — no sound) ────────────
        val connectionChannel = NotificationChannel(
            CHANNEL_CONNECTION,
            "Connessione OBD",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifiche silenziose per connessione/disconnessione Bluetooth"
            setSound(null, null)
            enableVibration(false)
        }

        // ── Channel 4: Service reminders (silent popup — no sound) ───────────
        val serviceChannel = NotificationChannel(
            CHANNEL_SERVICE,
            "Promemoria tagliando",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Promemoria per prenotare il tagliando dal meccanico"
            setSound(null, null)
            enableVibration(false)
        }

        manager.createNotificationChannels(listOf(persistentChannel, regenChannel, connectionChannel, serviceChannel))
        Log.d(TAG, "Notification channels created")
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Level 1 — Persistent notification
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Builds (or rebuilds) the always-on foreground service notification.
     * Updated every time DpfRepository emits new data.
     */
    fun buildPersistentNotification(
        context: Context,
        dpfData: DpfData = DpfData()
    ): Notification {
        val tapIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Summary line shown in the notification body
        val summary = buildPersistentSummary(dpfData)

        return NotificationCompat.Builder(context, CHANNEL_PERSISTENT)
            .setSmallIcon(R.drawable.ic_car_header)
            .setContentTitle(context.getString(R.string.notif_persistent_title))
            .setContentText(summary)
            .setOngoing(true)             // Cannot be swiped away
            .setOnlyAlertOnce(true)       // No sound on updates
            .setContentIntent(tapIntent)
            .setSilent(true)
            .build()
    }

    private fun buildPersistentSummary(data: DpfData): String {
        val connected = if (data.bleConnected) "Connected" else "Disconnected"
        val soot = if (data.sootPercentage >= 0) "${"%.0f".format(data.sootPercentage)}%" else "– –"
        val egt  = if (data.egtCelsius >= 0) "${"%.0f".format(data.egtCelsius)}°C" else "– –"
        return "$connected  •  Soot: $soot  •  EGT: $egt"
    }

    fun updatePersistentNotification(context: Context, notification: Notification) {
        NotificationManagerCompat.from(context)
            .notify(NOTIF_ID_PERSISTENT, notification)
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Level 2 — Event heads-up notifications (all play custom chime)
    // ═════════════════════════════════════════════════════════════════════════

    /** EGT ≥ 450°C — Do not stop engine (could be passive regen). */
    fun notifyWarning(context: Context) {
        postEventNotification(
            context,
            title = context.getString(R.string.notif_warning_title),
            text  = context.getString(R.string.notif_warning_text),
            color = Color.parseColor("#F57F17")   // amber
        )
        Log.d(TAG, "⚠ WARNING notification posted")
    }

    /** EGT ≥ 550°C or ECU flag — Active regeneration confirmed. */
    fun notifyActive(context: Context) {
        postEventNotification(
            context,
            title    = context.getString(R.string.notif_active_title),
            text     = context.getString(R.string.notif_active_text),
            color    = Color.parseColor("#C62828"),   // red
            priority = NotificationCompat.PRIORITY_MAX
        )
        Log.d(TAG, "🔴 ACTIVE regen notification posted")
    }

    /** Regen cycle completed — EGT dropped below safe threshold. */
    fun notifyCompleted(context: Context) {
        postEventNotification(
            context,
            title = context.getString(R.string.notif_complete_title),
            text  = context.getString(R.string.notif_complete_text),
            color = Color.parseColor("#2E7D32")   // green
        )
        Log.d(TAG, "✅ COMPLETED regen notification posted")
    }

    /** BLE connection to OBD dongle lost — silent popup, no sound. */
    fun notifyBleLost(context: Context) {
        postConnectionNotification(
            context,
            title = context.getString(R.string.notif_ble_lost_title),
            text  = context.getString(R.string.notif_ble_lost_text),
            color = Color.parseColor("#546E7A")   // grey
        )
        Log.d(TAG, "📵 BLE lost notification posted (silent)")
    }

    /** BLE successfully connected — silent popup, no sound. */
    fun notifyBleConnected(context: Context, deviceName: String) {
        postConnectionNotification(
            context,
            title = "OBD Connesso",
            text  = "Connesso a $deviceName — monitoraggio attivo",
            color = Color.parseColor("#2E7D32")   // green
        )
        Log.d(TAG, "✅ BLE connected notification posted (silent)")
    }

    /**
     * Promemoria tagliando — si attiva quando km dall'ultimo cambio olio ≥ 10.000.
     * Silenziosa (niente suono), rimane fino a dismiss manuale.
     */
    fun notifyOilChangeReminder(context: Context, kmSinceOilChange: Long) {
        val tapIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_SERVICE)
            .setSmallIcon(R.drawable.ic_car_header)
            .setContentTitle("🔧 Tagliando in scadenza")
            .setContentText("Hai percorso %,d km dall'ultimo cambio olio — prenota dal meccanico.".format(kmSinceOilChange))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Hai percorso %,d km dall'ultimo cambio olio.\nSi consiglia di prenotare un tagliando entro breve.".format(kmSinceOilChange)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setColor(0xFFFF9F0A.toInt())   // amber
            .setColorized(true)
            .setAutoCancel(false)          // resta nella tendina finché non si fa il tagliando
            .setContentIntent(tapIntent)
            .setSilent(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID_SERVICE, notification)
            Log.d(TAG, "🔧 Oil change reminder sent at ${kmSinceOilChange} km")
        } catch (e: SecurityException) {
            Log.e(TAG, "POST_NOTIFICATIONS permission not granted: ${e.message}")
        }
    }

    // ─── Private factories ────────────────────────────────────────────────────

    /**
     * Posts a heads-up regen notification on CHANNEL_REGEN.
     * Plays dpf_monitor_sound.mp3 automatically (set on the channel).
     * Auto-dismisses after [EVENT_TIMEOUT_MS].
     *
     * On the car screen:
     *   - Popup heads-up with large Ford Focus icon
     *   - Plays through car speakers (IMPORTANCE_HIGH on the channel)
     *   - Tap → opens DpfScreen directly on the car display
     */
    private fun postEventNotification(
        context: Context,
        title: String,
        text: String,
        color: Int,
        priority: Int = NotificationCompat.PRIORITY_HIGH
    ) {
        // Phone tap: opens MainActivity on the handset
        val phoneTapIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Car tap: opens DpfCarAppService → shows DpfScreen on the car display
        val carTapIntent = PendingIntent.getService(
            context, NOTIF_ID_EVENT,
            Intent(context, DpfCarAppService::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_REGEN)
            .setSmallIcon(R.drawable.ic_car_header)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(priority)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setColor(color)
            .setColorized(true)
            .setAutoCancel(true)
            .setTimeoutAfter(EVENT_TIMEOUT_MS)
            .setOnlyAlertOnce(false)             // sound plays every regen event
            .setContentIntent(phoneTapIntent)
            .extend(
                CarAppExtender.Builder()
                    .setImportance(NotificationManagerCompat.IMPORTANCE_HIGH)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(carTapIntent)     // tap → DpfScreen sul display
                    .setLargeIcon(getIconBitmap(context))
                    .build()
            )
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID_EVENT, notification)
        } catch (e: SecurityException) {
            Log.e(TAG, "POST_NOTIFICATIONS permission not granted: ${e.message}")
        }
    }

    /**
     * Posts a silent connection notification on CHANNEL_CONNECTION.
     * No sound, no vibration — just a heads-up popup.
     *
     * On the car screen: tap → opens DpfScreen so the driver can check the status.
     */
    private fun postConnectionNotification(
        context: Context,
        title: String,
        text: String,
        color: Int
    ) {
        val phoneTapIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val carTapIntent = PendingIntent.getService(
            context, NOTIF_ID_CONNECTION,
            Intent(context, DpfCarAppService::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_CONNECTION)
            .setSmallIcon(R.drawable.ic_car_header)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setColor(color)
            .setAutoCancel(true)
            .setTimeoutAfter(4_000L)   // dismiss after 4 s
            .setSilent(true)
            .setContentIntent(phoneTapIntent)
            .extend(
                CarAppExtender.Builder()
                    .setImportance(NotificationManagerCompat.IMPORTANCE_DEFAULT)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(carTapIntent)     // tap → DpfScreen sul display
                    .setLargeIcon(getIconBitmap(context))
                    .build()
            )
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID_CONNECTION, notification)
        } catch (e: SecurityException) {
            Log.e(TAG, "POST_NOTIFICATIONS permission not granted: ${e.message}")
        }
    }
}
