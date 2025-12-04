package com.example.evite.data.local.dao

import androidx.room.*
import com.example.evite.data.local.entities.Invitee

@Dao
interface InviteeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitee(invitee: Invitee): Long

    @Query("SELECT * FROM invitees WHERE eventId = :eventId")
    suspend fun getInviteesForEvent(eventId: Int): List<Invitee>
}
