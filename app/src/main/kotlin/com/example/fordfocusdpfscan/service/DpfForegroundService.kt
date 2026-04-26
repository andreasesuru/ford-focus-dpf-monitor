package com.example.fordfocusdpfscan.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.fordfocusdpfscan.ble.BleManager
import com.example.fordfocusdpfscan.ble.BleManagerHolder
import com.example.fordfocusdpfscan.car.NotificationHelper
import com.example.fordfocusdpfscan.data.ConnectionState
import com.example.fordfocusdpfscan.data.DpfData
import com.example.fordfocusdpfscan.data.DpfRepository
import com.example.fordfocusdpfscan.data.RegenHistoryRepository
import com.example.fordfocusdpfscan.data.RegenStatus
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════════════════════════════
// DpfForegroundService.kt — Foreground Service that owns the BLE connection.
//
// Responsibilities:
//   1. Keep the BLE GATT connection alive when the phone screen is off or the
//      user switches apps (foreground services are not killed by the OS).
//   2. Show the persistent "monitoring" notification (Level 1 in our stack).
//   3. React to DpfRepository state changes to fire event notifications
//      (Level 2 heads-up) via NotificationHelper.
//   4. Expose the BleManager instance so MainActivity can trigger reconnects.
//
// Lifecycle:
//   Started by MainActivity on "Connect" → keeps running until "Disconnect"
//   or the user explicitly stops the app.
// ═══════════════════════════════════════════════════════════════════════════════

class DpfForegroundService : LifecycleService() {

    companion object {
        private const val TAG = "FOCUS_Service"

        // Intent actions used to control the service from MainActivity
        const val ACTION_CONNECT         = "com.example.fordfocusdpfscan.CONNECT"
        const val ACTION_DISCONNECT      = "com.example.fordfocusdpfscan.DISCONNECT"
        const val ACTION_RECONNECT       = "com.example.fordfocusdpfscan.RECONNECT"
        /** Connect to a specific device by MAC address (from the device picker). */
        const val ACTION_CONNECT_ADDRESS = "com.example.fordfocusdpfscan.CONNECT_ADDRESS"
        const val EXTRA_DEVICE_ADDRESS   = "extra_device_address"
    }

    /** The BLE manager instance — created once per service lifetime. */
    val bleManager: BleManager by lazy { BleManager(applicationContext) }

    /** Regen history repository — owns the Room DB and the HTML export. */
    private val historyRepo: RegenHistoryRepository by lazy {
        RegenHistoryRepository(applicationContext)
    }

    /** Throttle DATA_POINT events: only record one sample every 30 seconds. */
    private var lastDataPointTime = 0L
    private val DATA_POINT_INTERVAL_MS = 30_000L

    // ═════════════════════════════════════════════════════════════════════════
    // Service lifecycle
    // ═════════════════════════════════════════════════════════════════════════

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        // Expose bleManager via the holder so EcuScanActivity can reach it
        // without needing to bind to this service.
        BleManagerHolder.instance = bleManager

        // Wire up the repository → notification callback BEFORE starting BLE
        DpfRepository.onRegenStatusChanged = { old, new ->
            onRegenStatusChanged(old, new)
        }

        // Wire up regen session events → history repository
        DpfRepository.onRegenSessionEvent = { event, data ->
            onRegenSessionEvent(event, data)
        }

        // Show the persistent foreground notification immediately
        // (required before any other work on Android 8+)
        startForeground(
            NotificationHelper.NOTIF_ID_PERSISTENT,
            NotificationHelper.buildPersistentNotification(this)
        )

        // Observe DpfData changes to keep the persistent notification updated
        observeDpfData()

        // Observe BLE connection state to update the persistent notification
        observeConnectionState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_CONNECT    -> bleManager.connect()
            ACTION_CONNECT_ADDRESS -> {
                val address = intent.getStringExtra(EXTRA_DEVICE_ADDRESS)
                if (address != null) bleManager.connectByAddress(address)
                else bleManager.connect()
            }
            ACTION_DISCONNECT -> {
                bleManager.disconnect()
                stopSelf()
            }
            ACTION_RECONNECT  -> {
                bleManager.disconnect()
                bleManager.connect()
            }
        }
        return START_STICKY  // Restart the service if killed by the OS
    }

    override fun onDestroy() {
        super.onDestroy()
        bleManager.disconnect()
        BleManagerHolder.instance = null
        DpfRepository.onRegenStatusChanged = null
        DpfRepository.onRegenSessionEvent  = null
        Log.d(TAG, "Service destroyed")
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null  // Not a bound service
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Observers
    // ═════════════════════════════════════════════════════════════════════════

    /** Refreshes the persistent notification content every time DpfData changes. */
    private fun observeDpfData() {
        lifecycleScope.launch {
            DpfRepository.dpfData.collectLatest { data ->
                // Update the persistent "monitoring" notification with latest values
                val updatedNotif = NotificationHelper.buildPersistentNotification(
                    context    = this@DpfForegroundService,
                    dpfData    = data
                )
                NotificationHelper.updatePersistentNotification(
                    this@DpfForegroundService,
                    updatedNotif
                )
            }
        }
    }

    /** Fires silent BLE connection/disconnection notifications. */
    private fun observeConnectionState() {
        lifecycleScope.launch {
            var previousState: ConnectionState? = null
            bleManager.connectionState.collectLatest { state ->
                when (state) {
                    ConnectionState.DISCONNECTED -> {
                        if (previousState == ConnectionState.CONNECTED) {
                            NotificationHelper.notifyBleLost(this@DpfForegroundService)
                        }
                    }
                    ConnectionState.CONNECTED -> {
                        val deviceName = bleManager.connectedDeviceName ?: "OBD Dongle"
                        NotificationHelper.notifyBleConnected(this@DpfForegroundService, deviceName)
                    }
                    else -> { /* SCANNING / CONNECTING — no notification */ }
                }
                previousState = state
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Regen status change handler
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Called by DpfRepository on every status transition.
     * Fires the appropriate heads-up notification + MP3 sound for regen events.
     */
    private fun onRegenStatusChanged(old: RegenStatus, new: RegenStatus) {
        Log.d(TAG, "Regen status: $old → $new")
        when (new) {
            RegenStatus.WARNING   -> NotificationHelper.notifyWarning(this)
            RegenStatus.ACTIVE    -> NotificationHelper.notifyActive(this)
            RegenStatus.COMPLETED -> {
                NotificationHelper.notifyCompleted(this)
                lifecycleScope.launch {
                    kotlinx.coroutines.delay(8_000)
                    DpfRepository.acknowledgeCompleted()
                }
            }
            RegenStatus.INACTIVE  -> { /* no notification needed */ }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Regen history recording
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Routes regen session events from DpfRepository to RegenHistoryRepository.
     * DATA_POINT events are throttled to one sample every 30 seconds.
     */
    private fun onRegenSessionEvent(event: String, data: DpfData) {
        lifecycleScope.launch {
            when (event) {
                "STARTED" -> historyRepo.onRegenStarted(data)

                "DATA_POINT" -> {
                    val now = System.currentTimeMillis()
                    if (now - lastDataPointTime >= DATA_POINT_INTERVAL_MS) {
                        lastDataPointTime = now
                        historyRepo.onRegenDataPoint(data)
                    }
                }

                "COMPLETED"   -> historyRepo.onRegenEnded(data, "COMPLETED")
                "INTERRUPTED" -> historyRepo.onRegenEnded(data, "INTERRUPTED")
            }
        }
    }
}
