package com.titanflaws.erp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titanflaws.erp.data.model.Attendance
import com.titanflaws.erp.data.repository.AttendanceRepository
import com.titanflaws.erp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

// UI state for attendance management
data class AttendanceUIState(
    val isLoading: Boolean = false,
    val attendanceList: List<Attendance> = emptyList(),
    val selectedDate: Date = Date(),
    val selectedClassId: String? = null,
    val selectedStudentId: String? = null,
    val selectedCourseId: String? = null,
    val statisticsData: Map<String, Int> = emptyMap(),
    val errorMessage: String? = null
)

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AttendanceUIState())
    val uiState: StateFlow<AttendanceUIState> = _uiState.asStateFlow()

    // Keep track of current user ID for filtering
    private var currentUserId: String? = null

    init {
        currentUserId = userRepository.getCurrentUserId()
    }

    // Load attendance for the current user
    fun loadUserAttendance() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            currentUserId?.let { userId ->
                attendanceRepository.getAttendanceByUserId(userId)
                    .catch { e ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                errorMessage = "Failed to load attendance: ${e.message}"
                            ) 
                        }
                    }
                    .collect { attendanceList ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                attendanceList = attendanceList
                            )
                        }
                    }
            } ?: run {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "User not authenticated"
                    ) 
                }
            }
        }
    }

    // Load attendance for a specific date
    fun loadAttendanceByDate(date: Date) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedDate = date, errorMessage = null) }
            
            attendanceRepository.getAttendanceByDate(date)
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Failed to load attendance: ${e.message}"
                        ) 
                    }
                }
                .collect { attendanceList ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            attendanceList = attendanceList
                        )
                    }
                }
        }
    }

    // Load attendance for a specific class and date
    fun loadClassAttendance(classId: String, date: Date) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    selectedClassId = classId, 
                    selectedDate = date,
                    errorMessage = null
                ) 
            }
            
            attendanceRepository.getAttendanceByClassAndDate(classId, date)
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Failed to load class attendance: ${e.message}"
                        ) 
                    }
                }
                .collect { attendanceList ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            attendanceList = attendanceList
                        )
                    }
                }
        }
    }

    // Load attendance for a specific course/subject
    fun loadCourseAttendance(courseId: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    selectedCourseId = courseId,
                    errorMessage = null
                ) 
            }
            
            attendanceRepository.getAttendanceByCourse(courseId)
                .catch { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Failed to load course attendance: ${e.message}"
                        ) 
                    }
                }
                .collect { attendanceList ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            attendanceList = attendanceList
                        )
                    }
                }
        }
    }

    // Mark attendance for a student
    fun markAttendance(
        userId: String,
        userType: String,
        date: Date,
        status: String,
        courseId: String?,
        classId: String?,
        sectionId: String?,
        reason: String?,
        remarks: String?
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                attendanceRepository.markAttendance(
                    userId, userType, date, status, courseId, classId, sectionId, reason, remarks
                )
                
                // Refresh attendance list based on current filters
                refreshAttendanceList()
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Failed to mark attendance: ${e.message}"
                    ) 
                }
            }
        }
    }

    // Update attendance status
    fun updateAttendanceStatus(attendanceId: String, status: String, reason: String?, remarks: String?) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                attendanceRepository.updateAttendanceStatus(attendanceId, status, reason, remarks)
                
                // Refresh attendance list based on current filters
                refreshAttendanceList()
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Failed to update attendance status: ${e.message}"
                    ) 
                }
            }
        }
    }

    // Get attendance statistics for a user
    fun loadAttendanceStatistics(userId: String, startDate: Date, endDate: Date) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                val statistics = attendanceRepository.getAttendanceStatistics(userId, startDate, endDate)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        statisticsData = statistics
                    )
                }
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Failed to load attendance statistics: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun loadStudentAttendance(studentId: String, month: Int) {

    }

    // Sync attendance data with Firestore
    fun syncAttendanceData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                attendanceRepository.syncAttendance()
                
                // Refresh attendance list based on current filters
                refreshAttendanceList()
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Failed to sync attendance data: ${e.message}"
                    ) 
                }
            }
        }
    }

    // Private helper method to refresh attendance list based on current filters
    private fun refreshAttendanceList() {
        val state = _uiState.value
        
        when {
            state.selectedClassId != null && state.selectedDate != null -> {
                loadClassAttendance(state.selectedClassId, state.selectedDate)
            }
            state.selectedCourseId != null -> {
                loadCourseAttendance(state.selectedCourseId)
            }
            state.selectedDate != null -> {
                loadAttendanceByDate(state.selectedDate)
            }
            currentUserId != null -> {
                loadUserAttendance()
            }
        }
    }
} 