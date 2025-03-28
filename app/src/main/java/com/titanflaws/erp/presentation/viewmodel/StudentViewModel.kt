package com.titanflaws.erp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titanflaws.erp.data.model.Student
import com.titanflaws.erp.data.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val studentRepository: StudentRepository
) : ViewModel() {

    // UI state for student data
    data class StudentUIState(
        val isLoading: Boolean = false,
        val students: List<Student> = emptyList(),
        val selectedStudent: Student? = null,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(StudentUIState())
    val uiState: StateFlow<StudentUIState> = _uiState

    // Load all students
    fun loadAllStudents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            studentRepository.getAllStudents()
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load students: ${e.message}"
                        )
                    }
                }
                .collect { students ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            students = students
                        )
                    }
                }
        }
    }

    // Load students by class and section
    fun loadStudentsByClass(classId: String, sectionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
//            studentRepository.getStudentsByClassSection(classId, sectionId)
//                .catch { e ->
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            errorMessage = "Failed to load students: ${e.message}"
//                        )
//                    }
//                }
//                .collect { students ->
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            students = students
//                        )
//                    }
//                }
        }
    }

    // Load a specific student by ID
    fun loadStudentById(studentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            studentRepository.getStudentById(studentId)
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load student: ${e.message}"
                        )
                    }
                }
                .collect { student ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            selectedStudent = student
                        )
                    }
                }
        }
    }

    // Sync student data with Firestore
    fun syncStudentData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                studentRepository.syncAllStudents()
                
                // Refresh data based on current state
                refreshData()
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to sync student data: ${e.message}"
                    )
                }
            }
        }
    }

    // Private helper method to refresh data based on current state
    private fun refreshData() {
        val state = _uiState.value
        
        when {
            state.selectedStudent != null -> {
                loadStudentById(state.selectedStudent.studentId)
            }
            state.students.isNotEmpty() -> {
                loadAllStudents()
            }
        }
    }
} 