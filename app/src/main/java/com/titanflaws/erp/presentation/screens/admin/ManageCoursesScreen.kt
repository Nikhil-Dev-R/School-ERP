package com.titanflaws.erp.presentation.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.components.ConfirmationDialog
import com.titanflaws.erp.presentation.components.CourseCard
import com.titanflaws.erp.presentation.components.EmptyStateMessage
import com.titanflaws.erp.presentation.components.LoadingIndicator
import com.titanflaws.erp.presentation.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCoursesScreen(
    onNavigateBack: () -> Unit,
    courseViewModel: CourseViewModel = hiltViewModel()
) {
    val uiState by courseViewModel.uiState.collectAsState()
    
    var showAddCourseDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedCourseId by remember { mutableStateOf("") }
    
    // Form fields
    var courseName by remember { mutableStateOf("") }
    var courseCode by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var credits by remember { mutableStateOf("3") }
    var teacherId by remember { mutableStateOf<String?>(null) }
    var classIds by remember { mutableStateOf<List<String>?>(null) }
    var status by remember { mutableStateOf("active") }
    
    LaunchedEffect(key1 = true) {
        courseViewModel.loadAllCourses()
    }
    
    // Reset form when dialog is shown/hidden
    LaunchedEffect(key1 = showAddCourseDialog, key2 = showEditDialog) {
        if (!showAddCourseDialog && !showEditDialog) {
            courseName = ""
            courseCode = ""
            description = ""
            department = ""
            credits = "3"
            teacherId = null
            classIds = null
            status = "active"
        } else if (showEditDialog && selectedCourseId.isNotEmpty()) {
            // Fill form with selected course data for editing
            val selectedCourse = uiState.allCourses.find { it.courseId == selectedCourseId }
            selectedCourse?.let {
                courseName = it.courseName
                courseCode = it.courseCode
                description = it.description ?: ""
                department = it.department ?: ""
                credits = it.credits?.toString() ?: "3"
                teacherId = it.teacher?.userId
                classIds = it.assignedClasses
                status = it.status
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Courses") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddCourseDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Course"
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
            if (uiState.isLoading) {
                LoadingIndicator()
            } else if (uiState.allCourses.isEmpty()) {
                EmptyStateMessage(
                    message = "No courses found",
                    icon = Icons.Default.Book
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.allCourses) { course ->
                        CourseCard(
                            courseWithDetails = course,
                            onEditClick = {
                                selectedCourseId = course.courseId
                                showEditDialog = true
                            },
                            onDeleteClick = {
                                selectedCourseId = course.courseId
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
            
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
    
    // Add Course Dialog
    if (showAddCourseDialog) {
        AlertDialog(
            onDismissRequest = { showAddCourseDialog = false },
            title = { Text("Add New Course") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = courseName,
                        onValueChange = { courseName = it },
                        label = { Text("Course Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = courseCode,
                        onValueChange = { courseCode = it },
                        label = { Text("Course Code") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("Department") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = credits,
                        onValueChange = { credits = it },
                        label = { Text("Credits") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Status dropdown
                    var statusExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
//                                .menuAnchor()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            listOf("active", "completed", "upcoming").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.capitalize()) },
                                    onClick = {
                                        status = option
                                        statusExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    // Note: Teacher selection and class assignment would ideally use dropdown menus
                    // or a more complex UI component, simplified here for brevity
                    Text(
                        "Note: Teacher and class assignments can be done after course creation",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        courseViewModel.createCourse(
                            name = courseName,
                            code = courseCode,
                            description = description,
                            department = department,
                            credits = credits.toIntOrNull() ?: 3,
                            teacherId = null,
                            classIds = null
                        )
                        showAddCourseDialog = false
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddCourseDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Edit Course Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Course") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = courseName,
                        onValueChange = { courseName = it },
                        label = { Text("Course Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = courseCode,
                        onValueChange = { courseCode = it },
                        label = { Text("Course Code") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("Department") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = credits,
                        onValueChange = { credits = it },
                        label = { Text("Credits") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Status dropdown
                    var statusExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
//                                .menuAnchor()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            listOf("active", "completed", "upcoming").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.capitalize()) },
                                    onClick = {
                                        status = option
                                        statusExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        courseViewModel.updateCourse(
                            courseId = selectedCourseId,
                            name = courseName,
                            code = courseCode,
                            description = description,
                            department = department,
                            credits = credits.toIntOrNull() ?: 3,
                            teacherId = teacherId,
                            classIds = classIds,
                            status = status
                        )
                        showEditDialog = false
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        ConfirmationDialog(
            title = "Delete Course",
            message = "Are you sure you want to delete this course? This action cannot be undone and may affect students enrolled in this course.",
            onConfirm = {
                // Implement delete course functionality
                // courseViewModel.deleteCourse(selectedCourseId)
                showDeleteDialog = false
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
}

private fun String.capitalize(): String {
    return if (isNotEmpty()) this[0].uppercase() + substring(1) else this
}
