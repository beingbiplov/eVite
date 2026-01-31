package com.example.evite.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evite.ui.viewmodels.EventViewModel
import com.example.evite.ui.viewmodels.EventViewModelFactory
import java.util.Calendar
import java.util.Locale
import android.net.Uri
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    eventId: Int? = null,
    currentUserId: Int,
    onEventCreated: () -> Unit,
    onBack: () -> Unit,
    onAddInviteeClick: () -> Unit,
    viewModel: EventViewModel = viewModel(factory = EventViewModelFactory(LocalContext.current.applicationContext as android.app.Application, currentUserId))
) {
    // Collect form data from ViewModel StateFlows
    val title by viewModel.eventTitle.collectAsState()
    val description by viewModel.eventDescription.collectAsState()
    val date by viewModel.eventDate.collectAsState()
    val location by viewModel.eventLocation.collectAsState()
    val theme by viewModel.eventTheme.collectAsState()
    val temporaryInvitees by viewModel.temporaryInvitees.collectAsState()
    
    // Watch save status
    val creationState by viewModel.creationState.collectAsState()

    // Loading State
    val isLoading = creationState == "loading"
    
    // Modal State
    var showAddInviteeDialog by remember { mutableStateOf(false) }
    
    // Initialize editing if eventId is provided
    LaunchedEffect(eventId) {
        if (eventId != null) {
            viewModel.loadEventForEdit(eventId)
        } else {
            viewModel.resetState()
        }
    }

    // Navigate back when save succeeds
    LaunchedEffect(creationState) {
        if (creationState == "success") {
            viewModel.resetState() 
            onEventCreated()
        }
    }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Date Time Picker Logic
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val timePickerDialog = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val selectedDate = String.format(
                        Locale.getDefault(),
                        "%04d-%02d-%02d at %02d:%02d", 
                        year, month + 1, dayOfMonth, hourOfDay, minute
                    )
                    viewModel.eventDate.value = selectedDate
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Screen Header
            Text(
                text = if (eventId != null) "Edit Your Event" else "Plan New Event",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Event Title
            Column {
                Text(
                        text = "Event Title", 
                        style = MaterialTheme.typography.titleLarge, 
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { viewModel.eventTitle.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("My Awesome Event", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }

                // Description
                Column {
                    Text(
                        "Description", 
                        style = MaterialTheme.typography.titleLarge, 
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { viewModel.eventDescription.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Tell us more about your event...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                        minLines = 3,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }

                // Date & Time
                Column {
                    Text(
                        "Date & Time", 
                        style = MaterialTheme.typography.titleLarge, 
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = date,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() }, 
                        enabled = false, 
                        trailingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date", tint = MaterialTheme.colorScheme.primary)
                        },
                        placeholder = { Text("Select Date & Time", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                // Location
                Column {
                    Text(
                        "Location", 
                        style = MaterialTheme.typography.titleLarge, 
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = location,
                        onValueChange = { viewModel.eventLocation.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        placeholder = { Text("Event Venue, City", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }

                // Theme Dropdown
                Column {
                    Text(
                        "Theme", 
                        style = MaterialTheme.typography.titleLarge, 
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    var expanded by remember { mutableStateOf(false) }
                    val themes = listOf("Party", "Business", "Casual", "Formal", "Wedding", "Other")
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = theme,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            placeholder = { Text("Select a theme", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            themes.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item, style = MaterialTheme.typography.bodyLarge) },
                                    onClick = {
                                        viewModel.eventTheme.value = item
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Cover Photo Placeholder
                Column {
                    Text(
                        "Cover Photo", 
                        style = MaterialTheme.typography.titleLarge, 
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    val imageLauncher = rememberLauncherForActivityResult(
                        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
                    ) { uri ->
                        if (uri != null) {
                             viewModel.eventImageUri.value = uri.toString()
                        }
                    }
                    
                    val selectedImageUriString by viewModel.eventImageUri.collectAsState()
                    val selectedImageBitmap = rememberBitmapFromUri(selectedImageUriString)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .dashedBorder(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { 
                                imageLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                ) 
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageBitmap != null) {
                            androidx.compose.foundation.Image(
                                bitmap = selectedImageBitmap,
                                contentDescription = "Selected Cover Photo",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Overlay to make "Change Photo" text visible if needed
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.1f)))
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowUpward, 
                                            contentDescription = "Upload",
                                            modifier = Modifier.size(28.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Tap to upload a cover photo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Invitees Section
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Invitees", 
                            style = MaterialTheme.typography.titleLarge, 
                            color = MaterialTheme.colorScheme.primary
                        )
                        TextButton(
                            onClick = { showAddInviteeDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add New", fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (temporaryInvitees.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Person, 
                                    contentDescription = null, 
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.outlineVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Your invitees will appear here",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            temporaryInvitees.forEach { invitee ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = androidx.compose.foundation.shape.CircleShape,
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                            modifier = Modifier.size(44.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                     text = (invitee.name ?: invitee.email).take(1).uppercase(),
                                                     style = MaterialTheme.typography.titleMedium,
                                                     color = MaterialTheme.colorScheme.primary
                                                 )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = invitee.name ?: "Unknown",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = invitee.email,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.removeTemporaryInvitee(invitee) },
                                            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remove",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                // Save Button (Now part of the scrollable column)
                Column {
                     // Show error message if save failed
                    if (creationState != null && creationState != "success" && creationState != "loading") {
                        Text(
                            text = creationState ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 8.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                            .clickable(enabled = !isLoading, onClick = { viewModel.saveEvent() }),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (eventId != null) "Update Event" else "Create Event", 
                                style = MaterialTheme.typography.titleMedium, 
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

    // ADD INVITEE MODAL (Custom Dialog)
    if (showAddInviteeDialog) {
        var newEmail by remember { mutableStateOf("") }
        var newName by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf<String?>(null) }

        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showAddInviteeDialog = false }
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        "Add Invitee", 
                        style = MaterialTheme.typography.headlineSmall, 
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // Email
                    Column {
                        Text("Email Address *", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newEmail,
                            onValueChange = { 
                                newEmail = it
                                if (emailError != null) emailError = null 
                            },
                            placeholder = { Text("john@example.com") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            isError = emailError != null,
                            supportingText = {
                                if (emailError != null) {
                                    Text(emailError!!, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }
                    // Name
                    Column {
                        Text("Name (Optional)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            placeholder = { Text("John Doe") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { showAddInviteeDialog = false },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                        ) {
                            Text("Cancel", fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                val emailPattern = android.util.Patterns.EMAIL_ADDRESS
                                if (newEmail.isBlank()) {
                                    emailError = "Email is required"
                                } else if (!emailPattern.matcher(newEmail).matches()) {
                                    emailError = "Please enter a valid email (e.g. @gmail.com, @outlook.com)"
                                } else {
                                    viewModel.addTemporaryInvitee(newName, newEmail)
                                    showAddInviteeDialog = false
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(44.dp).padding(horizontal = 8.dp)
                        ) {
                            Text("Add", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

}

// Extension to draw dashed border
fun Modifier.dashedBorder(width: Dp, color: Color, cornerRadius: Dp) = drawBehind {
    drawRoundRect(
        color = color,
        style = Stroke(
            width = width.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        ),
        cornerRadius = CornerRadius(cornerRadius.toPx())
    )
}

@Composable
fun rememberBitmapFromUri(uriString: String?): ImageBitmap? {
    if (uriString == null) return null
    val context = LocalContext.current
    val uri = remember(uriString) { Uri.parse(uriString) }
    var bitmap by remember(uri) { mutableStateOf<ImageBitmap?>(null) }
    
    LaunchedEffect(uri) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val androidBitmap = BitmapFactory.decodeStream(inputStream)
                // Optionally calculate inSampleSize to avoid OOM for large images
                bitmap = androidBitmap?.asImageBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
                // Consider handling error state
            }
        }
    }
    return bitmap
}