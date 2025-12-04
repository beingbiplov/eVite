package com.example.evite.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.evite.data.local.dao.*
import com.example.evite.data.local.entities.*

@Database(
    entities = [User::class, Event::class, Invitee::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun inviteeDao(): InviteeDao
}
