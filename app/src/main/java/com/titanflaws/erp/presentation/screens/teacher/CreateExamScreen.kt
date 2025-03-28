package com.titanflaws.erp.presentation.screens.teacher

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.components.common.DatePicker
import com.titanflaws.erp.presentation.components.common.ErrorMessage
import com.titanflaws.erp.presentation.components.common.SuccessMessage
import com.titanflaws.erp.presentation.viewmodel.ClassSectionViewModel
import com.titanflaws.erp.presentation.viewmodel.CourseViewModel
import com.titanflaws.erp.presentation.viewmodel.ExamViewModel
import java.util.*

/**
 * Screen for creating new exams
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExamScreen(
    onNavigateBack: () -> Unit,
    onSuccess: (examId: String) -> Unit,
    examViewModel: ExamViewModel = hiltViewModel(),
    classSectionViewModel: ClassSectionViewModel = hiltViewModel(),
    courseViewModel: CourseViewModel = hiltViewModel()
) {
    val examUiState by examViewModel.uiState.collectAsState()
    val classUiState by classSectionViewModel.uiState.collectAsState()
    val courseUiState by courseViewModel.uiState.collectAsState()
    
    val scrollState = rememberScrollState()
    
    // Form state
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var examType by remember { mutableStateOf("") }
    var selectedClassId by remember { mutableStateOf<String?>(null) }
    var selectedCourseId by remember { mutableStateOf<String?>(null) }
    var selectedSections by remember { mutableStateOf<List<String>>(emptyList()) }
    var startDate by remember { mutableStateOf(Date()) }
    var endDate by remember { mutableStateOf<Date?>(null) }
    var totalMarks by remember { mutableStateOf("100") }
    var passingMarks by remember { mutableStateOf("35") }
    var instructions by remember { mutableStateOf("") }
    var examDuration by remember { mutableStateOf("") }
    var gradingScheme by remember { mutableStateOf("") }
    
    // Dialog state
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showExamTypeDialog by remember { mutableStateOf(false) }
    var showClassDialog by remember { mutableStateOf(false) }
    var showCourseDialog by remember { mutableStateOf(false) }
    var showSectionsDialog by remember { mutableStateOf(false) }
    
    // Exam type options
    val examTypes = listOf("MIDTERM", "FINAL", "UNIT_TEST", "QUIZ", "PRACTICAL", "ASSIGNMENT")
    
    // Effect to load classes and courses when screen is first displayed
//    LaunchedEffect(key1 = true) {
//        classSectionViewModel.loadAllClasses()
//        courseViewModel.loadAllCourses()
//    }
//
//    // Check for success and navigate back
//    LaunchedEffect(examUiState.successMessage) {
//        if (examUiState.successMessage != null && examUiState.successMessage?.contains("created") == true) {
//            // Extract examId from state or generate a dummy one for this example
//            val examId = "exam_${UUID.randomUUID()}"
//            onSuccess(examId)
//        }
//    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Exam") },
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
                .padding(16.dp)
        ) {
            // Success message
//            examUiState.successMessage?.let { message ->
//                SuccessMessage(
//                    message = message,
//                    onDismiss = {
//                        // Clear success message
//                    }
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//            }
            
            // Error message
            examUiState.errorMessage?.let { message ->
                ErrorMessage(
                    message = message,
                    onDismiss = {
                        // Clear error message
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Exam Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Exam Title*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Exam Type
            OutlinedTextField(
                value = examType,
                onValueChange = { },
                label = { Text("Exam Type*") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showExamTypeDialog = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Type")
                    }
                },
                readOnly = true,
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Class Selection
            OutlinedTextField(
                value = selectedClassId ?: "",
                onValueChange = { },
                label = { Text("Class") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showClassDialog = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Class")
                    }
                },
                readOnly = true,
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subject/Course Selection
            OutlinedTextField(
                value = selectedCourseId ?: "",
                onValueChange = { },
                label = { Text("Subject/Course") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showCourseDialog = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Course")
                    }
                },
                readOnly = true,
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Date Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Start Date
                OutlinedTextField(
                    value = formatDate(startDate),
                    onValueChange = { },
                    label = { Text("Start Date*") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    },
                    readOnly = true,
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // End Date (Optional)
                OutlinedTextField(
                    value = endDate?.let { formatDate(it) } ?: "",
                    onValueChange = { },
                    label = { Text("End Date") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showEndDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    },
                    readOnly = true,
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Marks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Total Marks
                OutlinedTextField(
                    value = totalMarks,
                    onValueChange = { totalMarks = it },
                    label = { Text("Total Marks*") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Passing Marks
                OutlinedTextField(
                    value = passingMarks,
                    onValueChange = { passingMarks = it },
                    label = { Text("Passing Marks*") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Exam Duration
            OutlinedTextField(
                value = examDuration,
                onValueChange = { examDuration = it },
                label = { Text("Duration (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Instructions
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Grading Scheme
            OutlinedTextField(
                value = gradingScheme,
                onValueChange = { gradingScheme = it },
                label = { Text("Grading Scheme") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Create Button
            Button(
                onClick = {
                    if (selectedClassId != null && selectedCourseId != null && endDate != null) {
                        examViewModel.createExam(
                            title = title,
                            description = description.ifEmpty { null },
                            examType = examType,
                            classId = selectedClassId!!,
                            courseId = selectedCourseId!!,
                            startDate = startDate,
                            endDate = endDate!!,
                            totalMarks = totalMarks.toIntOrNull() ?: 100,
                            passingMarks = passingMarks.toIntOrNull() ?: 35,
                            availableSections = selectedSections.ifEmpty { null },
                            instructions = instructions.ifEmpty { null },
                            examDuration = examDuration.toIntOrNull(),
                            gradingScheme = gradingScheme.ifEmpty { null }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotEmpty() && examType.isNotEmpty() && !examUiState.isLoading
            ) {
                if (examUiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Create Exam")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    
    // Dialogs
    
    // Exam Type Selection Dialog
    if (showExamTypeDialog) {
        AlertDialog(
            onDismissRequest = { showExamTypeDialog = false },
            title = { Text("Select Exam Type") },
            text = {
                Column {
                    examTypes.forEach { type ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    examType = type
                                    showExamTypeDialog = false
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = examType == type,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = type)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showExamTypeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Class Selection Dialog
    if (showClassDialog) {
        AlertDialog(
            onDismissRequest = { showClassDialog = false },
            title = { Text("Select Class") },
            text = {
                Column {
//                    if (classUiState.classes.isEmpty()) {
//                        Text("No classes available")
//                    } else {
//                        classUiState.classes.forEach { classSection ->
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 8.dp)
//                                    .clickable {
//                                        selectedClassId = classSection.classId
//                                        showClassDialog = false
//
//                                        // If class has sections, show the sections dialog
//                                        if (classSection.sections.isNotEmpty()) {
//                                            showSectionsDialog = true
//                                        }
//                                    },
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                RadioButton(
//                                    selected = selectedClassId == classSection.classId,
//                                    onClick = null
//                                )
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Text(text = classSection.className)
//                            }
//                        }
//                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showClassDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Course Selection Dialog
    if (showCourseDialog) {
        AlertDialog(
            onDismissRequest = { showCourseDialog = false },
            title = { Text("Select Course") },
            text = {
                Column {
//                    if (courseUiState.courses.isEmpty()) {
//                        Text("No courses available")
//                    } else {
//                        courseUiState.courses.forEach { course ->
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 8.dp)
//                                    .clickable {
//                                        selectedCourseId = course.courseId
//                                        showCourseDialog = false
//                                    },
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                RadioButton(
//                                    selected = selectedCourseId == course.courseId,
//                                    onClick = null
//                                )
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Text(text = course.courseName)
//                            }
//                        }
//                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCourseDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Section Selection Dialog (multi-select)
    if (showSectionsDialog) {
        // Find the selected class
//        val selectedClass = classUiState.classes.find { it.classId == selectedClassId }
        
        AlertDialog(
            onDismissRequest = { showSectionsDialog = false },
            title = { Text("Select Sections") },
            text = {
                Column {
//                    if (selectedClass?.sections.isNullOrEmpty()) {
//                        Text("No sections available for this class")
//                    } else {
//                        selectedClass?.sections?.forEach { section ->
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 8.dp)
//                                    .clickable {
//                                        selectedSections = if (selectedSections.contains(section.sectionId)) {
//                                            selectedSections - section.sectionId
//                                        } else {
//                                            selectedSections + section.sectionId
//                                        }
//                                    },
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Checkbox(
//                                    checked = selectedSections.contains(section.sectionId),
//                                    onCheckedChange = null
//                                )
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Text(text = section.sectionName)
//                            }
//                        }
//                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSectionsDialog = false }) {
                    Text("Done")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    selectedSections = emptyList()
                    showSectionsDialog = false 
                }) {
                    Text("Clear All")
                }
            }
        )
    }
    
    // Date Pickers
    if (showStartDatePicker) {
        DatePicker(
            initialDate = startDate,
            onDateSelected = {
                startDate = it
                showStartDatePicker = false
                
                // If end date is before start date, update end date
                if (endDate != null && endDate!!.before(startDate)) {
                    endDate = startDate
                }
            },
            onDismiss = { showStartDatePicker = false }
        )
    }
    
    if (showEndDatePicker) {
        DatePicker(
            initialDate = endDate ?: startDate,
            onDateSelected = {
                // Ensure end date is not before start date
                endDate = if (it.before(startDate)) startDate else it
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}

/**
 * Helper function to format date
 */
private fun formatDate(date: Date): String {
    val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
    return dateFormat.format(date)
} 