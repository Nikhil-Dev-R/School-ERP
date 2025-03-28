package com.titanflaws.erp.presentation.screens.parent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.components.*
//import com.titanflaws.erp.presentation.viewmodel.ParentDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToChildAttendance: (String) -> Unit,
    onNavigateToChildProgress: (String) -> Unit,
    onNavigateToFeePayment: (String) -> Unit,
//    parentDashboardViewModel: ParentDashboardViewModel = hiltViewModel()
) {
//    val uiState by parentDashboardViewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(key1 = true) {
//        parentDashboardViewModel.loadDashboardData()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parent Dashboard") },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(
                            imageVector = Icons.Default.Notifications, 
                            contentDescription = "Notifications"
                        )
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
                    onClick = { /* Already on dashboard */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Navigate to calendar or schedule */ },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar") },
                    label = { Text("Calendar") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showLogoutDialog = true },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Logout") },
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
//            if (uiState.isLoading) {
//                LoadingIndicator()
//            } else if (uiState.children.isEmpty()) {
//                EmptyStateMessage(
//                    message = "No children found. Please contact the school administration.",
//                    icon = Icons.Default.ChildCare
//                )
//            } else {
//                LazyColumn(
//                    modifier = Modifier.fillMaxSize(),
//                    contentPadding = PaddingValues(16.dp)
//                ) {
//                    item {
//                        WelcomeCard(
//                            name = uiState.parentName,
//                            message = "Welcome back! Here's an overview of your children's activities."
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                    }
//
//                    item {
//                        Text(
//                            "Your Children",
//                            style = MaterialTheme.typography.titleLarge,
//                            modifier = Modifier.padding(vertical = 8.dp)
//                        )
//                    }
//
//                    items(uiState.children) { child ->
//                        Card(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 8.dp)
//                        ) {
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(16.dp)
//                            ) {
//                                Text(
//                                    text = child.name,
//                                    style = MaterialTheme.typography.titleMedium
//                                )
//                                Text(
//                                    text = "Class: ${child.className ?: "Not assigned"}",
//                                    style = MaterialTheme.typography.bodyMedium
//                                )
//                                Text(
//                                    text = "Roll No: ${child.rollNumber ?: "Not assigned"}",
//                                    style = MaterialTheme.typography.bodyMedium
//                                )
//
//                                Spacer(modifier = Modifier.height(8.dp))
//
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    StatCard(
//                                        title = "Attendance",
//                                        value = "${child.attendancePercentage}%",
//                                        icon = Icons.Default.HowToReg,
//                                        modifier = Modifier.weight(1f)
//                                    )
//                                    Spacer(modifier = Modifier.width(8.dp))
//                                    StatCard(
//                                        title = "Fees Status",
//                                        value = if (child.feesPaid) "Paid" else "Due",
//                                        icon = Icons.Default.AccountBalance,
//                                        modifier = Modifier.weight(1f)
//                                    )
//                                }
//
//                                Spacer(modifier = Modifier.height(16.dp))
//
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceEvenly
//                                ) {
//                                    ActionButton(
//                                        text = "Attendance",
//                                        icon = Icons.Default.CalendarToday,
//                                        onClick = { onNavigateToChildAttendance(child.id) }
//                                    )
//                                    ActionButton(
//                                        text = "Progress",
//                                        icon = Icons.Default.TrendingUp,
//                                        onClick = { onNavigateToChildProgress(child.id) }
//                                    )
//                                    ActionButton(
//                                        text = "Fees",
//                                        icon = Icons.Default.Payment,
//                                        onClick = { onNavigateToFeePayment(child.id) }
//                                    )
//                                }
//                            }
//                        }
//                    }
//
//                    item {
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text(
//                            "Recent Notifications",
//                            style = MaterialTheme.typography.titleLarge,
//                            modifier = Modifier.padding(vertical = 8.dp)
//                        )
//                    }
//
//                    items(uiState.recentNotifications) { notification ->
//                        NotificationItem(notification = notification)
//                    }
//
//                    item {
//                        if (uiState.recentNotifications.isEmpty()) {
//                            Text(
//                                "No recent notifications",
//                                style = MaterialTheme.typography.bodyMedium,
//                                textAlign = TextAlign.Center,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 16.dp)
//                            )
//                        }
//                    }
//                }
//            }
//
//            // Show error message if any
//            if (uiState.errorMessage != null) {
//                Snackbar(
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//                        .padding(16.dp)
//                ) {
//                    Text(uiState.errorMessage!!)
//                }
//            }
        }
    }
    
    // Logout confirmation dialog
    if (showLogoutDialog) {
        ConfirmationDialog(
            title = "Logout",
            message = "Are you sure you want to logout?",
            onConfirm = {
//                parentDashboardViewModel.logout()
                showLogoutDialog = false
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(80.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
} 