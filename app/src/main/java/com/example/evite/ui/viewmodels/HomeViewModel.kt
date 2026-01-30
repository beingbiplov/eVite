package com.example.evite.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evite.data.local.DatabaseProvider
import com.example.evite.data.local.entities.Event
import com.example.evite.data.repository.EventRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application, private val currentUserId: Int) : AndroidViewModel(application) {

    private val db = DatabaseProvider.getDatabase(application)
    private val eventDao = db.eventDao()
    private val repository = EventRepository(eventDao)

    // Using stateIn to convert Flow to StateFlow automatically
    // Now filters events by current user for data isolation
    val events: StateFlow<List<Event>> = repository.getEventsByUser(currentUserId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun refreshEvents() {
        // No longer needed as Flow handles updates automatically, 
        // but kept for compatibility if called elsewhere.
    }
}
