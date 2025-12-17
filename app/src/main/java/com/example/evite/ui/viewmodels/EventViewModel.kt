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

class EventViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize database and repository for data persistence
    private val eventDao = DatabaseProvider.getDatabase(application).eventDao()
    private val repository = EventRepository(eventDao)

    // Hold form input values that user enters
    val eventTitle = MutableStateFlow("")
    val eventDescription = MutableStateFlow("")
    val eventDate = MutableStateFlow("")
    val eventLocation = MutableStateFlow("")
    val eventTheme = MutableStateFlow("Party")

    // Track save operation status (success or error message)
    private val _creationState = MutableStateFlow<String?>(null)
    val creationState = _creationState.asStateFlow()

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
            kotlinx.coroutines.delay(1000) // Simulate processing

            // Package all form data into Event object
            val newEvent = Event(
                title = eventTitle.value,
                description = eventDescription.value,
                dateTime = eventDate.value,
                location = eventLocation.value,
                theme = eventTheme.value
            )

            // Save event to database through repository
            repository.createEvent(newEvent)

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
        eventTitle.value = ""
        eventDescription.value = ""
        eventDate.value = ""
        eventLocation.value = ""
        eventTheme.value = "Party"
        _temporaryInvitees.value = emptyList() // Clear invitees
        _creationState.value = null
    }
}
