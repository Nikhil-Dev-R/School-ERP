package com.titanflaws.erp.presentation.screens.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.components.common.ErrorMessage
import com.titanflaws.erp.presentation.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen for displaying user's notifications
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (String, String) -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    IconButton(onClick = { viewModel.markAllAsRead() }) {
                        Icon(Icons.Default.DoneAll, contentDescription = "Mark all as read")
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.hasUnreadNotifications) {
                FloatingActionButton(
                    onClick = { viewModel.markAllAsRead() }
                ) {
                    Icon(
                        imageVector = Icons.Default.MarkEmailRead,
                        contentDescription = "Mark all as read"
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                ErrorMessage(
                    message = uiState.error!!,
                    onRetry = { viewModel.loadNotifications() },
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.notifications.isEmpty()) {
                EmptyNotifications(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.notifications, key = { it.id }) { notification ->
                        NotificationItem(
                            notification = notification,
                            onItemClick = {
                                viewModel.markAsRead(notification.id)
                                onNavigateToDetails(notification.type, notification.targetId)
                            },
                            onDismiss = { viewModel.dismissNotification(notification.id) }
                        )
                    }
                }
            }
            
            // Filter menu
            DropdownMenu(
                expanded = showFilterMenu,
                onDismissRequest = { showFilterMenu = false },
                modifier = Modifier.width(200.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("All") },
                    onClick = {
                        viewModel.filterNotifications(null)
                        showFilterMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Unread") },
                    onClick = {
                        viewModel.filterNotifications("unread")
                        showFilterMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Announcements") },
                    onClick = {
                        viewModel.filterNotifications("announcement")
                        showFilterMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Assignments") },
                    onClick = {
                        viewModel.filterNotifications("assignment")
                        showFilterMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Exams") },
                    onClick = {
                        viewModel.filterNotifications("exam")
                        showFilterMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Attendance") },
                    onClick = {
                        viewModel.filterNotifications("attendance")
                        showFilterMenu = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItem(
    notification: Notification,
    onItemClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { it * 0.5f },
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                true
            } else {
                false
            }
        }
    )
    
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Notification",
                    modifier = Modifier.padding(end = 16.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onItemClick),
                colors = CardDefaults.cardColors(
                    containerColor = if (notification.read)
                        MaterialTheme.colorScheme.surface
                    else
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notification icon with background
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(getNotificationColor(notification.type)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getNotificationIcon(notification.type),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Notification content
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = notification.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (notification.read)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = notification.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = formatTimestamp(notification.timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (!notification.read) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun EmptyNotifications(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Notifications",
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "You're all caught up! Check back later for new notifications.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun getNotificationIcon(type: String): ImageVector {
    return when (type) {
        "announcement" -> Icons.Default.Campaign
        "assignment" -> Icons.Default.Assignment
        "exam" -> Icons.Default.Quiz
        "grade" -> Icons.Default.Grade
        "attendance" -> Icons.Default.HowToReg
        "message" -> Icons.Default.Message
        "payment" -> Icons.Default.Payment
        else -> Icons.Default.Notifications
    }
}

@Composable
fun getNotificationColor(type: String): Color {
    return when (type) {
        "announcement" -> MaterialTheme.colorScheme.primary
        "assignment" -> MaterialTheme.colorScheme.secondary
        "exam" -> MaterialTheme.colorScheme.tertiary
        "grade" -> MaterialTheme.colorScheme.error
        "attendance" -> MaterialTheme.colorScheme.secondary
        "message" -> MaterialTheme.colorScheme.primary
        "payment" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }
}

fun formatTimestamp(timestamp: Date): String {
    val now = Date()
    val diffMs = now.time - timestamp.time
    val diffSec = diffMs / 1000
    val diffMin = diffSec / 60
    val diffHours = diffMin / 60
    val diffDays = diffHours / 24
    
    return when {
        diffMin < 1 -> "Just now"
        diffMin < 60 -> "$diffMin minute${if (diffMin == 1L) "" else "s"} ago"
        diffHours < 24 -> "$diffHours hour${if (diffHours == 1L) "" else "s"} ago"
        diffDays < 7 -> "$diffDays day${if (diffDays == 1L) "" else "s"} ago"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(timestamp)
    }
}

/**
 * Data class for notification
 */
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val timestamp: Date,
    val read: Boolean = false,
    val targetId: String = "", // ID of the target item (exam, assignment, etc.)
    val sender: String = ""
) 