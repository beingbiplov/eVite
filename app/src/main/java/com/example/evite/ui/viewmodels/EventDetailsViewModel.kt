package com.example.evite.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evite.data.local.DatabaseProvider
import com.example.evite.data.local.entities.Event
import com.example.evite.data.local.entities.Invitee
import com.example.evite.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class EventDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EventRepository

    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event

    private val _invitees = MutableStateFlow<List<Invitee>>(emptyList())
    val invitees: StateFlow<List<Invitee>> = _invitees

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        val database = DatabaseProvider.getDatabase(application)
        repository = EventRepository(database.eventDao(), database.inviteeDao())
    }

    fun loadEvent(eventId: Int) {
        viewModelScope.launch {
            _event.value = repository.getEvent(eventId)
            _invitees.value = repository.getEventInvitees(eventId)
        }
    }

    fun deleteEvent(event: Event, onComplete: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000) // 1 second delay to show the "deleting" state
            repository.deleteEvent(event)
            _isLoading.value = false
            onComplete()
        }
    }
}
