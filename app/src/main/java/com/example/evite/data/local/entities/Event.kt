package com.example.evite.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,        // Foreign key to User.id for data isolation
    val title: String,
    val description: String,
    val dateTime: String,
    val location: String,
    val theme: String,
    val imageUri: String? = null
)
