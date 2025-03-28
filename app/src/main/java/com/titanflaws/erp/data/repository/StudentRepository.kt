package com.titanflaws.erp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.titanflaws.erp.data.datasource.local.dao.StudentDao
import com.titanflaws.erp.data.model.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

/**
 * Repository for managing Student data from both Firestore and local Room database
 */
class StudentRepository @Inject constructor(
    private val studentDao: StudentDao,
    private val firestore: FirebaseFirestore
) {
    private val studentsCollection = firestore.collection("students")
    
    /**
     * Get a student by ID from local database
     */
    fun getStudentById(studentId: String): Flow<Student?> = studentDao.getStudentById(studentId)
    
    /**
     * Get a student by user ID from local database
     */
    fun getStudentByUserId(userId: String): Flow<Student?> = studentDao.getStudentByUserId(userId)
    
    /**
     * Get all students from local database
     */
    fun getAllStudents(): Flow<List<Student>> = studentDao.getAllStudents()
    
    /**
     * Get students by class ID from local database
     */
    suspend fun getStudentsByClass(classId: String): Flow<List<Student>> =
        studentDao.getStudentsByClass(classId)
    
    /**
     * Get students by parent ID from local database
     */
    fun getStudentsByParent(parentId: String): Flow<List<Student>> = 
        studentDao.getStudentsByParentId(parentId)
    
    /**
     * Get students enrolled in a course from local database
     */
    suspend fun getStudentsByCourse(courseId: String): List<String> {
        val students = studentDao.getAllStudents().firstOrNull() ?: return emptyList()
        return students
            .filter { it.subjects?.contains(courseId) == true }
            .map { it.studentId }
    }
    
    /**
     * Create a new student in both Firestore and local database
     */
    suspend fun createStudent(student: Student) {
        // Generate a new ID if one isn't provided
        val studentId = if (student.studentId.isBlank()) UUID.randomUUID().toString() else student.studentId
        val newStudent = student.copy(studentId = studentId)
        
        // Add to Firestore
        studentsCollection.document(studentId).set(newStudent).await()
        
        // Add to local database
        studentDao.insertStudent(newStudent)
    }
    
    /**
     * Update an existing student in both Firestore and local database
     */
    suspend fun updateStudent(student: Student) {
        // Update in Firestore
        studentsCollection.document(student.studentId).set(student).await()
        
        // Update in local database
        studentDao.updateStudent(student)
    }
    
    /**
     * Delete a student by ID from both Firestore and local database
     */
    suspend fun deleteStudent(studentId: String) {
        // Delete from Firestore
        studentsCollection.document(studentId).delete().await()
        
        // Delete from local database
        studentDao.deleteStudentById(studentId)
    }
    
    /**
     * Fetch a specific student from Firestore and update local database
     */
    suspend fun syncStudent(studentId: String) {
        try {
            val studentDoc = studentsCollection.document(studentId).get().await()
            studentDoc.toObject(Student::class.java)?.let { student ->
                studentDao.insertStudent(student)
            }
        } catch (e: Exception) {
            // Handle errors
            throw e
        }
    }
    
    /**
     * Fetch all students from Firestore and update local database
     */
    suspend fun syncAllStudents() {
        try {
            val studentsSnapshot = studentsCollection.get().await()
            val students = studentsSnapshot.toObjects(Student::class.java)
            studentDao.insertStudents(students)
        } catch (e: Exception) {
            // Handle errors
            throw e
        }
    }
    
    /**
     * Fetch students by class from Firestore and update local database
     */
    suspend fun syncStudentsByClass(classId: String) {
        try {
            val studentsSnapshot = studentsCollection
                .whereEqualTo("classId", classId)
                .get()
                .await()
            
            val students = studentsSnapshot.toObjects(Student::class.java)
            studentDao.insertStudents(students)
        } catch (e: Exception) {
            // Handle errors
            throw e
        }
    }
    
    /**
     * Fetch students by parent from Firestore and update local database
     */
    suspend fun syncStudentsByParent(parentId: String) {
        try {
            val studentsSnapshot = studentsCollection
                .whereArrayContains("parentIds", parentId)
                .get()
                .await()
            
            val students = studentsSnapshot.toObjects(Student::class.java)
            studentDao.insertStudents(students)
        } catch (e: Exception) {
            // Handle errors
            throw e
        }
    }
    
    /**
     * Get total number of students
     */
    suspend fun getStudentCount(): Int = studentDao.getStudentCount()
    
    /**
     * Update student attendance percentage
     */
    suspend fun updateAttendancePercentage(studentId: String, attendancePercentage: Float) {
        // Update in Firestore
        studentsCollection.document(studentId).update("attendancePercentage", attendancePercentage).await()
        
        // Update in local database
        studentDao.updateAttendancePercentage(studentId, attendancePercentage)
    }
    
    /**
     * Update student course progress
     */
    suspend fun updateCourseProgress(studentId: String, courseId: String, progress: Float) {
        // Get current student
        val student = studentDao.getStudentById(studentId).firstOrNull() ?: return
        
        // Update progress map
        val progressMap = student.courseProgress?.toMutableMap() ?: mutableMapOf()
        progressMap[courseId] = progress
        
        // Update in Firestore
        studentsCollection.document(studentId).update("courseProgress", progressMap).await()
        
        // Update in local database
//        studentDao.updateCourseProgress(studentId, progressMap)
    }
    
    /**
     * Enroll student in a course
     */
    suspend fun enrollInCourse(studentId: String, courseId: String) {
        // Get current student
        val student = studentDao.getStudentById(studentId).firstOrNull() ?: return
        
        // Add course to subjects
        val subjects = student.subjects?.toMutableList() ?: mutableListOf()
        if (!subjects.contains(courseId)) {
            subjects.add(courseId)
            
            // Initialize course progress
            val progressMap = student.courseProgress?.toMutableMap() ?: mutableMapOf()
            progressMap[courseId] = 0f
            
            // Update in Firestore
            studentsCollection.document(studentId)
                .update(
                    mapOf(
                        "subjects" to subjects,
                        "courseProgress" to progressMap
                    )
                )
                .await()
            
            // Update in local database
            val updatedStudent = student.copy(
                subjects = subjects,
                courseProgress = progressMap
            )
            studentDao.updateStudent(updatedStudent)
        }
    }
    
    /**
     * Unenroll student from a course
     */
    suspend fun unenrollFromCourse(studentId: String, courseId: String) {
        // Get current student
        val student = studentDao.getStudentById(studentId).firstOrNull() ?: return
        
        // Remove course from subjects
        val subjects = student.subjects?.toMutableList() ?: mutableListOf()
        if (subjects.contains(courseId)) {
            subjects.remove(courseId)
            
            // Remove course progress
            val progressMap = student.courseProgress?.toMutableMap() ?: mutableMapOf()
            progressMap.remove(courseId)
            
            // Update in Firestore
            studentsCollection.document(studentId)
                .update(
                    mapOf(
                        "subjects" to subjects,
                        "courseProgress" to progressMap
                    )
                )
                .await()
            
            // Update in local database
            val updatedStudent = student.copy(
                subjects = subjects,
                courseProgress = progressMap
            )
            studentDao.updateStudent(updatedStudent)
        }
    }
} 