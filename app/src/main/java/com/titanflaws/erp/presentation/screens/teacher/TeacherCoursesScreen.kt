package com.titanflaws.erp.presentation.screens.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.components.common.EmptyStateMessage
import com.titanflaws.erp.presentation.components.common.ErrorMessage
import com.titanflaws.erp.presentation.components.common.LoadingIndicator
import com.titanflaws.erp.presentation.viewmodel.CourseViewModel

/**
 * Screen for teachers to view and manage their assigned courses
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherCoursesScreen(
    onNavigateBack: () -> Unit,
    courseViewModel: CourseViewModel = hiltViewModel()
) {
    val uiState by courseViewModel.uiState.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }
    var filterBy by remember { mutableStateOf("All") }
    
    // Effect to load teacher courses when the screen is first displayed
    LaunchedEffect(key1 = true) {
        courseViewModel.loadTeacherCourses()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Courses") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
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
            // Filter chip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter: ",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                FilterChip(
                    selected = true,
                    onClick = { showFilterMenu = true },
                    label = { Text(filterBy) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
            
            // Error message
            uiState.errorMessage?.let { message ->
                ErrorMessage(
                    message = message,
                    onDismiss = {
                        // Clear error
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Loading state
            if (uiState.isLoading) {
                LoadingIndicator("Loading courses...")
            } else if (uiState.teacherCourses.isEmpty()) {
                // Empty state
                EmptyStateMessage(
                    message = "You are not assigned to any courses",
                    icon = Icons.Default.School
                )
            } else {
                // Course list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(uiState.teacherCourses) { course ->
                        TeacherCourseCard(
                            courseName = course.courseName,
                            courseCode = course.courseCode,
                            studentCount = course.enrolledStudents?.size ?: 0,
                            department = course.department ?: "General",
                            classNames = course.assignedClasses?.joinToString(", ") ?: "Not assigned",
                            onClick = {
                                // Navigate to course detail or management
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
    
    // Filter menu dropdown
    if (showFilterMenu) {
        val options = listOf("All", "Active", "Completed", "Upcoming")
        
        AlertDialog(
            onDismissRequest = { showFilterMenu = false },
            title = { Text("Filter Courses") },
            text = {
                Column {
                    options.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = filterBy == option,
                                onClick = {
                                    filterBy = option
                                    // Apply filter
                                    when (option) {
                                        "All" -> courseViewModel.loadTeacherCourses()
                                        "Active" -> courseViewModel.loadTeacherCoursesByStatus("active")
                                        "Completed" -> courseViewModel.loadTeacherCoursesByStatus("completed")
                                        "Upcoming" -> courseViewModel.loadTeacherCoursesByStatus("upcoming")
                                    }
                                    showFilterMenu = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(option)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterMenu = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Card to display a teacher's course
 */
@Composable
fun TeacherCourseCard(
    courseName: String,
    courseCode: String,
    studentCount: Int,
    department: String,
    classNames: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Course name and code
            Text(
                text = courseName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = courseCode,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    DetailRow(
                        icon = Icons.Default.Group,
                        label = "Students",
                        value = "$studentCount enrolled"
                    )
                    
                    DetailRow(
                        icon = Icons.Default.Business,
                        label = "Department",
                        value = department
                    )
                    
                    DetailRow(
                        icon = Icons.Default.Class,
                        label = "Classes",
                        value = classNames
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { /* View grades */ }
                ) {
                    Icon(Icons.Default.Grade, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Grades")
                }
                
                TextButton(
                    onClick = { /* View material */ }
                ) {
                    Icon(Icons.Default.Book, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Materials")
                }
                
                TextButton(
                    onClick = { /* View attendance */ }
                ) {
                    Icon(Icons.Default.Event, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Attendance")
                }
            }
        }
    }
}

/**
 * Detail row with icon
 */
@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 