package com.example.evite.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evite.ui.viewmodels.EventViewModel
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
    onEventCreated: () -> Unit,
    onBack: () -> Unit,
    onAddInviteeClick: () -> Unit,
    viewModel: EventViewModel = viewModel()
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Event", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // SINGLE Scrollable Column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Event Title
                Column {
                    Text("Event Title", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { viewModel.eventTitle.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("My Awesome Event") },
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Description
                Column {
                    Text("Description", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { viewModel.eventDescription.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Tell us more about your event...") },
                        minLines = 3,
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Date & Time
                Column {
                    Text("Date & Time", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = date,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() }, 
                        enabled = false, 
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                            }
                        },
                        placeholder = { Text("Select Date & Time") },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Location
                Column {
                    Text("Location", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { viewModel.eventLocation.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        placeholder = { Text("Event Venue, City") },
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Theme Dropdown
                Column {
                    Text("Theme", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
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
                            placeholder = { Text("Select a theme") },
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            themes.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
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
                    Text("Cover Photo", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val context = LocalContext.current
                    val imageLauncher = rememberLauncherForActivityResult(
                        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
                    ) { uri ->
                        if (uri != null) {
                            // Persist permission (optional but good for some use cases)
                            // context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                             viewModel.eventImageUri.value = uri.toString()
                        }
                    }
                    
                    val selectedImageUriString by viewModel.eventImageUri.collectAsState()
                    val selectedImageBitmap = rememberBitmapFromUri(selectedImageUriString)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .dashedBorder(2.dp, MaterialTheme.colorScheme.outline, 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
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
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward, 
                                    contentDescription = "Upload",
                                    modifier = Modifier.size(32.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Drag & drop or click to upload",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { 
                            imageLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (selectedImageUriString != null) "Change Photo" else "Upload Photo")
                    }
                }

                // Invitees Section
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Invitees", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        FilledTonalButton(
                            onClick = { showAddInviteeDialog = true },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Invitee")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (temporaryInvitees.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No invitees added yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // List of added invitees (Compact & Designed)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            temporaryInvitees.forEach { invitee ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerLow, // Lighter background
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 12.dp, vertical = 8.dp) // Compact padding
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Avatar Icon (Left)
                                        Surface(
                                            shape = androidx.compose.foundation.shape.CircleShape,
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp),
                                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        // Text Info
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = invitee.email,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1
                                            )
                                            if (!invitee.name.isNullOrEmpty()) {
                                                Text(
                                                    text = invitee.name,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1
                                                )
                                            }
                                        }

                                        // Remove Button
                                        IconButton(
                                            onClick = { viewModel.removeTemporaryInvitee(invitee) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remove",
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = { viewModel.saveEvent() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                         if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Save Event", fontSize = 16.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp)) // Extra space at very bottom
            }
            
            // Full screen loading overlay
            if (isLoading) {
               Box(
                   modifier = Modifier
                       .fillMaxSize()
                       .background(Color.Black.copy(alpha = 0.5f))
                       .clickable(enabled = false) {},
                   contentAlignment = Alignment.Center
               ) {
                   CircularProgressIndicator()
               }
            }

            // ADD INVITEE MODAL (Custom Dialog)
            if (showAddInviteeDialog) {
                var newEmail by remember { mutableStateOf("") }
                var newName by remember { mutableStateOf("") }

                androidx.compose.ui.window.Dialog(
                    onDismissRequest = { showAddInviteeDialog = false }
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                "Send Invitation", 
                                style = MaterialTheme.typography.headlineSmall, 
                                fontWeight = FontWeight.Bold
                            )
                            
                            // Email
                            Column {
                                Text("Email Address *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = newEmail,
                                    onValueChange = { newEmail = it },
                                    placeholder = { Text("john@example.com") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )
                            }
                            // Name
                            Column {
                                Text("Name (Optional)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = newName,
                                    onValueChange = { newName = it },
                                    placeholder = { Text("John Doe") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { showAddInviteeDialog = false }) {
                                    Text("Cancel")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (newEmail.isNotBlank()) {
                                            viewModel.addTemporaryInvitee(newName, newEmail)
                                            showAddInviteeDialog = false
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Invite")
                                }
                            }
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