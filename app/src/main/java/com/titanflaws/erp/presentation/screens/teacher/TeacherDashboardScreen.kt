package com.titanflaws.erp.presentation.screens.teacher

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.components.ExamStatusChip
import com.titanflaws.erp.presentation.components.common.ErrorMessage
import com.titanflaws.erp.presentation.screens.student.StatusChip
//import com.titanflaws.erp.presentation.viewmodel.TeacherDashboardViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dashboard screen for teacher users
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(
    onNavigateToCourses: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToExams: () -> Unit,
    onNavigateToCreateExam: () -> Unit,
    onNavigateToAssignments: () -> Unit,
    onNavigateToStudents: () -> Unit,
    onNavigateToTimetable: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
//    viewModel: TeacherDashboardViewModel = hiltViewModel()
) {
//    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    LaunchedEffect(Unit) {
//        viewModel.loadDashboardData()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teacher Dashboard") },
                actions = {
                    BadgedBox(
                        badge = {
//                            if (uiState.unreadNotificationsCount > 0) {
//                                Badge { Text(uiState.unreadNotificationsCount.toString()) }
//                            }
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
//            if (uiState.isLoading) {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//            } else if (uiState.error != null) {
//                ErrorMessage(
//                    message = uiState.error!!,
//                    onRetry = { viewModel.loadDashboardData() },
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            } else {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .verticalScroll(scrollState)
//                        .padding(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    // Welcome message with current date
//                    WelcomeHeader(
//                        teacherName = uiState.teacherName,
//                        department = uiState.department
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // Quick stats
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        StatCard(
//                            icon = Icons.Default.Class,
//                            title = "Classes Today",
//                            value = uiState.classesToday.toString(),
//                            subtitle = "${uiState.completedClasses} completed",
//                            color = MaterialTheme.colorScheme.primary,
//                            modifier = Modifier.weight(1f),
//                            onClick = onNavigateToTimetable
//                        )
//
//                        StatCard(
//                            icon = Icons.Default.Group,
//                            title = "Students",
//                            value = uiState.totalStudents.toString(),
//                            subtitle = "${uiState.attendingStudents} attending today",
//                            color = MaterialTheme.colorScheme.secondary,
//                            modifier = Modifier.weight(1f),
//                            onClick = onNavigateToStudents
//                        )
//                    }
//
//                    // Classes Today
//                    if (uiState.todaySchedule.isNotEmpty()) {
//                        TodayClassesCard(
//                            classes = uiState.todaySchedule,
//                            onViewAll = onNavigateToTimetable
//                        )
//                    }
//
//                    // Pending Tasks
//                    if (uiState.pendingTasks.isNotEmpty()) {
//                        PendingTasksCard(
//                            tasks = uiState.pendingTasks
//                        )
//                    }
//
//                    // Upcoming Exams
//                    UpcomingExamsCard(
//                        exams = uiState.upcomingExams,
//                        onViewAll = onNavigateToExams,
//                        onCreateNew = onNavigateToCreateExam
//                    )
//
//                    // Quick Actions
//                    Text(
//                        text = "Quick Actions",
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold
//                    )
//
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        QuickActionButton(
//                            icon = Icons.Default.Book,
//                            label = "Courses",
//                            onClick = onNavigateToCourses
//                        )
//
//                        QuickActionButton(
//                            icon = Icons.Default.HowToReg,
//                            label = "Attendance",
//                            onClick = onNavigateToAttendance
//                        )
//
//                        QuickActionButton(
//                            icon = Icons.Default.Quiz,
//                            label = "Exams",
//                            onClick = onNavigateToExams
//                        )
//
//                        QuickActionButton(
//                            icon = Icons.Default.Assignment,
//                            label = "Assignments",
//                            onClick = onNavigateToAssignments
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(16.dp))
//                }
//            }
        }
    }
}

@Composable
fun WelcomeHeader(
    teacherName: String,
    department: String
) {
    val currentDate = remember {
        SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Hello, $teacherName!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Department: $department",
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
fun StatCard(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TodayClassesCard(
    classes: List<ClassSchedule>,
    onViewAll: () -> Unit
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
                    text = "Today's Classes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onViewAll) {
                    Text("View Timetable")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            classes.take(3).forEach { classItem ->
                ClassScheduleItem(classItem = classItem)
                
                if (classes.take(3).last() != classItem) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
            
            if (classes.size > 3) {
                TextButton(
                    onClick = onViewAll,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("${classes.size - 3} more classes")
                }
            }
        }
    }
}

@Composable
fun ClassScheduleItem(classItem: ClassSchedule) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Time indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(70.dp)
        ) {
            Text(
                text = classItem.startTime,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = classItem.endTime,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Vertical divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(50.dp)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Class info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = classItem.className,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "Room: ${classItem.roomNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Grade: ${classItem.grade}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Status indicator
        StatusChip(status = classItem.status)
    }
}

//@Composable
//fun StatusChip(status: String) {
//    val (backgroundColor, textColor, text) = when (status) {
//        "completed" -> Triple(
//            MaterialTheme.colorScheme.primaryContainer,
//            MaterialTheme.colorScheme.onPrimaryContainer,
//            "COMPLETED"
//        )
//        "ongoing" -> Triple(
//            MaterialTheme.colorScheme.tertiaryContainer,
//            MaterialTheme.colorScheme.onTertiaryContainer,
//            "ONGOING"
//        )
//        "upcoming" -> Triple(
//            MaterialTheme.colorScheme.secondaryContainer,
//            MaterialTheme.colorScheme.onSecondaryContainer,
//            "UPCOMING"
//        )
//        else -> Triple(
//            MaterialTheme.colorScheme.surfaceVariant,
//            MaterialTheme.colorScheme.onSurfaceVariant,
//            status.uppercase()
//        )
//    }
//
//    Box(
//        modifier = Modifier
//            .clip(RoundedCornerShape(4.dp))
//            .background(backgroundColor)
//            .padding(horizontal = 8.dp, vertical = 4.dp)
//    ) {
//        Text(
//            text = text,
//            style = MaterialTheme.typography.labelSmall,
//            color = textColor
//        )
//    }
//}

@Composable
fun PendingTasksCard(tasks: List<TeacherTask>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Pending Tasks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            tasks.forEach { task ->
                TaskItem(task = task)
                
                if (tasks.last() != task) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: TeacherTask) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Task icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when (task.type) {
                        "attendance" -> MaterialTheme.colorScheme.primaryContainer
                        "grading" -> MaterialTheme.colorScheme.secondaryContainer
                        "report" -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (task.type) {
                    "attendance" -> Icons.Default.HowToReg
                    "grading" -> Icons.Default.Grading
                    "report" -> Icons.Default.InsertDriveFile
                    else -> Icons.Default.CheckCircle
                },
                contentDescription = null,
                tint = when (task.type) {
                    "attendance" -> MaterialTheme.colorScheme.onPrimaryContainer
                    "grading" -> MaterialTheme.colorScheme.onSecondaryContainer
                    "report" -> MaterialTheme.colorScheme.onTertiaryContainer
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Task details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Due date
        Text(
            text = task.dueDate,
            style = MaterialTheme.typography.bodySmall,
            color = if (task.isUrgent) MaterialTheme.colorScheme.error 
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun UpcomingExamsCard(
    exams: List<ExamInfo>,
    onViewAll: () -> Unit,
    onCreateNew: () -> Unit
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
                    text = "Upcoming Exams",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row {
                    TextButton(onClick = onViewAll) {
                        Text("View All")
                    }
                    
                    TextButton(onClick = onCreateNew) {
                        Text("Create New")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (exams.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No upcoming exams",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                exams.forEach { exam ->
                    ExamItem(exam = exam)
                    
                    if (exams.last() != exam) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ExamItem(exam: ExamInfo) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Exam info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = exam.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "Class: ${exam.className}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Date: ${exam.date}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Status indicator
        ExamStatusChip(status = exam.status)
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

data class ClassSchedule(
    val className: String,
    val grade: String,
    val roomNumber: String,
    val startTime: String,
    val endTime: String,
    val status: String // "completed", "ongoing", "upcoming"
)

data class TeacherTask(
    val id: String,
    val title: String,
    val description: String,
    val type: String, // "attendance", "grading", "report"
    val dueDate: String,
    val isUrgent: Boolean = false
)

data class ExamInfo(
    val id: String,
    val title: String,
    val className: String,
    val date: String,
    val status: String // "scheduled", "ongoing", "completed"
) 