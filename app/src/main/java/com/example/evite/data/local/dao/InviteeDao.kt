package com.example.evite.data.local.dao

import androidx.room.*
import com.example.evite.data.local.entities.Invitee

@Dao
interface InviteeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitee(invitee: Invitee): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitees(invitees: List<Invitee>)

    @Query("SELECT * FROM invitees WHERE eventId = :eventId")
    suspend fun getInviteesForEvent(eventId: Int): List<Invitee>

    @Query("DELETE FROM invitees WHERE eventId = :eventId")
    suspend fun deleteInviteesForEvent(eventId: Int)
}
