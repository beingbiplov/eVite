package com.example.evite.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invitees")
data class Invitee(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: Int,       // foreign key to Event.id
    val name: String?,
    val email: String
)
