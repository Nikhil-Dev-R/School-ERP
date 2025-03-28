package com.titanflaws.erp.presentation.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.viewmodel.SettingsViewModel
import java.util.*

/**
 * School Settings Screen for administrators
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolSettingsScreen(
    onNavigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    
    // School settings mock data
    var schoolName by remember { mutableStateOf("Vista Academy") }
    var schoolAddress by remember { mutableStateOf("123 Education Street, Learning City, 45678") }
    var schoolEmail by remember { mutableStateOf("info@vistaacademy.edu") }
    var schoolPhone by remember { mutableStateOf("+1 234 567 8900") }
    var academicYear by remember { mutableStateOf("2023-2024") }
    var currentTermName by remember { mutableStateOf("Spring Term") }
    var termStartDate by remember { mutableStateOf(Calendar.getInstance().apply { set(2023, 1, 1) }.time) }
    var termEndDate by remember { mutableStateOf(Calendar.getInstance().apply { set(2023, 5, 30) }.time) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    
    // Boolean states for switches
    var enableFeesReminders by remember { mutableStateOf(true) }
    var enableAttendanceNotifications by remember { mutableStateOf(true) }
    var enableParentAccess by remember { mutableStateOf(true) }
    var enableOnlineExams by remember { mutableStateOf(false) }
    var enableMaintenanceMode by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("School Settings") },
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
                .padding(16.dp)
        ) {
            if (showSuccessMessage) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = "Settings saved successfully!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        
                        IconButton(
                            onClick = { showSuccessMessage = false }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
            
            // School Information Settings
            SettingSection(
                title = "School Information",
                icon = Icons.Default.School
            ) {
                OutlinedTextField(
                    value = schoolName,
                    onValueChange = { schoolName = it },
                    label = { Text("School Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = schoolAddress,
                    onValueChange = { schoolAddress = it },
                    label = { Text("School Address") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = schoolEmail,
                        onValueChange = { schoolEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = schoolPhone,
                        onValueChange = { schoolPhone = it },
                        label = { Text("Phone") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        singleLine = true
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Academic Settings
            SettingSection(
                title = "Academic Settings",
                icon = Icons.Default.DateRange
            ) {
                OutlinedTextField(
                    value = academicYear,
                    onValueChange = { academicYear = it },
                    label = { Text("Academic Year") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = currentTermName,
                    onValueChange = { currentTermName = it },
                    label = { Text("Current Term") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Term date selectors would go here
                    // Using text fields as placeholders
                    OutlinedTextField(
                        value = "01/02/2023",
                        onValueChange = { },
                        label = { Text("Term Start Date") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        singleLine = true,
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                        }
                    )
                    
                    OutlinedTextField(
                        value = "30/05/2023",
                        onValueChange = { },
                        label = { Text("Term End Date") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        singleLine = true,
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Features & Notifications
            SettingSection(
                title = "Features & Notifications",
                icon = Icons.Default.Notifications
            ) {
                SettingSwitch(
                    title = "Fee Payment Reminders",
                    description = "Send automatic reminders for due fee payments",
                    checked = enableFeesReminders,
                    onCheckedChange = { enableFeesReminders = it }
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                SettingSwitch(
                    title = "Attendance Notifications",
                    description = "Send daily notifications about student attendance to parents",
                    checked = enableAttendanceNotifications,
                    onCheckedChange = { enableAttendanceNotifications = it }
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                SettingSwitch(
                    title = "Parent Portal Access",
                    description = "Allow parents to access student information through the portal",
                    checked = enableParentAccess,
                    onCheckedChange = { enableParentAccess = it }
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                SettingSwitch(
                    title = "Online Exams",
                    description = "Enable online exam platform for students",
                    checked = enableOnlineExams,
                    onCheckedChange = { enableOnlineExams = it }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // System Settings
            SettingSection(
                title = "System Settings",
                icon = Icons.Default.Settings
            ) {
                SettingSwitch(
                    title = "Maintenance Mode",
                    description = "Put the system in maintenance mode (users will be unable to access)",
                    checked = enableMaintenanceMode,
                    onCheckedChange = { enableMaintenanceMode = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { /* Backup logic */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backup,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Backup Data")
                    }
                    
                    Button(
                        onClick = { /* Restore logic */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Restore")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Save button
            Button(
                onClick = { 
                    // Save settings logic would go here
                    showSuccessMessage = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Settings")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Section container for settings
 */
@Composable
fun SettingSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            content()
        }
    }
}

/**
 * Setting switch component
 */
@Composable
fun SettingSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
} 