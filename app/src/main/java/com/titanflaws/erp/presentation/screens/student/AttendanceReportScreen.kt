package com.titanflaws.erp.presentation.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.components.common.DatePicker
import com.titanflaws.erp.presentation.components.common.LoadingIndicator
import com.titanflaws.erp.presentation.viewmodel.AttendanceViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceReportScreen(
    studentId: String,
    onNavigateBack: () -> Unit,
    attendanceViewModel: AttendanceViewModel = hiltViewModel()
) {
    val attendanceUiState by attendanceViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    var startDate by remember { mutableStateOf(getStartOfMonth()) }
    var endDate by remember { mutableStateOf(Date()) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    
    // Load attendance statistics when screen is shown or date range changes
    LaunchedEffect(startDate, endDate) {
        attendanceViewModel.loadAttendanceStatistics(studentId, startDate, endDate)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Report") },
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
            // Date range selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Select Date Range",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("From:")
                            Text(
                                text = dateFormat.format(startDate),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Start Date")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("To:")
                            Text(
                                text = dateFormat.format(endDate),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        
                        IconButton(onClick = { showEndDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select End Date")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Quick date selection buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickDateButton("This Month") {
                            startDate = getStartOfMonth()
                            endDate = Date()
                        }
                        
                        QuickDateButton("Last Month") {
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.MONTH, -1)
                            startDate = getStartOfMonth(calendar.time)
                            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                            endDate = calendar.time
                        }
                        
                        QuickDateButton("This Year") {
                            startDate = getStartOfYear()
                            endDate = Date()
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Attendance summary
            if (attendanceUiState.isLoading) {
                LoadingIndicator("Loading attendance data...")
            } else {
                // Display attendance statistics
                AttendanceStatisticsCard(attendanceUiState.statisticsData)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Attendance by month
                MonthlyAttendanceChart()
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Attendance by subject (if course data is available)
                SubjectWiseAttendanceCard()
            }
        }
    }
    
    // Date pickers
    if (showStartDatePicker) {
        DatePicker(
            initialDate = startDate,
            onDateSelected = { 
                startDate = it
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }
    
    if (showEndDatePicker) {
        DatePicker(
            initialDate = endDate,
            onDateSelected = { 
                endDate = it
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}

@Composable
fun QuickDateButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(text)
    }
}

@Composable
fun AttendanceStatisticsCard(statistics: Map<String, Int>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Attendance Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Calculate total days and percentage
            val totalDays = statistics.values.sum()
            val presentDays = statistics["PRESENT"] ?: 0
            val absentDays = statistics["ABSENT"] ?: 0
            val lateDays = statistics["LATE"] ?: 0
            val leaveDays = statistics["LEAVE"] ?: 0
            val halfDays = statistics["HALF_DAY"] ?: 0
            
            val attendancePercentage = if (totalDays > 0) {
                (presentDays + (lateDays * 0.5) + (halfDays * 0.5)) * 100.0 / totalDays
            } else {
                0.0
            }
            
            // Attendance percentage indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${attendancePercentage.roundToInt()}%",
                    style = MaterialTheme.typography.headlineMedium,
                    color = when {
                        attendancePercentage >= 90.0 -> Color.Green
                        attendancePercentage >= 75.0 -> Color(0xFFFFA500) // Orange
                        else -> Color.Red
                    }
                )
                
                Text(
                    text = "Attendance Percentage",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress bar
                LinearProgressIndicator(
                    progress = (attendancePercentage / 100).toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = when {
                        attendancePercentage >= 90.0 -> Color.Green
                        attendancePercentage >= 75.0 -> Color(0xFFFFA500) // Orange
                        else -> Color.Red
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Detailed statistics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AttendanceStatusBox("Present", presentDays, Color.Green)
                AttendanceStatusBox("Absent", absentDays, Color.Red)
                AttendanceStatusBox("Late", lateDays, Color.Yellow)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AttendanceStatusBox("Leave", leaveDays, Color.Blue)
                AttendanceStatusBox("Half Day", halfDays, Color(0xFFFFA500)) // Orange
                AttendanceStatusBox("Total", totalDays, MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun AttendanceStatusBox(label: String, count: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .width(80.dp)
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = color
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MonthlyAttendanceChart() {
    // Simplified chart for now - in a real app, use a charting library
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Monthly Attendance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Chart would be displayed here using a charting library like MPAndroidChart",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "For a complete implementation, integrate a third-party charting library",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SubjectWiseAttendanceCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Subject-wise Attendance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sample subject data - in a real app, get this from the ViewModel
            val subjects = listOf(
                Triple("Mathematics", 85, Color.Green),
                Triple("Science", 92, Color.Green),
                Triple("English", 78, Color(0xFFFFA500)), // Orange
                Triple("History", 65, Color.Red),
                Triple("Computer", 88, Color.Green)
            )
            
            subjects.forEach { (name, percentage, color) ->
                SubjectAttendanceItem(name, percentage, color)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SubjectAttendanceItem(subjectName: String, percentage: Int, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = subjectName)
            Text(
                text = "$percentage%",
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = percentage / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

// Helper functions for date calculations
private fun getStartOfMonth(date: Date = Date()): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

private fun getStartOfYear(date: Date = Date()): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.DAY_OF_YEAR, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
} 