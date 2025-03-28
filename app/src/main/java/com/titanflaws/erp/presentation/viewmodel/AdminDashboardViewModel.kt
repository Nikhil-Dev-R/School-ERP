package com.titanflaws.erp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Data class representing an activity in the admin dashboard
 */
data class AdminActivity(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val type: String
)

/**
 * Data class representing the UI state for admin dashboard
 */
data class AdminDashboardUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // School stats
    val totalStudents: Int = 0,
    val totalTeachers: Int = 0,
    val totalClasses: Int = 0,
    val totalCourses: Int = 0,
    
    // Attendance stats
    val studentsPresent: Int = 0,
    val teachersPresent: Int = 0,
    val studentAttendancePercent: Float = 0f,
    val teacherAttendancePercent: Float = 0f,
    
    // Recent activities
    val recentActivities: List<AdminActivity> = emptyList()
)

/**
 * ViewModel for managing admin dashboard data
 */
@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    // TODO: Inject repositories for users, classes, courses, and attendance
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()
    
    /**
     * Load dashboard data for admin
     */
    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // TODO: Replace with actual repository calls
                delay(1000) // Simulate network delay
                
                // Fetch simulated data
                val studentCount = 452
                val teacherCount = 38
                val classCount = 24
                val courseCount = 48
                
                val studentsPresent = 410
                val teachersPresent = 36
                
                // Calculate attendance percentages
                val studentAttendancePercent = if (studentCount > 0) 
                    (studentsPresent.toFloat() / studentCount) * 100 else 0f
                val teacherAttendancePercent = if (teacherCount > 0) 
                    (teachersPresent.toFloat() / teacherCount) * 100 else 0f
                
                // Sample recent activities
                val activities = generateSampleActivities()
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalStudents = studentCount,
                        totalTeachers = teacherCount,
                        totalClasses = classCount,
                        totalCourses = courseCount,
                        studentsPresent = studentsPresent,
                        teachersPresent = teachersPresent,
                        studentAttendancePercent = studentAttendancePercent,
                        teacherAttendancePercent = teacherAttendancePercent,
                        recentActivities = activities
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Failed to load dashboard data: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Generate sample activities for demo
     */
    private fun generateSampleActivities(): List<AdminActivity> {
        return listOf(
            AdminActivity(
                id = "1",
                title = "New Student Added",
                description = "Amol Patel was added to Class 10A",
                timestamp = "10 min ago",
                type = "user"
            ),
            AdminActivity(
                id = "2",
                title = "Exam Schedule Published",
                description = "Mid-term examination schedule was published",
                timestamp = "1 hour ago",
                type = "exam"
            ),
            AdminActivity(
                id = "3",
                title = "New Announcement",
                description = "Principal's address scheduled for tomorrow",
                timestamp = "2 hours ago",
                type = "announcement"
            ),
            AdminActivity(
                id = "4",
                title = "Class Added",
                description = "New class 12C was added with 30 students",
                timestamp = "Yesterday",
                type = "class"
            ),
            AdminActivity(
                id = "5",
                title = "Course Updated",
                description = "Advanced Physics course syllabus was updated",
                timestamp = "Yesterday",
                type = "course"
            )
        )
    }
} 