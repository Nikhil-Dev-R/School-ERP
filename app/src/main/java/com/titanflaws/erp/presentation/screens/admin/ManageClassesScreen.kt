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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.components.ClassSectionCard
import com.titanflaws.erp.presentation.components.ConfirmationDialog
import com.titanflaws.erp.presentation.components.EmptyStateMessage
import com.titanflaws.erp.presentation.components.LoadingIndicator
import com.titanflaws.erp.presentation.viewmodel.ClassSectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageClassesScreen(
    onNavigateBack: () -> Unit,
    classSectionViewModel: ClassSectionViewModel = hiltViewModel()
) {
    val uiState by classSectionViewModel.uiState.collectAsState()
    
    var showAddClassDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedClassId by remember { mutableStateOf("") }
    
    // Form fields
    var className by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("") }
//    var gradeLevel by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("30") }
    
    LaunchedEffect(key1 = true) {
        classSectionViewModel.loadAllClassSections()
    }
    
    // Reset form when dialog is shown/hidden
    LaunchedEffect(key1 = showAddClassDialog, key2 = showEditDialog) {
        if (!showAddClassDialog && !showEditDialog) {
            className = ""
            section = ""
//            gradeLevel = ""
            capacity = "30"
        } else if (showEditDialog && selectedClassId.isNotEmpty()) {
            // Fill form with selected class data for editing
            val selectedClass = uiState.classSections.find { it.classSectionId == selectedClassId }
            selectedClass?.let {
                className = it.className
                section = it.sectionName
//                gradeLevel = it.gradeLevel.toString()
                capacity = it.capacity.toString()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Classes & Sections") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddClassDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Class"
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
            } else if (uiState.classSections.isEmpty()) {
                EmptyStateMessage(
                    message = "No classes found",
                    icon = Icons.Default.School
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.classSections) { classSection ->
                        ClassSectionCard(
                            classSection = classSection,
                            onEditClick = {
                                selectedClassId = classSection.classSectionId
                                showEditDialog = true
                            },
                            onDeleteClick = {
                                selectedClassId = classSection.classSectionId
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
    
    // Add Class Dialog
    if (showAddClassDialog) {
        AlertDialog(
            onDismissRequest = { showAddClassDialog = false },
            title = { Text("Add New Class") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = className,
                        onValueChange = { className = it },
                        label = { Text("Class Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = section,
                        onValueChange = { section = it },
                        label = { Text("Section") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
//                    OutlinedTextField(
//                        value = gradeLevel,
//                        onValueChange = { gradeLevel = it },
//                        label = { Text("Grade Level") },
//                        modifier = Modifier.fillMaxWidth(),
//                        singleLine = true
//                    )
                    
                    OutlinedTextField(
                        value = capacity,
                        onValueChange = { capacity = it },
                        label = { Text("Capacity") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        classSectionViewModel.createClassSection(
                            className = className,
                            section = section,
//                            gradeLevel = gradeLevel.toIntOrNull() ?: 0,
                            capacity = capacity.toIntOrNull() ?: 30
                        )
                        showAddClassDialog = false
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddClassDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Edit Class Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Class") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = className,
                        onValueChange = { className = it },
                        label = { Text("Class Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = section,
                        onValueChange = { section = it },
                        label = { Text("Section") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = capacity,
                        onValueChange = { capacity = it },
                        label = { Text("Capacity") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        classSectionViewModel.updateClassSection(
                            classId = selectedClassId,
                            className = className,
                            section = section,
//                            gradeLevel = gradeLevel.toIntOrNull() ?: 0,
                            capacity = capacity.toIntOrNull() ?: 30
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
            title = "Delete Class",
            message = "Are you sure you want to delete this class? This action cannot be undone.",
            onConfirm = {
                classSectionViewModel.deleteClassSection(selectedClassId)
                showDeleteDialog = false
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
} 