package com.example.evite.data.local.dao

import androidx.room.*
import com.example.evite.data.local.entities.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event): Long

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("SELECT * FROM events ORDER BY dateTime ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE userId = :userId ORDER BY dateTime ASC")
    fun getEventsByUser(userId: Int): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Int): Event?

    @Query("SELECT * FROM events WHERE id = :id AND userId = :userId")
    suspend fun getEventByIdAndUser(id: Int, userId: Int): Event?

    @Query("DELETE FROM events WHERE id = :id AND userId = :userId")
    suspend fun deleteEventByIdAndUser(id: Int, userId: Int)
}
