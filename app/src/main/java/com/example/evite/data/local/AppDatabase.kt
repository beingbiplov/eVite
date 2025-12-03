package com.example.evite.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.evite.data.local.dao.EventDao
import com.example.evite.data.local.entities.Event

@Database(
    entities = [Event::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
}
