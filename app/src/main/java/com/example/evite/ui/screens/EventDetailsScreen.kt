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
import com.example.evite.ui.viewmodels.EventDetailsViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.evite.navigation.NavRoutes
import com.example.evite.ui.theme.getThemeStyle
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Place
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    eventId: Int,
    currentUserId: Int,
    onBackClick: () -> Unit = {},
    onEditClick: (Event) -> Unit = {},
    onDeleteClick: (Event) -> Unit = {},
    viewModel: EventDetailsViewModel = viewModel(factory = EventDetailsViewModelFactory(LocalContext.current.applicationContext as android.app.Application, currentUserId))
) {
    val event by viewModel.event.collectAsState()
    val invitees by viewModel.invitees.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        event?.let { currentEvent ->
            val (themeColors, themeIcon) = getThemeStyle(currentEvent.theme)
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Immersive Header Section
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(360.dp)) {
                        EventImageSection(currentEvent)
                        
                        // Action buttons on top of image
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = onBackClick,
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.3f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Surface(
                                    onClick = { onEditClick(currentEvent) },
                                    shape = CircleShape,
                                    color = Color.Black.copy(alpha = 0.3f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Surface(
                                    onClick = { 
                                        viewModel.deleteEvent(currentEvent) {
                                            onBackClick()
                                        }
                                    },
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Gradient fading upwards from content
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                                            MaterialTheme.colorScheme.background
                                        ),
                                        startY = 150f
                                    )
                                )
                        )

                        // Icon Chip overlapping image and content
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 24.dp, bottom = 0.dp)
                                .size(80.dp),
                            tonalElevation = 8.dp,
                            shadowElevation = 8.dp,
                            border = BorderStroke(2.dp, themeColors.first().copy(alpha = 0.5f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = themeIcon,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = themeColors.first()
                                )
                            }
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
                        Text(
                            text = currentEvent.title,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Description Box
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = currentEvent.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp),
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.times(1.4f)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Event Details",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Info Cards grid
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoItemCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.CalendarMonth,
                                title = "Date",
                                value = currentEvent.dateTime.split(" ").firstOrNull() ?: currentEvent.dateTime,
                                color = themeColors.first()
                            )
                            InfoItemCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Schedule,
                                title = "Time",
                                value = currentEvent.dateTime.split(" ").lastOrNull() ?: "",
                                color = themeColors.first()
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        InfoItemCard(
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.Place,
                            title = "Location",
                            value = currentEvent.location,
                            color = themeColors.first()
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Guest List (${invitees.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Invitees List
                if (invitees.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .height(100.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No invitees yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(invitees) { invitee ->
                        InviteeRow(invitee)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun InfoItemCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun InviteeRow(invitee: Invitee) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Personalized Avatar
            val initials = (invitee.name?.firstOrNull() ?: invitee.email.firstOrNull())?.uppercase() ?: "?"
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = invitee.name ?: invitee.email,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = invitee.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status chip
            val statusColor = when (invitee.status.lowercase()) {
                "accepted" -> Color(0xFF10B981)
                "declined" -> Color(0xFFEF4444)
                else -> Color(0xFFF59E0B)
            }
            
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusColor.copy(alpha = 0.1f),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = invitee.status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EventImageSection(event: Event) {
    if (event.imageUri != null) {
        val context = LocalContext.current
        var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

        LaunchedEffect(event.imageUri) {
            withContext(Dispatchers.IO) {
                try {
                    val uri = Uri.parse(event.imageUri)
                    val inputStream = context.contentResolver.openInputStream(uri)
                    bitmap = BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(400.dp),
                contentScale = ContentScale.Crop
            )
        } ?: Box(
            modifier = Modifier.fillMaxWidth().height(400.dp).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        }
    } else {
        Box(
            modifier = Modifier.fillMaxWidth().height(400.dp).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        }
    }
}
