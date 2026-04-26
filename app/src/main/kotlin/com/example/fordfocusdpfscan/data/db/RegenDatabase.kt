package com.example.fordfocusdpfscan.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ═══════════════════════════════════════════════════════════════════════════════
// RegenDatabase.kt — Room database definition.
//
// Single instance (singleton) shared across the app via getInstance().
// Stores regen_sessions and regen_data_points tables.
// exportSchema = false → no schema JSON files in the build output.
// ═══════════════════════════════════════════════════════════════════════════════

@Database(
    entities  = [RegenSession::class, RegenDataPoint::class],
    version   = 1,
    exportSchema = false
)
abstract class RegenDatabase : RoomDatabase() {

    abstract fun regenDao(): RegenDao

    companion object {

        @Volatile
        private var INSTANCE: RegenDatabase? = null

        fun getInstance(context: Context): RegenDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    RegenDatabase::class.java,
                    "regen_history.db"
                )
                    .fallbackToDestructiveMigration()   // dev convenience — replace with Migration in prod
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
