package com.titanflaws.erp.presentation.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.titanflaws.erp.data.model.Attendance
import com.titanflaws.erp.data.model.Student
import com.titanflaws.erp.presentation.components.common.DatePicker
import com.titanflaws.erp.presentation.components.common.LoadingIndicator
import com.titanflaws.erp.presentation.viewmodel.AttendanceViewModel
import com.titanflaws.erp.presentation.viewmodel.StudentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    classId: String,
    sectionId: String,
    courseId: String? = null,
    onNavigateBack: () -> Unit,
    attendanceViewModel: AttendanceViewModel = hiltViewModel(),
    studentViewModel: StudentViewModel = hiltViewModel()
) {
    val attendanceUiState by attendanceViewModel.uiState.collectAsState()
    val studentUiState by studentViewModel.uiState.collectAsState()
    val selectedDate = remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
    
    // Load class students when screen is first shown
    LaunchedEffect(key1 = classId, key2 = sectionId) {
        studentViewModel.loadStudentsByClass(classId, sectionId)
    }
    
    // Load attendance for selected date and class
    LaunchedEffect(key1 = classId, key2 = sectionId, key3 = selectedDate.value) {
        attendanceViewModel.loadClassAttendance(classId, selectedDate.value)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { attendanceViewModel.syncAttendanceData() }) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync Data")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Date selection section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Date: ${dateFormat.format(selectedDate.value)}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Button(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Change Date")
                }
            }
            
            // Class and Section Info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Class: ${classId}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Section: ${sectionId}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    courseId?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Course: ${courseId}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Legend for attendance status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AttendanceStatusLegend(status = "PRESENT", color = Color.Green)
                AttendanceStatusLegend(status = "ABSENT", color = Color.Red)
                AttendanceStatusLegend(status = "LATE", color = Color.Yellow)
                AttendanceStatusLegend(status = "LEAVE", color = Color.Blue)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Loading indicator
            if (attendanceUiState.isLoading || studentUiState.isLoading) {
                LoadingIndicator()
            } else if (studentUiState.errorMessage != null) {
                // Error message
                Text(
                    text = studentUiState.errorMessage ?: "Error loading students",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                // Attendance list
                Text(
                    text = "Student Attendance",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    items(studentUiState.students) { student ->
                        StudentAttendanceItem(
                            student = student,
                            attendance = attendanceUiState.attendanceList.find { it.userId == student.userId },
                            onMarkAttendance = { status, reason, remarks ->
                                attendanceViewModel.markAttendance(
                                    userId = student.userId,
                                    userType = "STUDENT",
                                    date = selectedDate.value,
                                    status = status,
                                    courseId = courseId,
                                    classId = classId,
                                    sectionId = sectionId,
                                    reason = reason,
                                    remarks = remarks
                                )
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
    
    // Date picker dialog
    if (showDatePicker) {
        DatePicker(
            initialDate = selectedDate.value,
            onDateSelected = { 
                selectedDate.value = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun AttendanceStatusLegend(status: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = status, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun StudentAttendanceItem(
    student: Student,
    attendance: Attendance?,
    onMarkAttendance: (status: String, reason: String?, remarks: String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showReasonDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(attendance?.status ?: "PRESENT") }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${student.firstName} ${student.lastName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Roll No: ${student.rollNumber}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                // Status indicator
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            when (attendance?.status) {
                                "PRESENT" -> Color.Green
                                "ABSENT" -> Color.Red
                                "LATE" -> Color.Yellow
                                "LEAVE" -> Color.Blue
                                else -> Color.LightGray
                            }
                        )
                        .border(1.dp, Color.Black)
                )
            }
            
            // Expanded view for attendance marking
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Mark Attendance:",
                    style = MaterialTheme.typography.titleSmall
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AttendanceButton(
                        text = "Present",
                        color = Color.Green,
                        isSelected = selectedStatus == "PRESENT",
                        onClick = { 
                            selectedStatus = "PRESENT"
                            onMarkAttendance(selectedStatus, null, null)
                        }
                    )
                    
                    AttendanceButton(
                        text = "Absent",
                        color = Color.Red,
                        isSelected = selectedStatus == "ABSENT",
                        onClick = { 
                            selectedStatus = "ABSENT"
                            showReasonDialog = true
                        }
                    )
                    
                    AttendanceButton(
                        text = "Late",
                        color = Color.Yellow,
                        isSelected = selectedStatus == "LATE",
                        onClick = { 
                            selectedStatus = "LATE"
                            showReasonDialog = true
                        }
                    )
                    
                    AttendanceButton(
                        text = "Leave",
                        color = Color.Blue,
                        isSelected = selectedStatus == "LEAVE",
                        onClick = { 
                            selectedStatus = "LEAVE"
                            showReasonDialog = true
                        }
                    )
                }
                
                if (attendance != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Status: ${attendance.status}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (attendance.reason != null) {
                        Text(
                            text = "Reason: ${attendance.reason}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (attendance.remarks != null) {
                        Text(
                            text = "Remarks: ${attendance.remarks}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
    
    // Reason dialog
    if (showReasonDialog) {
        ReasonDialog(
            status = selectedStatus,
            onConfirm = { reason, remarks ->
                onMarkAttendance(selectedStatus, reason, remarks)
                showReasonDialog = false
            },
            onDismiss = { showReasonDialog = false }
        )
    }
}

@Composable
fun AttendanceButton(
    text: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color else Color.LightGray
        ),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(text = text)
    }
}

@Composable
fun ReasonDialog(
    status: String,
    onConfirm: (reason: String, remarks: String) -> Unit,
    onDismiss: () -> Unit
) {
    var reason by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reason for $status") },
        text = {
            Column {
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(reason, remarks) }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
} 