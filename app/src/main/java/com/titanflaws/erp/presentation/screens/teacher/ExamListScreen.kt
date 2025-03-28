package com.titanflaws.erp.presentation.screens.teacher

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.data.model.Exam
import com.titanflaws.erp.presentation.components.ExamStatusChip
import com.titanflaws.erp.presentation.components.common.EmptyStateMessage
import com.titanflaws.erp.presentation.components.common.ErrorMessage
import com.titanflaws.erp.presentation.components.common.LoadingIndicator
import com.titanflaws.erp.presentation.components.common.SuccessMessage
import com.titanflaws.erp.presentation.viewmodel.ExamViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen for teachers to view and manage exams
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreateExam: () -> Unit,
    onNavigateToExamDetails: (String) -> Unit,
    examViewModel: ExamViewModel = hiltViewModel()
) {
    val uiState by examViewModel.uiState.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }
    var currentFilter by remember { mutableStateOf("All") }
    
    // Filter options
    val filterOptions = listOf("All", "Upcoming", "Completed", "Scheduled", "Ongoing", "Cancelled")
    
    // Effect to load exams when the screen is first displayed
    LaunchedEffect(key1 = true) {
        // This will call the appropriate method based on the user's role
        examViewModel.loadTeacherExams()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exams") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Filter menu
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    
                    // Sync data
                    IconButton(onClick = { examViewModel.syncExamData() }) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateExam,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Exam")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Filter chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Filter: ",
                    style = MaterialTheme.typography.titleSmall
                )
                
                FilterChip(
                    selected = true,
                    onClick = { showFilterMenu = true },
                    label = { Text(currentFilter) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
            
            // Success message
            uiState.successMessage?.let { message ->
                SuccessMessage(
                    message = message,
                    onDismiss = {
                        // Clear the success message
                    }
                )
            }
            
            // Error message
            uiState.errorMessage?.let { message ->
                ErrorMessage(
                    message = message,
                    onDismiss = {
                        // Clear the error message
                    }
                )
            }
            
            // Loading indicator
            if (uiState.isLoading) {
                LoadingIndicator()
            } else if (uiState.exams.isEmpty()) {
                // Empty state
                EmptyStateMessage(
                    message = "No exams found",
                    icon = Icons.Default.Assignment,
                    actionText = "Create Exam",
                    onAction = onNavigateToCreateExam
                )
            } else {
                // Exam list
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.exams) { exam ->
                        ExamItem(
                            exam = exam,
                            onClick = { onNavigateToExamDetails(exam.examId) }
                        )
                    }
                }
            }
        }
    }
    
    // Filter menu dropdown
    if (showFilterMenu) {
        AlertDialog(
            onDismissRequest = { showFilterMenu = false },
            title = { Text("Filter Exams") },
            text = {
                Column {
                    filterOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentFilter = option
                                    
                                    // Apply the filter
                                    when (option) {
                                        "All" -> examViewModel.loadTeacherExams()
                                        "Upcoming" -> examViewModel.loadUpcomingExams()
                                        "Scheduled" -> examViewModel.loadExamsByStatus("SCHEDULED")
                                        "Ongoing" -> examViewModel.loadExamsByStatus("ONGOING")
                                        "Completed" -> examViewModel.loadExamsByStatus("COMPLETED")
                                        "Cancelled" -> examViewModel.loadExamsByStatus("CANCELLED")
                                    }
                                    
                                    showFilterMenu = false
                                }
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentFilter == option,
                                onClick = null
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(text = option)
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
 * Component to display a single exam item in the list
 */
@Composable
fun ExamItem(
    exam: Exam,
    onClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Exam title and type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exam.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                // Status chip
                ExamStatusChip(status = exam.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Exam details
            Text(
                text = "Type: ${exam.examType}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Date: ${formatDate(exam.startDate)}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (exam.endDate != null) {
                Text(
                    text = "End Date: ${formatDate(exam.endDate)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Show class and course if available
            exam.classId?.let { classId ->
                Text(
                    text = "Class: $classId",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            exam.courseId?.let { courseId ->
                Text(
                    text = "Course: $courseId",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Expandable section
            ExpandButton(
                expanded = expanded,
                onClick = { expanded = !expanded }
            )
            
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Divider()
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Marks: ${exam.totalMarks} (Passing: ${exam.passingMarks})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    exam.description?.let { description ->
                        Text(
                            text = "Description: $description",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
//                    Text(
//                        text = "Results Published: ${if (exam.publishResults) "Yes" else "No"}",
//                        style = MaterialTheme.typography.bodyMedium
//                    )
                    
//                    exam.instructions?.let { instructions ->
//                        Text(
//                            text = "Instructions: $instructions",
//                            style = MaterialTheme.typography.bodySmall
//                        )
//                    }
//
//                    exam.examDuration?.let { duration ->
//                        Text(
//                            text = "Duration: $duration minutes",
//                            style = MaterialTheme.typography.bodyMedium
//                        )
//                    }
                }
            }
        }
    }
}

/**
 * Button to expand/collapse exam details
 */
@Composable
fun ExpandButton(expanded: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = if (expanded) "Show Less" else "Show More")
        Icon(
            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (expanded) "Collapse" else "Expand"
        )
    }
}

/**
 * Helper function to format date
 */
private fun formatDate(date: Date): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return dateFormat.format(date)
} 