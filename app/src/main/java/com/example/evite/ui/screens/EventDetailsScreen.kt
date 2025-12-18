package com.example.evite.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evite.data.local.entities.Event
import com.example.evite.data.local.entities.Invitee
import com.example.evite.ui.viewmodels.EventDetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.evite.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    eventId: Int,
    onBackClick: () -> Unit = {},
    onEditClick: (Event) -> Unit = {},
    onDeleteClick: (Event) -> Unit = {},
    viewModel: EventDetailsViewModel = viewModel()
) {
    val event by viewModel.event.collectAsState()
    val invitees by viewModel.invitees.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            event?.let { currentEvent ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    item {
                        // Event Image
                        EventImageSection(currentEvent)
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Event Title
                            Text(
                                text = currentEvent.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Event Description
                            Text(
                                text = currentEvent.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.times(1.3f)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Event Information Section
                            Text(
                                text = "Event Information",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Date & Time
                            EventInfoRow(
                                icon = Icons.Default.Schedule,
                                title = "Date & Time",
                                content = currentEvent.dateTime
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Location
                            EventInfoRow(
                                icon = Icons.Default.LocationOn,
                                title = "Location",
                                content = currentEvent.location
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Theme
                            EventInfoRow(
                                icon = Icons.Default.Style,
                                title = "Theme",
                                content = currentEvent.theme
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Invitees Section
                            Text(
                                text = "Invitees",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Invitees List
                    items(invitees) { invitee ->
                        InviteeItem(
                            invitee = invitee,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))

                        // Action Buttons
                        Column(
                            modifier = Modifier.padding(horizontal = 24.dp)
                        ) {
                            // Edit Event Button
                            Button(
                                onClick = { onEditClick(currentEvent) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Edit Event",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Delete Event Button
                            OutlinedButton(
                                onClick = { 
                                    viewModel.deleteEvent(currentEvent) {
                                        onBackClick()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(
                                        MaterialTheme.colorScheme.error
                                    )
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Delete Event",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) { },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun EventImageSection(event: Event) {
    val context = LocalContext.current
    var bitmap by remember(event.imageUri) { 
        mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) 
    }

    // Load image if URI is provided
    LaunchedEffect(event.imageUri) {
        if (event.imageUri != null) {
            withContext(Dispatchers.IO) {
                try {
                    val uri = Uri.parse(event.imageUri)
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val androidBitmap = BitmapFactory.decodeStream(inputStream)
                    bitmap = androidBitmap?.asImageBitmap()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!,
                contentDescription = event.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Placeholder with gradient
            val (gradientColors, _) = getThemeStyle(event.theme)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = gradientColors
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun EventInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun InviteeItem(
    invitee: Invitee,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with initial
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(48.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = (invitee.name?.firstOrNull() ?: invitee.email.firstOrNull())
                        ?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name and Status
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = invitee.name ?: invitee.email,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = invitee.status,
                style = MaterialTheme.typography.bodyMedium,
                color = when (invitee.status.lowercase()) {
                    "accepted" -> Color(0xFF4CAF50)
                    "declined" -> Color(0xFFF44336)
                    else -> Color(0xFFFF9800)
                },
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
