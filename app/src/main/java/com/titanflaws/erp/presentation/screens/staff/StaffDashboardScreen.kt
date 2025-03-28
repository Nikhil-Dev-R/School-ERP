package com.titanflaws.erp.presentation.screens.staff

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDashboardScreen(
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToManageTransport: () -> Unit,
    onNavigateToManageHostel: () -> Unit,
    onNavigateToManageInventory: () -> Unit,
//    staffDashboardViewModel: StaffDashboardViewModel = hiltViewModel()
) {
//    val uiState by staffDashboardViewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(key1 = true) {
//        staffDashboardViewModel.loadDashboardData()
    }
    
    /*Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Staff Dashboard") },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        BadgedBox(
                            badge = {
                                if (uiState.unreadNotificationsCount > 0) {
                                    Badge { Text(uiState.unreadNotificationsCount.toString()) }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications"
                            )
                        }
                    }
                    
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { *//* Already on dashboard *//* },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "Dashboard"
                        )
                    },
                    label = { Text("Dashboard") }
                )
                
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile") }
                )
                
                NavigationBarItem(
                    selected = false,
                    onClick = { showLogoutDialog = true },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout"
                        )
                    },
                    label = { Text("Logout") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LoadingIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Welcome Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Welcome, ${uiState.staffName}",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Text(
                                        text = "Department: ${uiState.department}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Staff ID: ${uiState.staffId}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                    
                    // Quick Stats
                    item {
                        Text(
                            text = "Quick Stats",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            DashboardCard(
                                title = "Tasks",
                                value = uiState.tasksCount.toString(),
                                icon = Icons.Default.Assignment,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            
                            DashboardCard(
                                title = "Pending",
                                value = uiState.pendingTasksCount.toString(),
                                icon = Icons.Default.HourglassEmpty,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    // Management Sections
                    item {
                        Text(
                            text = "Management",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Show relevant management cards based on staff department
                            if (uiState.department.contains("Transport", ignoreCase = true)) {
                                ManagementCard(
                                    title = "Transport Management",
                                    description = "Manage vehicles, routes, and schedules",
                                    icon = Icons.Default.DirectionsBus,
                                    onClick = onNavigateToManageTransport
                                )
                            }
                            
                            if (uiState.department.contains("Hostel", ignoreCase = true)) {
                                ManagementCard(
                                    title = "Hostel Management",
                                    description = "Manage rooms, allocations, and facilities",
                                    icon = Icons.Default.Hotel,
                                    onClick = onNavigateToManageHostel
                                )
                            }
                            
                            if (uiState.department.contains("Inventory", ignoreCase = true) || 
                                uiState.department.contains("Store", ignoreCase = true)) {
                                ManagementCard(
                                    title = "Inventory Management",
                                    description = "Track items, purchases, and stock levels",
                                    icon = Icons.Default.Inventory,
                                    onClick = onNavigateToManageInventory
                                )
                            }
                        }
                    }
                    
                    // Recent Tasks
                    item {
                        Text(
                            text = "Recent Tasks",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                    
                    if (uiState.recentTasks.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No recent tasks assigned")
                                }
                            }
                        }
                    } else {
                        items(uiState.recentTasks) { task ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = task.title,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        
                                        Chip(
                                            onClick = { },
                                            colors = ChipDefaults.chipColors(
                                                containerColor = when(task.status) {
                                                    "completed" -> MaterialTheme.colorScheme.primaryContainer
                                                    "in_progress" -> MaterialTheme.colorScheme.secondaryContainer
                                                    else -> MaterialTheme.colorScheme.errorContainer
                                                }
                                            )
                                        ) {
                                            Text(task.status.replace("_", " ").capitalize())
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Text(
                                        text = task.description,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Assigned: ${task.assignedDate}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        
                                        Text(
                                            text = "Due: ${task.dueDate}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    
                                    if (task.status != "completed") {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Button(
                                            onClick = {
                                                staffDashboardViewModel.updateTaskStatus(
                                                    taskId = task.id,
                                                    newStatus = if (task.status == "pending") "in_progress" else "completed"
                                                )
                                            },
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Text(
                                                text = if (task.status == "pending") "Start Task" else "Mark Completed"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Recent Notifications
                    item {
                        Text(
                            text = "Recent Notifications",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                    
                    if (uiState.recentNotifications.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No recent notifications")
                                }
                            }
                        }
                    } else {
                        items(uiState.recentNotifications.take(5)) { notification ->
                            NotificationItem(
                                notification = notification,
                                onNotificationClicked = {
                                    staffDashboardViewModel.markNotificationAsRead(notification.id)
                                }
                            )
                        }
                        
                        if (uiState.recentNotifications.size > 5) {
                            item {
                                TextButton(
                                    onClick = onNavigateToNotifications,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text("View All Notifications")
                                }
                            }
                        }
                    }
                    
                    // Bottom space for better scrolling
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
            
            // Show error message if any
            if (uiState.errorMessage != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(uiState.errorMessage!!)
                }
            }
            
            // Logout Confirmation Dialog
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Confirm Logout") },
                    text = { Text("Are you sure you want to logout?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                staffDashboardViewModel.logout()
                                showLogoutDialog = false
                            }
                        ) {
                            Text("Logout")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { showLogoutDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }*/
}

@Composable
fun ManagementCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate"
            )
        }
    }
}

private fun String.capitalize(): String {
    return this.split(" ").joinToString(" ") { 
        it.replaceFirstChar { char -> 
            if (char.isLowerCase()) char.titlecase() else char.toString() 
        }
    }
} 