package com.titanflaws.erp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titanflaws.erp.data.model.Course
import com.titanflaws.erp.data.model.Teacher
import com.titanflaws.erp.data.repository.CourseRepository
import com.titanflaws.erp.data.repository.StudentRepository
import com.titanflaws.erp.data.repository.TeacherRepository
import com.titanflaws.erp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Data class representing the UI state for courses
 */
data class CourseUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val studentCourses: List<CourseWithDetails> = emptyList(),
    val teacherCourses: List<CourseWithDetails> = emptyList(),
    val allCourses: List<CourseWithDetails> = emptyList()
)

/**
 * Data class containing course details with additional information
 */
data class CourseWithDetails(
    val courseId: String,
    val courseName: String,
    val courseCode: String,
    val description: String? = null,
    val department: String? = null,
    val credits: Int? = null,
    val teacher: TeacherInfo? = null,
    val enrolledStudents: List<String>? = null,
    val assignedClasses: List<String>? = null,
    val status: String = "active",
    val progress: Float? = null
)

/**
 * Teacher information for displaying in course details
 */
data class TeacherInfo(
    val userId: String,
    val fullName: String,
    val email: String? = null,
    val profilePic: String? = null
)

/**
 * ViewModel for managing course data
 */
@HiltViewModel
class CourseViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseUiState())
    val uiState: StateFlow<CourseUiState> = _uiState.asStateFlow()

    /**
     * Load courses for a student user
     */
    fun loadStudentCourses() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                val currentUserId = userRepository.getCurrentUserId()
                if (currentUserId == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "User not authenticated"
                        )
                    }
                    return@launch
                }
                
                // Get student information
                val student = studentRepository.getStudentByUserId(currentUserId).firstOrNull()
                if (student == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Student information not found"
                        )
                    }
                    return@launch
                }
                
                // Get enrolled courses
                val enrolledCourses = mutableListOf<CourseWithDetails>()
                student.subjects?.forEach { courseId ->
                    val course = courseRepository.getCourseById(courseId).firstOrNull()
                    if (course != null) {
                        // Get teacher info
                        val teacherInfo = course.teacherId?.let { teacherId ->
                            val teacher = teacherRepository.getTeacherById(teacherId).firstOrNull()
                            val user = userRepository.getUserById(teacherId).firstOrNull()
                            if (teacher != null && user != null) {
                                TeacherInfo(
                                    userId = teacherId,
                                    fullName = user.fullName,
                                    email = user.email,
                                    profilePic = user.profilePicUrl
                                )
                            } else null
                        }
                        
                        // Calculate progress
                        val progress = student.courseProgress?.get(courseId) ?: 0f
                        
                        enrolledCourses.add(
                            CourseWithDetails(
                                courseId = course.courseId,
                                courseName = course.name,
                                courseCode = course.code,
                                description = course.description,
                                department = course.department,
                                credits = course.credits,
                                teacher = teacherInfo,
                                status = course.status ?: "active",
                                progress = progress
                            )
                        )
                    }
                }
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        studentCourses = enrolledCourses
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load courses: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Load courses assigned to a teacher
     */
    fun loadTeacherCourses() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                val currentUserId = userRepository.getCurrentUserId()
                if (currentUserId == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "User not authenticated"
                        )
                    }
                    return@launch
                }
                
                // Get teacher information
                val teacher = teacherRepository.getTeacherByUserId(currentUserId).firstOrNull()
                if (teacher == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Teacher information not found"
                        )
                    }
                    return@launch
                }
                
                // Get courses taught by this teacher
                val teacherCourses = mutableListOf<CourseWithDetails>()
                teacher.subjectsTaught?.forEach { courseId ->
                    val course = courseRepository.getCourseById(courseId).firstOrNull()
                    if (course != null) {
                        // Get enrolled students count
                        val studentIds = studentRepository.getStudentsByCourse(courseId)
                        
                        teacherCourses.add(
                            CourseWithDetails(
                                courseId = course.courseId,
                                courseName = course.name,
                                courseCode = course.code,
                                description = course.description,
                                department = course.department,
                                credits = course.credits,
                                enrolledStudents = studentIds,
                                assignedClasses = course.classIds,
                                status = course.status ?: "active"
                            )
                        )
                    }
                }
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        teacherCourses = teacherCourses
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load courses: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Load all courses (for admin)
     */
    fun loadAllCourses() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                val allCourses = mutableListOf<CourseWithDetails>()
                val courses = courseRepository.getAllCourses().firstOrNull() ?: emptyList()
                
                courses.forEach { course ->
                    // Get teacher info
                    val teacherInfo = course.teacherId?.let { teacherId ->
                        val user = userRepository.getUserById(teacherId).firstOrNull()
                        if (user != null) {
                            TeacherInfo(
                                userId = teacherId,
                                fullName = user.fullName,
                                email = user.email,
                                profilePic = user.profilePicUrl
                            )
                        } else null
                    }
                    
                    // Get enrolled students
                    val studentIds = studentRepository.getStudentsByCourse(course.courseId)
                    
                    allCourses.add(
                        CourseWithDetails(
                            courseId = course.courseId,
                            courseName = course.name,
                            courseCode = course.code,
                            description = course.description,
                            department = course.department,
                            credits = course.credits,
                            teacher = teacherInfo,
                            enrolledStudents = studentIds,
                            assignedClasses = course.classIds,
                            status = course.status ?: "active"
                        )
                    )
                }
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        allCourses = allCourses
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load courses: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Filter teacher courses by status
     */
    fun loadTeacherCoursesByStatus(status: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                val currentUserId = userRepository.getCurrentUserId()
                if (currentUserId == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "User not authenticated"
                        )
                    }
                    return@launch
                }
                
                // Get teacher information
                val teacher = teacherRepository.getTeacherByUserId(currentUserId).firstOrNull()
                if (teacher == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Teacher information not found"
                        )
                    }
                    return@launch
                }
                
                // Get courses taught by this teacher
                val teacherCourses = mutableListOf<CourseWithDetails>()
                teacher.subjectsTaught?.forEach { courseId ->
                    val course = courseRepository.getCourseById(courseId).firstOrNull()
                    if (course != null && (status == "all" || course.status == status)) {
                        // Get enrolled students count
                        val studentIds = studentRepository.getStudentsByCourse(courseId)
                        
                        teacherCourses.add(
                            CourseWithDetails(
                                courseId = course.courseId,
                                courseName = course.name,
                                courseCode = course.code,
                                description = course.description,
                                department = course.department,
                                credits = course.credits,
                                enrolledStudents = studentIds,
                                assignedClasses = course.classIds,
                                status = course.status ?: "active"
                            )
                        )
                    }
                }
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        teacherCourses = teacherCourses
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load courses: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Create a new course (admin function)
     */
    fun createCourse(
        name: String,
        code: String,
        description: String,
        department: String,
        credits: Int,
        teacherId: String?,
        classIds: List<String>?
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                val course = Course(
                    courseId = "",  // Will be assigned by repository
                    name = name,
                    code = code,
                    description = description,
                    department = department,
                    credits = credits,
                    teacherId = teacherId,
                    classIds = classIds,
                    status = "active"
                )
                
                courseRepository.createCourse(course)
                
                // Refresh courses
                loadAllCourses()
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to create course: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Update an existing course
     */
    fun updateCourse(
        courseId: String,
        name: String,
        code: String,
        description: String,
        department: String,
        credits: Int,
        teacherId: String?,
        classIds: List<String>?,
        status: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                val course = Course(
                    courseId = courseId,
                    name = name,
                    code = code,
                    description = description,
                    department = department,
                    credits = credits,
                    teacherId = teacherId,
                    classIds = classIds,
                    status = status
                )
                
                courseRepository.updateCourse(course)
                
                // Refresh courses based on user role
                val currentUser = userRepository.getCurrentUserId() ?: return@launch
                val currentUserObj = userRepository.getUserById(currentUser).firstOrNull()
                
                when (currentUserObj?.role) {
                    "admin" -> loadAllCourses()
                    "teacher" -> loadTeacherCourses()
                    "student" -> loadStudentCourses()
                }
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to update course: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Assign a course to a teacher
     */
    fun assignCourseToTeacher(courseId: String, teacherId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                // Update course with teacher
                val course = courseRepository.getCourseById(courseId).firstOrNull()
                if (course != null) {
                    val updatedCourse = course.copy(teacherId = teacherId)
                    courseRepository.updateCourse(updatedCourse)
                    
                    // Update teacher's subjects
                    val teacher = teacherRepository.getTeacherById(teacherId).firstOrNull()
                    if (teacher != null) {
                        val subjects = teacher.subjectsTaught?.toMutableList() ?: mutableListOf()
                        if (!subjects.contains(courseId)) {
                            subjects.add(courseId)
                            val updatedTeacher = teacher.copy(subjectsTaught = subjects)
                            teacherRepository.updateTeacher(updatedTeacher)
                        }
                    }
                }
                
                // Refresh courses
                loadAllCourses()
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to assign course: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Enroll students in a course
     */
    fun enrollStudentsInCourse(courseId: String, studentIds: List<String>) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                studentIds.forEach { studentId ->
                    val student = studentRepository.getStudentById(studentId).firstOrNull()
                    if (student != null) {
                        val subjects = student.subjects?.toMutableList() ?: mutableListOf()
                        if (!subjects.contains(courseId)) {
                            subjects.add(courseId)
                            val updatedStudent = student.copy(subjects = subjects)
                            studentRepository.updateStudent(updatedStudent)
                        }
                    }
                }
                
                // Refresh courses
                val currentUser = userRepository.getCurrentUserId() ?: return@launch
                val currentUserObj = userRepository.getUserById(currentUser).firstOrNull()
                
                when (currentUserObj?.role) {
                    "admin" -> loadAllCourses()
                    "teacher" -> loadTeacherCourses()
                }
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to enroll students: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
} 