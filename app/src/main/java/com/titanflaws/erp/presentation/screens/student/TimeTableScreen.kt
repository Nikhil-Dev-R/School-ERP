package com.titanflaws.erp.presentation.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.components.common.ErrorMessage
import com.titanflaws.erp.presentation.components.common.LoadingIndicator
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data class to represent a class session
 */
data class ClassSession(
    val id: String,
    val subject: String,
    val teacher: String,
    val startTime: Date,
    val endTime: Date,
    val room: String,
    val day: String,
    val color: Color
)

/**
 * Screen to display student's weekly timetable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeTableScreen(
    onNavigateBack: () -> Unit,
//    timetableViewModel: TimetableViewModel = hiltViewModel()
) {
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val selectedDay = remember { mutableStateOf(getCurrentDay(days)) }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    
    // Mock data - would be replaced with data from the ViewModel
    val classesMap = remember {
        mapOf(
            "Monday" to listOf(
                ClassSession(
                    id = "1",
                    subject = "Mathematics",
                    teacher = "Mr. Johnson",
                    startTime = getTimeFromHourMin(8, 0),
                    endTime = getTimeFromHourMin(9, 30),
                    room = "Room 101",
                    day = "Monday",
                    color = Color(0xFF2196F3) // Blue
                ),
                ClassSession(
                    id = "2",
                    subject = "Physics",
                    teacher = "Mrs. Smith",
                    startTime = getTimeFromHourMin(9, 45),
                    endTime = getTimeFromHourMin(11, 15),
                    room = "Room 205",
                    day = "Monday",
                    color = Color(0xFF4CAF50) // Green
                ),
                ClassSession(
                    id = "3",
                    subject = "English Literature",
                    teacher = "Ms. Davis",
                    startTime = getTimeFromHourMin(12, 30),
                    endTime = getTimeFromHourMin(14, 0),
                    room = "Room 303",
                    day = "Monday",
                    color = Color(0xFFF44336) // Red
                )
            ),
            "Tuesday" to listOf(
                ClassSession(
                    id = "4",
                    subject = "Chemistry",
                    teacher = "Dr. Wilson",
                    startTime = getTimeFromHourMin(8, 0),
                    endTime = getTimeFromHourMin(9, 30),
                    room = "Lab 2",
                    day = "Tuesday",
                    color = Color(0xFF9C27B0) // Purple
                ),
                ClassSession(
                    id = "5",
                    subject = "Computer Science",
                    teacher = "Mr. Anderson",
                    startTime = getTimeFromHourMin(9, 45),
                    endTime = getTimeFromHourMin(11, 15),
                    room = "Room 405",
                    day = "Tuesday",
                    color = Color(0xFFFF9800) // Orange
                )
            ),
            "Wednesday" to listOf(
                ClassSession(
                    id = "6",
                    subject = "History",
                    teacher = "Mrs. Thompson",
                    startTime = getTimeFromHourMin(8, 0),
                    endTime = getTimeFromHourMin(9, 30),
                    room = "Room 201",
                    day = "Wednesday",
                    color = Color(0xFF795548) // Brown
                ),
                ClassSession(
                    id = "7",
                    subject = "Physical Education",
                    teacher = "Coach Roberts",
                    startTime = getTimeFromHourMin(9, 45),
                    endTime = getTimeFromHourMin(11, 15),
                    room = "Gymnasium",
                    day = "Wednesday",
                    color = Color(0xFF009688) // Teal
                ),
                ClassSession(
                    id = "8",
                    subject = "Mathematics",
                    teacher = "Mr. Johnson",
                    startTime = getTimeFromHourMin(12, 30),
                    endTime = getTimeFromHourMin(14, 0),
                    room = "Room 101",
                    day = "Wednesday",
                    color = Color(0xFF2196F3) // Blue
                )
            ),
            "Thursday" to listOf(
                ClassSession(
                    id = "9",
                    subject = "Biology",
                    teacher = "Dr. Garcia",
                    startTime = getTimeFromHourMin(8, 0),
                    endTime = getTimeFromHourMin(9, 30),
                    room = "Lab 1",
                    day = "Thursday",
                    color = Color(0xFF8BC34A) // Light Green
                ),
                ClassSession(
                    id = "10",
                    subject = "Art",
                    teacher = "Ms. Lopez",
                    startTime = getTimeFromHourMin(9, 45),
                    endTime = getTimeFromHourMin(11, 15),
                    room = "Art Studio",
                    day = "Thursday",
                    color = Color(0xFFE91E63) // Pink
                )
            ),
            "Friday" to listOf(
                ClassSession(
                    id = "11",
                    subject = "Geography",
                    teacher = "Mr. Clark",
                    startTime = getTimeFromHourMin(8, 0),
                    endTime = getTimeFromHourMin(9, 30),
                    room = "Room 203",
                    day = "Friday",
                    color = Color(0xFF3F51B5) // Indigo
                ),
                ClassSession(
                    id = "12",
                    subject = "Music",
                    teacher = "Mrs. Baker",
                    startTime = getTimeFromHourMin(9, 45),
                    endTime = getTimeFromHourMin(11, 15),
                    room = "Music Room",
                    day = "Friday",
                    color = Color(0xFF00BCD4) // Cyan
                ),
                ClassSession(
                    id = "13",
                    subject = "Physics",
                    teacher = "Mrs. Smith",
                    startTime = getTimeFromHourMin(12, 30),
                    endTime = getTimeFromHourMin(14, 0),
                    room = "Room 205",
                    day = "Friday",
                    color = Color(0xFF4CAF50) // Green
                )
            ),
            "Saturday" to emptyList()
        )
    }
    
    val selectedDayClasses = classesMap[selectedDay.value] ?: emptyList()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Class Schedule") },
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
        ) {
            // Day selector tabs
            ScrollableTabRow(
                selectedTabIndex = days.indexOf(selectedDay.value),
                edgePadding = 0.dp
            ) {
                days.forEachIndexed { index, day ->
                    Tab(
                        selected = selectedDay.value == day,
                        onClick = { selectedDay.value = day },
                        text = { Text(day) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
            
            // Error message
            errorMessage.value?.let { message ->
                ErrorMessage(
                    message = message,
                    onDismiss = { errorMessage.value = null }
                )
            }
            
            // Content
            if (isLoading.value) {
                LoadingIndicator(message = "Loading schedule...")
            } else if (selectedDayClasses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "No classes scheduled for ${selectedDay.value}",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(selectedDayClasses) { classSession ->
                        ClassSessionCard(classSession = classSession)
                    }
                }
            }
        }
    }
}

/**
 * Card component to display a class session
 */
@Composable
fun ClassSessionCard(classSession: ClassSession) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Color bar on the left
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .fillMaxHeight()
                    .background(classSession.color)
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = classSession.subject,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Teacher: ${classSession.teacher}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "${timeFormat.format(classSession.startTime)} - ${timeFormat.format(classSession.endTime)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Location pill
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = classSession.room,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper function to get the current day of the week
 */
private fun getCurrentDay(availableDays: List<String>): String {
    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    
    // Convert Calendar.DAY_OF_WEEK to our days list index (Calendar uses 1=Sunday, 7=Saturday)
    return when (dayOfWeek) {
        Calendar.MONDAY -> availableDays[0]
        Calendar.TUESDAY -> availableDays[1]
        Calendar.WEDNESDAY -> availableDays[2]
        Calendar.THURSDAY -> availableDays[3]
        Calendar.FRIDAY -> availableDays[4]
        Calendar.SATURDAY -> availableDays[5]
        else -> availableDays[0] // Default to Monday for Sunday
    }
}

/**
 * Helper function to create a Date object from hour and minute
 */
private fun getTimeFromHourMin(hour: Int, minute: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    return calendar.time
} 