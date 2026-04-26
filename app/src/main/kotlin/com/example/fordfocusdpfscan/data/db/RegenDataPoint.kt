package com.example.fordfocusdpfscan.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ═══════════════════════════════════════════════════════════════════════════════
// RegenDataPoint.kt — Room entity for periodic OBD samples during a regen cycle.
//
// One row = one sample taken every ~30 seconds while regen is ACTIVE.
// These samples feed the EGT sparkline chart in HistoryActivity and are included
// in the exported mechanic report as a time-series table.
//
// ForeignKey CASCADE ensures data points are deleted with their parent session.
// ═══════════════════════════════════════════════════════════════════════════════

@Entity(
    tableName = "regen_data_points",
    foreignKeys = [ForeignKey(
        entity      = RegenSession::class,
        parentColumns = ["id"],
        childColumns  = ["sessionId"],
        onDelete    = ForeignKey.CASCADE
    )],
    indices = [Index("sessionId")]
)
data class RegenDataPoint(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** FK → RegenSession.id */
    val sessionId: Long,

    val timestamp:      Long,
    val elapsedSeconds: Int,

    val sootPct:   Float,
    val loadPct:   Float,
    val deltaPKpa: Float,
    val egtC:      Float,
    val coolantC:  Float
)
