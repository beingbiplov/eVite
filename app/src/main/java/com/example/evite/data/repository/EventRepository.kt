package com.example.evite.data.repository

import com.example.evite.data.local.dao.EventDao
import com.example.evite.data.local.entities.Event

/**
 * EventRepository
 * 
 * This class acts as a clean API for the rest of the app to access Event data.
 * It hides the details of the database (DAO) from the UI.
 */
class EventRepository(private val eventDao: EventDao) {

    // -------------------------------------------------------
    // Create
    // -------------------------------------------------------
    suspend fun createEvent(event: Event) {
        eventDao.insertEvent(event)
    }

    // -------------------------------------------------------
    // Read (All)
    // -------------------------------------------------------
    suspend fun getEvents(): List<Event> {
        return eventDao.getAllEvents()
    }

    // -------------------------------------------------------
    // Read (One)
    // -------------------------------------------------------
    suspend fun getEvent(id: Int): Event? {
        return eventDao.getEventById(id)
    }
}
