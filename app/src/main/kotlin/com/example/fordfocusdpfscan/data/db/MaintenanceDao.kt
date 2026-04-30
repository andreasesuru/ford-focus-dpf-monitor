package com.example.fordfocusdpfscan.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════════════════════════════════
// MaintenanceDao.kt — Room DAO for maintenance_reminders table.
// ═══════════════════════════════════════════════════════════════════════════════

@Dao
interface MaintenanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: MaintenanceReminder): Long

    @Update
    suspend fun update(reminder: MaintenanceReminder)

    @Delete
    suspend fun delete(reminder: MaintenanceReminder)

    /** Live list for MaintenanceActivity: auto-managed entry pinned to top. */
    @Query("SELECT * FROM maintenance_reminders ORDER BY isAutoManaged DESC, title ASC")
    fun getAllFlow(): Flow<List<MaintenanceReminder>>

    /** One-shot list used by the service for notification checks. */
    @Query("SELECT * FROM maintenance_reminders")
    suspend fun getAll(): List<MaintenanceReminder>

    /** Returns the single auto-managed entry (tagliando olio), or null. */
    @Query("SELECT * FROM maintenance_reminders WHERE isAutoManaged = 1 LIMIT 1")
    suspend fun getAutoManaged(): MaintenanceReminder?

    /**
     * Marks maintenance as done at [km] and resets all notification flags.
     * Called when the user taps "Fatto oggi" or when ECU data causes an update.
     */
    @Query("""
        UPDATE maintenance_reminders
        SET lastDoneKm = :km,
            notif1000Sent = 0,
            notif500Sent  = 0,
            notifOverdueSent = 0
        WHERE id = :id
    """)
    suspend fun markDone(id: Long, km: Long)

    @Query("UPDATE maintenance_reminders SET notif1000Sent    = 1 WHERE id = :id")
    suspend fun setNotif1000Sent(id: Long)

    @Query("UPDATE maintenance_reminders SET notif500Sent     = 1 WHERE id = :id")
    suspend fun setNotif500Sent(id: Long)

    @Query("UPDATE maintenance_reminders SET notifOverdueSent = 1 WHERE id = :id")
    suspend fun setNotifOverdueSent(id: Long)
}
