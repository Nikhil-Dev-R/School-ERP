package com.titanflaws.erp.presentation.screens.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.components.common.ErrorMessage
import com.titanflaws.erp.presentation.components.common.LoadingIndicator
import com.titanflaws.erp.presentation.components.common.EmptyStateMessage
import com.titanflaws.erp.presentation.viewmodel.CourseViewModel

/**
 * Screen for students to view their enrolled courses
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentCoursesScreen(
    onNavigateBack: () -> Unit,
    courseViewModel: CourseViewModel = hiltViewModel()
) {
    val uiState by courseViewModel.uiState.collectAsState()
    
    // Effect to load student courses when the screen is first displayed
    LaunchedEffect(key1 = true) {
        courseViewModel.loadStudentCourses()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Courses") },
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
            } else if (uiState.studentCourses.isEmpty()) {
                // Empty state
                EmptyStateMessage(
                    message = "You are not enrolled in any courses",
                    icon = Icons.Default.Book
                )
            } else {
                // Course list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(uiState.studentCourses) { course ->
                        CourseCard(
                            courseName = course.courseName,
                            courseCode = course.courseCode,
                            teacherName = course.teacher?.fullName ?: "Not assigned",
                            department = course.department ?: "General",
                            credits = course.credits ?: 0,
                            progress = course.progress ?: 0f
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

/**
 * Card to display a course item
 */
@Composable
fun CourseCard(
    courseName: String,
    courseCode: String,
    teacherName: String,
    department: String,
    credits: Int,
    progress: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
            
            // Teacher and details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    DetailItem(label = "Teacher", value = teacherName)
                    DetailItem(label = "Department", value = department)
                    DetailItem(label = "Credits", value = credits.toString())
                }
                
                // Progress indicator
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = when {
                    progress < 0.3f -> Color(0xFFE57373) // Red for low progress
                    progress < 0.7f -> Color(0xFFFFB74D) // Orange for medium progress
                    else -> Color(0xFF81C784) // Green for high progress
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

/**
 * Helper function for displaying a detail item
 */
@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
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