package com.example.evite.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EventDetailsViewModelFactory(
    private val application: Application,
    private val currentUserId: Int
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventDetailsViewModel::class.java)) {
            return EventDetailsViewModel(application, currentUserId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
