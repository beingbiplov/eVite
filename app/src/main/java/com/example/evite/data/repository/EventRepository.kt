package com.example.evite.data.repository

import com.example.evite.data.local.dao.EventDao
import com.example.evite.data.local.entities.Event

import kotlinx.coroutines.flow.Flow

/**
 * EventRepository
 * 
 * This class acts as a clean API for the rest of the app to access Event data.
 * It hides the details of the database (DAO) from the UI.
 */
class EventRepository(
    private val eventDao: EventDao,
    private val inviteeDao: com.example.evite.data.local.dao.InviteeDao? = null 
) {

    // -------------------------------------------------------
    // Create
    // -------------------------------------------------------
    suspend fun createEvent(event: Event): Long {
        return eventDao.insertEvent(event)
    }

    /**
     * Saves event and its invitees in one flow.
     * 1. Inserts event to get the new ID.
     * 2. Updates invitees with that ID.
     * 3. Inserts all invitees.
     */
    suspend fun createEventWithInvitees(event: Event, invitees: List<com.example.evite.data.local.entities.Invitee>) {
        val eventId = eventDao.insertEvent(event)
        
        if (inviteeDao != null && invitees.isNotEmpty()) {
            val updatedInvitees = invitees.map { it.copy(eventId = eventId.toInt()) }
            inviteeDao.insertInvitees(updatedInvitees)
        }
    }

    // -------------------------------------------------------
    // Read (All) - REACTIVE
    // -------------------------------------------------------
    fun getEvents(): Flow<List<Event>> {
        return eventDao.getAllEvents()
    }

    // -------------------------------------------------------
    // Read (User Events) - REACTIVE
    // -------------------------------------------------------
    fun getEventsByUser(userId: Int): Flow<List<Event>> {
        return eventDao.getEventsByUser(userId)
    }

    // -------------------------------------------------------
    // Read (One)
    // -------------------------------------------------------
    suspend fun getEvent(id: Int): Event? {
        return eventDao.getEventById(id)
    }

    // -------------------------------------------------------
    // Read (One with User Validation)
    // -------------------------------------------------------
    suspend fun getEventByIdAndUser(id: Int, userId: Int): Event? {
        return eventDao.getEventByIdAndUser(id, userId)
    }

    // -------------------------------------------------------
    // Get Invitees for Event
    // -------------------------------------------------------
    suspend fun getEventInvitees(eventId: Int): List<com.example.evite.data.local.entities.Invitee> {
        return inviteeDao?.getInviteesForEvent(eventId) ?: emptyList()
    }

    // -------------------------------------------------------
    // Update
    // -------------------------------------------------------
    suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(event)
    }

    // -------------------------------------------------------
    // Update with User Validation
    // -------------------------------------------------------
    suspend fun updateEventWithValidation(event: Event, userId: Int) {
        // Verify ownership before update
        if (event.userId == userId) {
            eventDao.updateEvent(event)
        }
    }

    /**
     * Updates event and its invitees.
     * 1. Updates event details.
     * 2. Deletes existing invitees for that event.
     * 3. Inserts the new list of invitees.
     */
    suspend fun updateEventWithInvitees(event: Event, invitees: List<com.example.evite.data.local.entities.Invitee>) {
        eventDao.updateEvent(event)
        
        if (inviteeDao != null) {
            inviteeDao.deleteInviteesForEvent(event.id)
            if (invitees.isNotEmpty()) {
                val updatedInvitees = invitees.map { it.copy(eventId = event.id) }
                inviteeDao.insertInvitees(updatedInvitees)
            }
        }
    }

    /**
     * Updates event and its invitees with user validation.
     * Ensures only the event owner can modify the event.
     */
    suspend fun updateEventWithInviteesAndValidation(event: Event, invitees: List<com.example.evite.data.local.entities.Invitee>, userId: Int) {
        // Verify ownership before update
        if (event.userId == userId) {
            eventDao.updateEvent(event)
            
            if (inviteeDao != null) {
                inviteeDao.deleteInviteesForEvent(event.id)
                if (invitees.isNotEmpty()) {
                    val updatedInvitees = invitees.map { it.copy(eventId = event.id) }
                    inviteeDao.insertInvitees(updatedInvitees)
                }
            }
        }
    }

    // -------------------------------------------------------
    // Delete
    // -------------------------------------------------------
    suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event)
    }

    // -------------------------------------------------------
    // Delete with User Validation
    // -------------------------------------------------------
    suspend fun deleteEventByIdAndUser(id: Int, userId: Int) {
        eventDao.deleteEventByIdAndUser(id, userId)
    }
}
