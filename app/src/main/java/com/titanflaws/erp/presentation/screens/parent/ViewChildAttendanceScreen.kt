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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.components.EmptyStateMessage
import com.titanflaws.erp.presentation.components.LoadingIndicator
import com.titanflaws.erp.presentation.viewmodel.AttendanceViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewChildAttendanceScreen(
    onNavigateBack: () -> Unit,
    studentId: String,
    attendanceViewModel: AttendanceViewModel = hiltViewModel()
) {
    val uiState by attendanceViewModel.uiState.collectAsState()
    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    
    LaunchedEffect(key1 = studentId, key2 = selectedMonth) {
//        attendanceViewModel.loadStudentAttendance(studentId, selectedMonth)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Records") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            /*if (uiState.isLoading) {
                LoadingIndicator()
            } else if (uiState.student == null) {
                EmptyStateMessage(
                    message = "Student information not found",
                    icon = Icons.Default.Person
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Student Info Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = uiState.student!!.name,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = "Class: ${uiState.student!!.className ?: "Not assigned"}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Roll No: ${uiState.student!!.rollNumber ?: "Not assigned"}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (uiState.attendanceSummary != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Present",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "${uiState.attendanceSummary!!.presentDays}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.Green
                                        )
                                    }
                                    
                                    Column {
                                        Text(
                                            text = "Absent",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "${uiState.attendanceSummary!!.absentDays}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.Red
                                        )
                                    }
                                    
                                    Column {
                                        Text(
                                            text = "Late",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "${uiState.attendanceSummary!!.lateDays}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.Yellow
                                        )
                                    }
                                    
                                    Column {
                                        Text(
                                            text = "Percentage",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "${uiState.attendanceSummary!!.attendancePercentage}%",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = when {
                                                uiState.attendanceSummary!!.attendancePercentage >= 90 -> Color.Green
                                                uiState.attendanceSummary!!.attendancePercentage >= 75 -> Color.Yellow
                                                else -> Color.Red
                                            }
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                LinearProgressIndicator(
                                    progress = { uiState.attendanceSummary!!.attendancePercentage / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    color = when {
                                        uiState.attendanceSummary!!.attendancePercentage >= 90 -> Color.Green
                                        uiState.attendanceSummary!!.attendancePercentage >= 75 -> Color.Yellow
                                        else -> Color.Red
                                    }
                                )
                            }
                        }
                    }
                    
                    // Month Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (selectedMonth > 1) {
                                    selectedMonth--
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Month"
                            )
                        }
                        
                        Text(
                            text = LocalDate.of(LocalDate.now().year, selectedMonth, 1)
                                .format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        IconButton(
                            onClick = {
                                if (selectedMonth < LocalDate.now().monthValue) {
                                    selectedMonth++
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Month"
                            )
                        }
                    }
                    
                    // Filter Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.filterStatus == "all",
                            onClick = { attendanceViewModel.updateFilter("all") },
                            label = { Text("All") }
                        )
                        
                        FilterChip(
                            selected = uiState.filterStatus == "present",
                            onClick = { attendanceViewModel.updateFilter("present") },
                            label = { Text("Present") }
                        )
                        
                        FilterChip(
                            selected = uiState.filterStatus == "absent",
                            onClick = { attendanceViewModel.updateFilter("absent") },
                            label = { Text("Absent") }
                        )
                        
                        FilterChip(
                            selected = uiState.filterStatus == "late",
                            onClick = { attendanceViewModel.updateFilter("late") },
                            label = { Text("Late") }
                        )
                    }
                    
                    // Attendance Records List
                    if (uiState.attendanceRecords.isEmpty()) {
                        EmptyStateMessage(
                            message = "No attendance records found for this month",
                            icon = Icons.Default.EventBusy
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.attendanceRecords) { record ->
                                val statusColor = when(record.status) {
                                    "present" -> Color.Green
                                    "absent" -> Color.Red
                                    "late" -> Color.Yellow
                                    else -> Color.Gray
                                }
                                
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = formatDateString(record.date),
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            
                                            Text(
                                                text = getDayOfWeek(record.date),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            val statusIcon = when(record.status) {
                                                "present" -> Icons.Default.CheckCircle
                                                "absent" -> Icons.Default.Cancel
                                                "late" -> Icons.Default.Schedule
                                                else -> Icons.Default.HelpOutline
                                            }
                                            
                                            Icon(
                                                imageVector = statusIcon,
                                                contentDescription = record.status.capitalize(),
                                                tint = statusColor
                                            )
                                            
                                            Text(
                                                text = record.status.capitalize(),
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = statusColor
                                            )
                                        }
                                    }
                                    
                                    if (record.notes.isNotEmpty()) {
                                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                                        Text(
                                            text = record.notes,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                    
                                    if (record.status == "late" && record.lateMinutes != null) {
                                        Text(
                                            text = "Late by ${record.lateMinutes} minutes",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                            
                            // Bottom space for better scrolling
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }*/
            
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
        }
    }
}

private fun formatDateString(dateString: String): String {
    try {
        val date = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE)
        return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    } catch (e: Exception) {
        return dateString
    }
}

private fun getDayOfWeek(dateString: String): String {
    try {
        val date = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE)
        return date.format(DateTimeFormatter.ofPattern("EEEE"))
    } catch (e: Exception) {
        return ""
    }
}

private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
} 