package com.titanflaws.erp.presentation.screens.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.viewmodel.SettingsViewModel

/**
 * Settings screen for the app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // App Preferences
            SettingsCategory(title = "App Preferences")
            
            // Theme
            SettingsItem(
                icon = Icons.Default.Palette,
                title = "Theme",
                subtitle = uiState.currentTheme,
                onClick = { showThemeDialog = true }
            )
            
            // Language
            SettingsItem(
                icon = Icons.Default.Language,
                title = "Language",
                subtitle = uiState.currentLanguage,
                onClick = { showLanguageDialog = true }
            )
            
            // Notification
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                subtitle = "Manage notification preferences",
                onClick = { showNotificationDialog = true }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Data & Privacy
            SettingsCategory(title = "Data & Privacy")
            
            // Offline mode
            SettingsSwitchItem(
                icon = Icons.Default.CloudOff,
                title = "Offline Mode",
                subtitle = "Download content for offline use",
                checked = uiState.offlineModeEnabled,
                onCheckedChange = { viewModel.setOfflineMode(it) }
            )
            
            // Storage usage
            SettingsItem(
                icon = Icons.Default.Storage,
                title = "Storage Usage",
                subtitle = uiState.storageUsage,
                onClick = { viewModel.calculateStorageUsage() }
            )
            
            // Clear cache
            SettingsItem(
                icon = Icons.Default.DeleteSweep,
                title = "Clear Cache",
                subtitle = "Free up space by clearing cached data",
                onClick = { viewModel.clearCache() }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // About
            SettingsCategory(title = "About")
            
            // App version
            SettingsItem(
                icon = Icons.Default.Info,
                title = "App Version",
                subtitle = uiState.appVersion,
                onClick = {}
            )
            
            // Terms of service
            SettingsItem(
                icon = Icons.Default.Description,
                title = "Terms of Service",
                subtitle = "View the terms of service",
                onClick = {}
            )
            
            // Privacy policy
            SettingsItem(
                icon = Icons.Default.Security,
                title = "Privacy Policy",
                subtitle = "View the privacy policy",
                onClick = {}
            )
            
            // Help & feedback
            SettingsItem(
                icon = Icons.Default.Help,
                title = "Help & Feedback",
                subtitle = "Get help or send feedback",
                onClick = {}
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    
    // Theme selection dialog
    if (showThemeDialog) {
        val themes = listOf("System Default", "Light", "Dark")
        
        SettingsSelectionDialog(
            title = "Choose Theme",
            options = themes,
            selectedOption = uiState.currentTheme,
            onOptionSelected = { theme ->
                viewModel.setTheme(theme)
                showThemeDialog = false
            },
            onDismissRequest = { showThemeDialog = false }
        )
    }
    
    // Language selection dialog
    if (showLanguageDialog) {
        val languages = listOf("English", "Hindi", "Spanish", "French", "German", "Chinese", "Japanese", "Arabic")
        
        SettingsSelectionDialog(
            title = "Choose Language",
            options = languages,
            selectedOption = uiState.currentLanguage,
            onOptionSelected = { language ->
                viewModel.setLanguage(language)
                showLanguageDialog = false
            },
            onDismissRequest = { showLanguageDialog = false }
        )
    }
    
    // Notification settings dialog
    if (showNotificationDialog) {
        NotificationSettingsDialog(
            emailNotificationsEnabled = uiState.emailNotificationsEnabled,
            pushNotificationsEnabled = uiState.pushNotificationsEnabled,
            reminderNotificationsEnabled = uiState.reminderNotificationsEnabled,
            onEmailNotificationsChanged = { viewModel.setEmailNotifications(it) },
            onPushNotificationsChanged = { viewModel.setPushNotifications(it) },
            onReminderNotificationsChanged = { viewModel.setReminderNotifications(it) },
            onDismissRequest = { showNotificationDialog = false }
        )
    }
}

/**
 * Settings category header
 */
@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

/**
 * Settings item with icon, title, and subtitle
 */
@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Settings item with a switch
 */
@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * Dialog for selecting an option from a list
 */
@Composable
fun SettingsSelectionDialog(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = { onOptionSelected(option) }
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(text = option)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Dialog for notification settings
 */
@Composable
fun NotificationSettingsDialog(
    emailNotificationsEnabled: Boolean,
    pushNotificationsEnabled: Boolean,
    reminderNotificationsEnabled: Boolean,
    onEmailNotificationsChanged: (Boolean) -> Unit,
    onPushNotificationsChanged: (Boolean) -> Unit,
    onReminderNotificationsChanged: (Boolean) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Notification Settings") },
        text = {
            Column {
                // Email notifications
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Email Notifications",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Text(
                            text = "Get notifications via email",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Switch(
                        checked = emailNotificationsEnabled,
                        onCheckedChange = onEmailNotificationsChanged
                    )
                }
                
                Divider()
                
                // Push notifications
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Push Notifications",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Text(
                            text = "Get notifications on your device",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Switch(
                        checked = pushNotificationsEnabled,
                        onCheckedChange = onPushNotificationsChanged
                    )
                }
                
                Divider()
                
                // Reminder notifications
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reminder Notifications",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Text(
                            text = "Get reminders for events and assignments",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Switch(
                        checked = reminderNotificationsEnabled,
                        onCheckedChange = onReminderNotificationsChanged
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text("Done")
            }
        }
    )
} 