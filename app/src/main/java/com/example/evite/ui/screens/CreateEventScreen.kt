package com.example.evite.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evite.ui.viewmodels.EventViewModel

@Composable
fun CreateEventScreen(
    onEventCreated: () -> Unit,
    viewModel: EventViewModel = viewModel()
) {
    // Collect form data from ViewModel StateFlows
    val title by viewModel.eventTitle.collectAsState()
    val description by viewModel.eventDescription.collectAsState()
    val date by viewModel.eventDate.collectAsState()
    val location by viewModel.eventLocation.collectAsState()
    val theme by viewModel.eventTheme.collectAsState()
    
    // Watch save status to know if operation succeeded
    val creationState by viewModel.creationState.collectAsState()

    // Navigate back when save succeeds
    LaunchedEffect(creationState) {
        if (creationState == "success") {
            viewModel.resetState() 
            onEventCreated()
        }
    }

    ScreenContainer(title = "Create Event") {
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Title input field
            OutlinedTextField(
                value = title,
                onValueChange = { newText ->
                    viewModel.eventTitle.value = newText
                },
                label = { Text("Event Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // Description input field
            OutlinedTextField(
                value = description,
                onValueChange = { newText ->
                    viewModel.eventDescription.value = newText
                },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Date input field
            OutlinedTextField(
                value = date,
                onValueChange = { newText ->
                    viewModel.eventDate.value = newText
                },
                label = { Text("Date (e.g., 2025-12-25)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Location input field
            OutlinedTextField(
                value = location,
                onValueChange = { newText ->
                    viewModel.eventLocation.value = newText
                },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            // Theme selection with radio buttons
            Text(
                text = "Select Theme:", 
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val availableThemes = listOf("Party", "Business", "Casual")
                
                availableThemes.forEach { themeOption ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (theme == themeOption),
                            onClick = { 
                                viewModel.eventTheme.value = themeOption 
                            }
                        )
                        Text(text = themeOption)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save event button
            Button(
                onClick = { 
                    viewModel.saveEvent() 
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Event")
            }

            // Show error message if save failed
            if (creationState != null && creationState != "success") {
                Text(
                    text = creationState ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}