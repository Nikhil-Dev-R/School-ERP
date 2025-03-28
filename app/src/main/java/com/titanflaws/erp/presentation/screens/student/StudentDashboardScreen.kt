package com.titanflaws.erp.presentation.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.components.common.ErrorMessage
import com.titanflaws.erp.presentation.viewmodel.StudentDashboardViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dashboard screen for student users
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    onNavigateToCourses: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToTimetable: () -> Unit,
    onNavigateToExams: () -> Unit,
    onNavigateToAssignments: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: StudentDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Dashboard") },
                actions = {
                    BadgedBox(
                        badge = {
                            if (uiState.unreadNotificationsCount > 0) {
                                Badge { Text(uiState.unreadNotificationsCount.toString()) }
                            }
                        }
                    ) {
                        IconButton(onClick = onNavigateToNotifications) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                }
            )
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
                    onRetry = { viewModel.loadDashboardData() },
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Welcome message with current date
                    WelcomeHeader(
                        studentName = uiState.studentName,
                        className = uiState.className
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Quick stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AttendanceCard(
                            attendancePercentage = uiState.attendancePercentage,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToAttendance
                        )
                        
                        UpcomingExamsCard(
                            upcomingExamsCount = uiState.upcomingExamsCount,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToExams
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Today's Schedule
                    TodayScheduleCard(
                        scheduleItems = uiState.todaySchedule,
                        onViewFullSchedule = onNavigateToTimetable
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Pending Assignments
                    if (uiState.pendingAssignments.isNotEmpty()) {
                        PendingAssignmentsCard(
                            assignments = uiState.pendingAssignments,
                            onViewAllAssignments = onNavigateToAssignments
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Courses
                    EnrolledCoursesCard(
                        cours = uiState.enrolledCours,
                        onViewAllCourses = onNavigateToCourses
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Quick Actions
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickActionButton(
                            icon = Icons.Default.CalendarMonth,
                            label = "Timetable",
                            onClick = onNavigateToTimetable
                        )
                        
                        QuickActionButton(
                            icon = Icons.Default.Assignment,
                            label = "Assignments",
                            onClick = onNavigateToAssignments
                        )
                        
                        QuickActionButton(
                            icon = Icons.Default.Grade,
                            label = "Exams",
                            onClick = onNavigateToExams
                        )
                        
                        QuickActionButton(
                            icon = Icons.Default.Settings,
                            label = "Settings",
                            onClick = onNavigateToSettings
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun WelcomeHeader(
    studentName: String,
    className: String
) {
    val currentDate = remember {
        SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Hello, $studentName!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Class: $className",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = currentDate,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AttendanceCard(
    attendancePercentage: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Attendance",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${attendancePercentage.toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = when {
                    attendancePercentage < 75f -> MaterialTheme.colorScheme.error
                    attendancePercentage < 85f -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { attendancePercentage / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    attendancePercentage < 75f -> MaterialTheme.colorScheme.error
                    attendancePercentage < 85f -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = if (attendancePercentage < 75f) "Needs Improvement" 
                       else if (attendancePercentage < 85f) "Good" 
                       else "Excellent",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun UpcomingExamsCard(
    upcomingExamsCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Upcoming Exams",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = upcomingExamsCount.toString(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (upcomingExamsCount == 0) "No upcoming exams" 
                       else "Tap to view details",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TodayScheduleCard(
    scheduleItems: List<ScheduleItem>,
    onViewFullSchedule: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onViewFullSchedule) {
                    Text("View All")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (scheduleItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No classes scheduled for today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                scheduleItems.forEach { item ->
                    ScheduleItemRow(item = item)
                    
                    if (scheduleItems.last() != item) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleItemRow(item: ScheduleItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Time column
        Column(
            modifier = Modifier.width(80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.startTime,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "to",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = item.endTime,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Vertical line
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(60.dp)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Subject info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.subjectName,
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = "Teacher: ${item.teacherName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Room: ${item.roomNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Status indicator
        if (item.isCurrentClass) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "CURRENT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun PendingAssignmentsCard(
    assignments: List<Assignment>,
    onViewAllAssignments: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pending Assignments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onViewAllAssignments) {
                    Text("View All")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            assignments.take(3).forEach { assignment ->
                AssignmentItem(assignment = assignment)
                
                if (assignments.take(3).last() != assignment) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
            
            if (assignments.size > 3) {
                TextButton(
                    onClick = onViewAllAssignments,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("${assignments.size - 3} more assignments")
                }
            }
        }
    }
}

@Composable
fun AssignmentItem(assignment: Assignment) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Subject indicator
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = assignment.subjectCode,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Assignment details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = assignment.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "Due: ${assignment.dueDate}",
                style = MaterialTheme.typography.bodySmall,
                color = if (assignment.isUrgent) MaterialTheme.colorScheme.error 
                        else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Status indicator
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                    if (assignment.isUrgent) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.secondaryContainer
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (assignment.isUrgent) "URGENT" else "PENDING",
                style = MaterialTheme.typography.labelSmall,
                color = if (assignment.isUrgent) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun EnrolledCoursesCard(
    cours: List<DisplayCourse>,
    onViewAllCourses: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enrolled Courses",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onViewAllCourses) {
                    Text("View All")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            cours.take(3).forEach { course ->
                CourseItem(displayCourse = course)
                
                if (cours.take(3).last() != course) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
            
            if (cours.size > 3) {
                TextButton(
                    onClick = onViewAllCourses,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("${cours.size - 3} more courses")
                }
            }
        }
    }
}

@Composable
fun CourseItem(displayCourse: DisplayCourse) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Course indicator
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayCourse.code,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Course details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = displayCourse.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "Teacher: ${displayCourse.teacherName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Progress indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${displayCourse.progress}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            LinearProgressIndicator(
                progress = { displayCourse.progress / 100f },
                modifier = Modifier.width(60.dp),
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

// Data classes for the dashboard

data class ScheduleItem(
    val subjectName: String,
    val teacherName: String,
    val roomNumber: String,
    val startTime: String,
    val endTime: String,
    val isCurrentClass: Boolean = false
)

data class Assignment(
    val id: String,
    val title: String,
    val subjectCode: String,
    val dueDate: String,
    val isUrgent: Boolean = false
)

data class DisplayCourse(
    val id: String,
    val name: String,
    val code: String,
    val teacherName: String,
    val progress: Float = 0.0f,
    val description: String? = null,
    val department: String? = null,
    val credits: Int = 0,
    val status: String = "PASS"
)

enum class CourseStatus(

)