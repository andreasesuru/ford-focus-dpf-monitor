package com.example.fordfocusdpfscan.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════════════════════════════════
// RegenDao.kt — Room data access object for regen history.
// ═══════════════════════════════════════════════════════════════════════════════

@Dao
interface RegenDao {

    // ── Session CRUD ──────────────────────────────────────────────────────────

    @Insert
    suspend fun insertSession(session: RegenSession): Long

    @Update
    suspend fun updateSession(session: RegenSession)

    /** Returns all sessions newest-first, as a live Flow for the UI. */
    @Query("SELECT * FROM regen_sessions ORDER BY startTimestamp DESC")
    fun getAllSessions(): Flow<List<RegenSession>>

    /** Returns all sessions for export (no live updates needed). */
    @Query("SELECT * FROM regen_sessions ORDER BY startTimestamp DESC")
    suspend fun getAllSessionsOnce(): List<RegenSession>

    /** Finds the most recent IN_PROGRESS session (should be at most one). */
    @Query("SELECT * FROM regen_sessions WHERE result = 'IN_PROGRESS' ORDER BY startTimestamp DESC LIMIT 1")
    suspend fun getActiveSession(): RegenSession?

    @Query("SELECT COUNT(*) FROM regen_sessions")
    suspend fun getTotalCount(): Int

    @Query("DELETE FROM regen_sessions")
    suspend fun clearAll()

    // ── Data points ───────────────────────────────────────────────────────────

    @Insert
    suspend fun insertDataPoint(point: RegenDataPoint)

    @Query("SELECT * FROM regen_data_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun getDataPointsForSession(sessionId: Long): List<RegenDataPoint>
}
