package com.example.fordfocusdpfscan.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

// ═══════════════════════════════════════════════════════════════════════════════
// MaintenanceReminder.kt — Room entity for user-defined maintenance reminders.
//
// Each reminder tracks:
//   • How often the maintenance is due (intervalKm)
//   • When it was last done (lastDoneKm)
//   • Notification state — avoids re-firing the same alert every km
//
// isAutoManaged = true → the app updates lastDoneKm automatically from ECU data
//   (used for "Tagliando olio" via odometerKm - kmSinceOilChange).
//   These entries cannot be edited/deleted by the user.
// ═══════════════════════════════════════════════════════════════════════════════

@Entity(tableName = "maintenance_reminders")
data class MaintenanceReminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    /** Display name shown on the card (e.g. "Tagliando olio"). */
    val title: String,

    /** Maintenance interval in km (e.g. 15000 for an oil change). */
    val intervalKm: Long,

    /** Odometer reading when maintenance was last performed. */
    val lastDoneKm: Long,

    /** If true, the app manages lastDoneKm from ECU data automatically. */
    val isAutoManaged: Boolean = false,

    // ── Notification state — reset to false whenever lastDoneKm changes ──────
    /** True once the "in scadenza tra 1000 km" notification has been sent. */
    val notif1000Sent: Boolean = false,
    /** True once the "in scadenza tra 500 km" notification has been sent. */
    val notif500Sent: Boolean = false,
    /** True once the "scaduto" (overdue) notification has been sent. */
    val notifOverdueSent: Boolean = false
) {
    // ── Computed helpers — require current odometer reading ───────────────────

    /** Odometer km at which this maintenance is next due. */
    fun dueAtKm(): Long = lastDoneKm + intervalKm

    /** Km remaining before the next maintenance. Negative = overdue. */
    fun kmRemaining(currentOdometer: Long): Long = dueAtKm() - currentOdometer

    /** Fraction of the interval consumed (0.0 = just done, 1.0+ = overdue). */
    fun fractionUsed(currentOdometer: Long): Float =
        ((currentOdometer - lastDoneKm).toFloat() / intervalKm).coerceAtLeast(0f)

    /** Progress bar value (0–100), capped at 100 even if overdue. */
    fun progressPercent(currentOdometer: Long): Int =
        (fractionUsed(currentOdometer) * 100f).toInt().coerceIn(0, 100)

    enum class Status { OK, WARNING, DANGER, OVERDUE }

    fun status(currentOdometer: Long): Status = when {
        kmRemaining(currentOdometer) < 0    -> Status.OVERDUE
        kmRemaining(currentOdometer) < 500  -> Status.DANGER
        kmRemaining(currentOdometer) < 1000 -> Status.WARNING
        else                                -> Status.OK
    }
}
