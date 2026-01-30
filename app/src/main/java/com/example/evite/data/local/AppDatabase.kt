package com.example.evite.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.evite.data.local.dao.*
import com.example.evite.data.local.entities.*

@Database(
    entities = [User::class, Event::class, Invitee::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun inviteeDao(): InviteeDao

    companion object {
        // Migration from version 3 to 4: Add userId column to events table
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add userId column to events table
                database.execSQL("ALTER TABLE events ADD COLUMN userId INTEGER NOT NULL DEFAULT 1")
            }
        }
    }
}
