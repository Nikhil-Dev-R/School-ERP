package com.titanflaws.erp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titanflaws.erp.data.model.Exam
import com.titanflaws.erp.data.model.ExamResult
import com.titanflaws.erp.data.repository.ExamRepository
import com.titanflaws.erp.data.repository.UserRepository
import com.titanflaws.erp.domain.repository.ExamResultRepository
import com.titanflaws.erp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Data class that represents the state of the exam view
 */
data class ExamUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val selectedExam: Exam? = null,
    val selectedResult: ExamResult? = null,
    val studentResults: List<ExamResult> = emptyList(),
    val exams: List<Exam> = emptyList()
)

/**
 * ViewModel for managing exam data and exam results
 */
@HiltViewModel
class ExamViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    private val userRepository: UserRepository,
    private val examResultRepository: ExamResultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamUiState())
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()

    // Keep track of current user ID and role for permissions
    private var currentUserId: String? = null
    private var userRole: UserRole? = null

    init {
        currentUserId = userRepository.getCurrentUserId()
        
        // Load user role
        viewModelScope.launch {
            userRepository.getCurrentUserId()?.let { id ->
                userRepository.getUserById(id).collectLatest { user ->
                    user?.let {
                        userRole = UserRole.fromString(it.role)
                        when (userRole) {
                            is UserRole.Teacher -> loadTeacherExams()
                            is UserRole.Student -> loadStudentExams()
                            is UserRole.Admin -> loadAllExams()
                            is UserRole.Parent -> { /* Will be implemented separately in ParentViewModel */ }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    // Load all exams
    fun loadAllExams() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            examRepository.getAllExams()
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Failed to load exams: ${e.message}"
                        ) 
                    }
                }
                .collect { examsList ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            exams = examsList
                        )
                    }
                }
        }
    }

    // Load exams created by the current teacher
    fun loadTeacherExams() {
        viewModelScope.launch {
            currentUserId?.let { teacherId ->
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                examRepository.getExamsByTeacher(teacherId)
                    .catch { e ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                errorMessage = "Failed to load exams: ${e.message}"
                            ) 
                        }
                    }
                    .collect { examsList ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                exams = examsList
                            )
                        }
                    }
            } ?: run {
                _uiState.update { 
                    it.copy(errorMessage = "User not authenticated") 
                }
            }
        }
    }

    // Load exams for the current student
    fun loadStudentExams() {
        viewModelScope.launch {
            // This is just a placeholder. In a real app, you would fetch the student's class
            // and then load exams for that class
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // For now, just load all exams (this would be filtered by student's class in a real app)
            examRepository.getAllExams()
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Failed to load exams: ${e.message}"
                        ) 
                    }
                }
                .collect { examsList ->
                    // Filter exams that are published or have published results for students
                    val filteredExams = examsList.filter { it.status != "CANCELLED" }
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            exams = filteredExams
                        )
                    }
                }
        }
    }

    // Load exams by class
    fun loadExamsByClass(classId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            examRepository.getExamsByClass(classId)
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Failed to load exams: ${e.message}"
                        ) 
                    }
                }
                .collect { examsList ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            exams = examsList
                        )
                    }
                }
        }
    }

    // Load exams by course/subject
    fun loadExamsByCourse(courseId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            examRepository.getExamsByCourse(courseId)
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Failed to load exams: ${e.message}"
                        ) 
                    }
                }
                .collect { examsList ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            exams = examsList
                        )
                    }
                }
        }
    }

    // Load upcoming exams
    fun loadUpcomingExams() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            examRepository.getUpcomingExams()
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Failed to load upcoming exams: ${e.message}"
                        ) 
                    }
                }
                .collect { examsList ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            exams = examsList
                        )
                    }
                }
        }
    }

    // Load exams by status
    fun loadExamsByStatus(status: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            examRepository.getExamsByStatus(status)
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Failed to load exams: ${e.message}"
                        ) 
                    }
                }
                .collect { examsList ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            exams = examsList
                        )
                    }
                }
        }
    }

    /**
     * Loads a specific exam by ID
     */
    fun loadExam(examId: String) {
        viewModelScope.launch {
            var result: Exam? = null
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            examRepository.getExamById(examId).collectLatest {
                result = it
            }
//            when ( result ) {
//                is Resource.Success -> {
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            selectedExam = result.data,
//                            errorMessage = null
//                        )
//                    }
//                }
//                is Resource.Error -> {
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            errorMessage = result.message ?: "Failed to load exam"
//                        )
//                    }
//                }
//                is Resource.Loading -> {
//                    // Already handled above
//                }
//            }
        }
    }

    // Create a new exam
    fun createExam(
        title: String,
        description: String?,
        examType: String,
        classId: String,
        courseId: String,
        startDate: Date,
        endDate: Date,
        totalMarks: Int,
        passingMarks: Int,
        availableSections: List<String>?,
        instructions: String?,
        examDuration: Int?,
        gradingScheme: String?
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                
                val examId = examRepository.createExam(
                    title = title,
                    description = description,
                    examType = examType,
                    classId = classId,
                    courseId = courseId,
                    startDate = startDate,
                    endDate = endDate,
                    totalMarks = totalMarks,
                    passingMarks = passingMarks,
                    availableSections = availableSections,
                    instructions = instructions,
                    examDuration = examDuration,
                    gradingScheme = gradingScheme
                )
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        successMessage = "Exam created successfully"
                    )
                }
                
                // Refresh the list of exams
                if (userRole == UserRole.Teacher) {
                    loadTeacherExams()
                } else {
                    loadAllExams()
                }
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to create exam: ${e.message}"
                    ) 
                }
            }
        }
    }

    // Update exam status
    fun updateExamStatus(examId: String, status: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null,) }
                
                examRepository.updateExamStatus(examId, status)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        successMessage = "Exam status updated successfully"
                    )
                }
                
                // Refresh the selected exam
                loadExam(examId)
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to update exam status: ${e.message}"
                    ) 
                }
            }
        }
    }

    // Delete an exam
    fun deleteExam(examId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                
                examRepository.deleteExam(examId)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        selectedExam = null,
                        successMessage = "Exam deleted successfully"
                    )
                }
                
                // Refresh the list of exams
                if (userRole == UserRole.Teacher) {
                    loadTeacherExams()
                } else {
                    loadAllExams()
                }
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to delete exam: ${e.message}"
                    ) 
                }
            }
        }
    }

    // Publish exam results
    fun publishExamResults(examId: String, publish: Boolean) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                
                examRepository.publishExamResults(examId, publish)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        successMessage = if (publish) "Results published successfully" else "Results unpublished"
                    )
                }
                
                // Refresh the selected exam
                loadExam(examId)
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to update results publish status: ${e.message}"
                    ) 
                }
            }
        }
    }

    /**
     * Loads all results for a specific exam
     */
    fun loadResultsForExam(examId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = examResultRepository.getResultsForExam(examId)) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            studentResults = result.data ?: emptyList(),
                            errorMessage = null
                        ) 
                    }
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = result.message ?: "Failed to load exam results"
                        ) 
                    }
                }
                is Resource.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    /**
     * Loads all exams for a specific student
     */
    fun loadStudentExams(studentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

//            when (val result = examRepository.getExamsForStudent(studentId)) {
//                is Resource.Success -> {
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            exams = result.data ?: emptyList(),
//                            errorMessage = null
//                        )
//                    }
//                }
//                is Resource.Error -> {
//                    _uiState.update {
//                        it.copy(
//                            isLoading = false,
//                            errorMessage = result.message ?: "Failed to load student exams"
//                        )
//                    }
//                }
//                is Resource.Loading -> {
//                    // Already handled above
//                }
//            }
        }
    }

    /**
     * Loads a student's result for a specific exam
     */
    fun loadStudentResultForExam(examId: String, studentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = examResultRepository.getStudentExamResult(examId, studentId)) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            selectedResult = result.data,
                            errorMessage = null
                        ) 
                    }
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = result.message ?: "Failed to load exam result"
                        ) 
                    }
                }
                is Resource.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    // Save a student's exam result
    fun saveExamResult(
        examId: String,
        studentId: String,
        marksObtained: Float,
        totalMarks: Int,
        remarks: String?,
        courseName: String?
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                
                examRepository.saveExamResult(
                    examId = examId,
                    studentId = studentId,
                    marksObtained = marksObtained,
                    totalMarks = totalMarks,
                    remarks = remarks,
                    courseName = courseName
                )
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        successMessage = "Result saved successfully"
                    )
                }
                
                // Refresh the results list
                loadResultsForExam(examId)
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to save result: ${e.message}"
                    ) 
                }
            }
        }
    }

    // Update an exam result
    fun updateExamResult(
        resultId: String,
        marks: Float,
        totalMarks: Int,
        remarks: String?
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                
                examRepository.updateResultEvaluation(resultId, marks, totalMarks, remarks)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        successMessage = "Result updated successfully"
                    )
                }
                
                // Refresh the selected result and results list
                val examId = _uiState.value.selectedExam?.examId
                if (examId != null) {
                    loadResultsForExam(examId)
                }
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to update result: ${e.message}"
                    ) 
                }
            }
        }
    }

    // Verify an exam result
    fun verifyExamResult(resultId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                
                examRepository.verifyResult(resultId)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        successMessage = "Result verified successfully"
                    )
                }
                
                // Refresh the selected result
                val examId = _uiState.value.selectedExam?.examId
                if (examId != null) {
                    loadResultsForExam(examId)
                }
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to verify result: ${e.message}"
                    ) 
                }
            }
        }
    }

    // Load exam statistics
    fun loadExamStatistics(examId: String) {
        viewModelScope.launch {
            try {
                val statistics = examRepository.getExamStatistics(examId)
                
//                _uiState.update {
//                    it.copy(examStatistics = statistics)
//                }
                
            } catch (e: Exception) {
                // Just log the error, don't update UI state since this is a secondary operation
                println("Failed to load exam statistics: ${e.message}")
            }
        }
    }

    // Sync exam data with Firestore
    fun syncExamData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                
                examRepository.syncAllExams()
                examRepository.syncExamResults()
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        successMessage = "Exam data synchronized successfully"
                    )
                }
                
                // Refresh the list of exams based on role
                when (userRole) {
                    is UserRole.Teacher-> loadTeacherExams()
                    is UserRole.Student -> loadStudentExams()
                    else -> loadAllExams()
                }
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to sync exam data: ${e.message}"
                    ) 
                }
            }
        }
    }

    /**
     * Clear any error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
} 