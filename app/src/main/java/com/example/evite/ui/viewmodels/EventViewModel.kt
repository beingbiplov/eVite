package com.example.evite.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evite.data.local.DatabaseProvider
import com.example.evite.data.local.entities.Event
import com.example.evite.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventViewModel(application: Application, private val currentUserId: Int) : AndroidViewModel(application) {

    // Initialize database and repository for data persistence
    // Initialize database and repository for data persistence
    private val db = DatabaseProvider.getDatabase(application)
    private val eventDao = db.eventDao()
    private val inviteeDao = db.inviteeDao()
    private val repository = EventRepository(eventDao, inviteeDao)

    // Hold form input values that user enters
    val eventTitle = MutableStateFlow("")
    val eventDescription = MutableStateFlow("")
    val eventDate = MutableStateFlow("")
    val eventLocation = MutableStateFlow("")
    val eventTheme = MutableStateFlow("Party")
    val eventImageUri = MutableStateFlow<String?>(null)
    
    // Track if we are editing an existing event
    private val currentEventId = MutableStateFlow<Int?>(null)

    // Track save operation status (success or error message)
    private val _creationState = MutableStateFlow<String?>(null)
    val creationState = _creationState.asStateFlow()

    /**
     * Loads an existing event into the form fields.
     * Only loads events that belong to the current user.
     */
    fun loadEventForEdit(eventId: Int) {
        viewModelScope.launch {
            val event = repository.getEventByIdAndUser(eventId, currentUserId)
            if (event != null) {
                currentEventId.value = event.id
                eventTitle.value = event.title
                eventDescription.value = event.description
                eventDate.value = event.dateTime
                eventLocation.value = event.location
                eventTheme.value = event.theme
                eventImageUri.value = event.imageUri
                
                // Also load invitees
                val invitees = repository.getEventInvitees(eventId)
                _temporaryInvitees.value = invitees
            }
        }
    }

    fun saveEvent() {
        viewModelScope.launch {
            // Extract current values from form fields
            val titleInput = eventTitle.value
            val dateInput = eventDate.value
            
            // Validate required fields are not empty
            if (titleInput.isBlank() || dateInput.isBlank()) {
                _creationState.value = "Title and Date are required!"
                return@launch
            }

            _creationState.value = "loading"
            
            // Get organizer details for the email
            val user = db.userDao().getUserById(currentUserId)
            val organizerName = user?.fullName ?: "Someone"

            val eventId = currentEventId.value
            if (eventId != null) {
                // UPDATE existing event
                val updatedEvent = Event(
                    id = eventId,
                    userId = currentUserId,
                    title = eventTitle.value,
                    description = eventDescription.value,
                    dateTime = eventDate.value,
                    location = eventLocation.value,
                    theme = eventTheme.value,
                    imageUri = eventImageUri.value
                )
                repository.updateEventWithInviteesAndValidation(updatedEvent, _temporaryInvitees.value, currentUserId)
            } else {
                // CREATE new event
                val newEvent = Event(
                    userId = currentUserId,
                    title = eventTitle.value,
                    description = eventDescription.value,
                    dateTime = eventDate.value,
                    location = eventLocation.value,
                    theme = eventTheme.value,
                    imageUri = eventImageUri.value
                )
                repository.createEventWithInvitees(newEvent, _temporaryInvitees.value)
            }

            // Send Emails to all invitees
            val currentInvitees = _temporaryInvitees.value
            if (currentInvitees.isNotEmpty()) {
                currentInvitees.forEach { invitee ->
                    com.example.evite.data.network.EmailService.sendInviteEmail(
                        toEmail = invitee.email,
                        eventTitle = eventTitle.value,
                        eventDate = eventDate.value,
                        eventLocation = eventLocation.value,
                        organizerName = organizerName
                    )
                }
            }

            // Notify UI that save completed successfully
            _creationState.value = "success"
        }
    }

    // List of invitees added temporarily before saving
    private val _temporaryInvitees = MutableStateFlow<List<com.example.evite.data.local.entities.Invitee>>(emptyList())
    val temporaryInvitees = _temporaryInvitees.asStateFlow()

    fun addTemporaryInvitee(name: String?, email: String) {
        // Use update to safely modify key state
        val newInvitee = com.example.evite.data.local.entities.Invitee(eventId = 0, name = name, email = email)
        _temporaryInvitees.value = _temporaryInvitees.value + newInvitee
    }

    fun removeTemporaryInvitee(invitee: com.example.evite.data.local.entities.Invitee) {
        _temporaryInvitees.value = _temporaryInvitees.value - invitee
    }

    fun resetState() {
        // Clear all form fields back to defaults
        currentEventId.value = null
        eventTitle.value = ""
        eventDescription.value = ""
        eventDate.value = ""
        eventLocation.value = ""
        eventTheme.value = "Party"
        eventImageUri.value = null
        _temporaryInvitees.value = emptyList() // Clear invitees
        _creationState.value = null
    }
}
