package com.titanflaws.erp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titanflaws.erp.presentation.screens.student.Assignment
import com.titanflaws.erp.presentation.screens.student.DisplayCourse
import com.titanflaws.erp.presentation.screens.student.ScheduleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Data class representing the UI state for student dashboard
 */
data class StudentDashboardUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // Student info
    val studentName: String = "",
    val className: String = "",
    
    // Stats
    val attendancePercentage: Float = 0f,
    val upcomingExamsCount: Int = 0,
    val unreadNotificationsCount: Int = 0,
    
    // Schedule
    val todaySchedule: List<ScheduleItem> = emptyList(),
    
    // Assignments
    val pendingAssignments: List<Assignment> = emptyList(),
    
    // Courses
    val enrolledCours: List<DisplayCourse> = emptyList()
)

/**
 * ViewModel for managing student dashboard data
 */
@HiltViewModel
class StudentDashboardViewModel @Inject constructor(
    // TODO: Inject repositories for student, attendance, exams, courses, etc.
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StudentDashboardUiState())
    val uiState: StateFlow<StudentDashboardUiState> = _uiState.asStateFlow()
    
    /**
     * Load dashboard data for student
     */
    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // TODO: Replace with actual repository calls
                delay(1000) // Simulate network delay
                
                // Fetch student data
                val studentName = "Rahul Sharma"
                val className = "10th A"
                
                // Fetch student stats
                val attendancePercentage = 85.5f
                val upcomingExamsCount = 2
                val unreadNotificationsCount = 3
                
                // Fetch today's schedule
                val todaySchedule = generateSampleSchedule()
                
                // Fetch pending assignments
                val pendingAssignments = generateSampleAssignments()
                
                // Fetch enrolled courses
                val enrolledCourses = generateSampleCourses()
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        studentName = studentName,
                        className = className,
                        attendancePercentage = attendancePercentage,
                        upcomingExamsCount = upcomingExamsCount,
                        unreadNotificationsCount = unreadNotificationsCount,
                        todaySchedule = todaySchedule,
                        pendingAssignments = pendingAssignments,
                        enrolledCours = enrolledCourses
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
     * Generate sample schedule for demo
     */
    private fun generateSampleSchedule(): List<ScheduleItem> {
        return listOf(
            ScheduleItem(
                subjectName = "Mathematics",
                teacherName = "Mrs. Gupta",
                roomNumber = "101",
                startTime = "8:30 AM",
                endTime = "9:30 AM",
                isCurrentClass = false
            ),
            ScheduleItem(
                subjectName = "Science",
                teacherName = "Mr. Sharma",
                roomNumber = "Lab 2",
                startTime = "9:40 AM",
                endTime = "10:40 AM",
                isCurrentClass = true
            ),
            ScheduleItem(
                subjectName = "English",
                teacherName = "Mrs. Patel",
                roomNumber = "202",
                startTime = "11:00 AM",
                endTime = "12:00 PM",
                isCurrentClass = false
            ),
            ScheduleItem(
                subjectName = "Computer Science",
                teacherName = "Mr. Kumar",
                roomNumber = "Lab 1",
                startTime = "1:00 PM",
                endTime = "2:00 PM",
                isCurrentClass = false
            )
        )
    }
    
    /**
     * Generate sample assignments for demo
     */
    private fun generateSampleAssignments(): List<Assignment> {
        return listOf(
            Assignment(
                id = "1",
                title = "Algebra Problem Set",
                subjectCode = "MA",
                dueDate = "Tomorrow",
                isUrgent = true
            ),
            Assignment(
                id = "2",
                title = "Science Lab Report",
                subjectCode = "SC",
                dueDate = "In 3 days",
                isUrgent = false
            ),
            Assignment(
                id = "3",
                title = "English Essay",
                subjectCode = "EN",
                dueDate = "Next week",
                isUrgent = false
            ),
            Assignment(
                id = "4",
                title = "History Research Paper",
                subjectCode = "HI",
                dueDate = "In 5 days",
                isUrgent = false
            )
        )
    }
    
    /**
     * Generate sample courses for demo
     */
    private fun generateSampleCourses(): List<DisplayCourse> {
        return listOf(
            DisplayCourse(
                id = "1",
                name = "Mathematics",
                code = "MA101",
                teacherName = "Mrs. Gupta",
                progress = 65f
            ),
            DisplayCourse(
                id = "2",
                name = "Science",
                code = "SC102",
                teacherName = "Mr. Sharma",
                progress = 78f
            ),
            DisplayCourse(
                id = "3",
                name = "English Language",
                code = "EN103",
                teacherName = "Mrs. Patel",
                progress = 92f
            ),
            DisplayCourse(
                id = "4",
                name = "Computer Science",
                code = "CS104",
                teacherName = "Mr. Kumar",
                progress = 45f
            ),
            DisplayCourse(
                id = "5",
                name = "History",
                code = "HI105",
                teacherName = "Mr. Singh",
                progress = 60f
            )
        )
    }
}