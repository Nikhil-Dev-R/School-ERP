package com.titanflaws.erp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.titanflaws.erp.data.datasource.local.dao.TeacherDao
import com.titanflaws.erp.data.model.Teacher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

/**
 * Repository for managing Teacher data from both Firestore and local Room database
 */
class TeacherRepository @Inject constructor(
    private val teacherDao: TeacherDao,
    private val firestore: FirebaseFirestore
) {
    private val teachersCollection = firestore.collection("teachers")
    
    /**
     * Get a teacher by ID from local database
     */
    fun getTeacherById(teacherId: String): Flow<Teacher?> = teacherDao.getTeacherById(teacherId)
    
    /**
     * Get a teacher by user ID from local database
     */
    fun getTeacherByUserId(userId: String): Flow<Teacher?> = teacherDao.getTeacherByUserId(userId)
    
    /**
     * Get all teachers from local database
     */
    fun getAllTeachers(): Flow<List<Teacher>> = teacherDao.getAllTeachers()
    
    /**
     * Get teachers by department from local database
     */
    fun getTeachersByDepartment(department: String): Flow<List<Teacher>> = 
        teacherDao.getTeachersByDepartment(department)
    
    /**
     * Get teachers for a class from local database
     */
    fun getTeachersByClass(classId: String): Flow<List<Teacher>> = 
        teacherDao.getTeachersByClass(classId)
    
    /**
     * Get teachers for a course from local database
     */
    fun getTeachersByCourse(courseId: String): Flow<List<Teacher>> = 
        teacherDao.getTeachersByCourse(courseId)
    
    /**
     * Create a new teacher in both Firestore and local database
     */
    suspend fun createTeacher(teacher: Teacher) {
        // Generate a new ID if one isn't provided
        val teacherId = if (teacher.teacherId.isBlank()) UUID.randomUUID().toString() else teacher.teacherId
        val newTeacher = teacher.copy(teacherId = teacherId)
        
        // Add to Firestore
        teachersCollection.document(teacherId).set(newTeacher).await()
        
        // Add to local database
        teacherDao.insertTeacher(newTeacher)
    }
    
    /**
     * Update an existing teacher in both Firestore and local database
     */
    suspend fun updateTeacher(teacher: Teacher) {
        // Update in Firestore
        teachersCollection.document(teacher.teacherId).set(teacher).await()
        
        // Update in local database
        teacherDao.updateTeacher(teacher)
    }
    
    /**
     * Delete a teacher by ID from both Firestore and local database
     */
    suspend fun deleteTeacher(teacherId: String) {
        // Delete from Firestore
        teachersCollection.document(teacherId).delete().await()
        
        // Delete from local database
        teacherDao.deleteTeacherById(teacherId)
    }
    
    /**
     * Fetch a specific teacher from Firestore and update local database
     */
    suspend fun syncTeacher(teacherId: String) {
        try {
            val teacherDoc = teachersCollection.document(teacherId).get().await()
            teacherDoc.toObject(Teacher::class.java)?.let { teacher ->
                teacherDao.insertTeacher(teacher)
            }
        } catch (e: Exception) {
            // Handle errors
            throw e
        }
    }
    
    /**
     * Fetch all teachers from Firestore and update local database
     */
    suspend fun syncAllTeachers() {
        try {
            val teachersSnapshot = teachersCollection.get().await()
            val teachers = teachersSnapshot.toObjects(Teacher::class.java)
            teacherDao.insertTeachers(teachers)
        } catch (e: Exception) {
            // Handle errors
            throw e
        }
    }
    
    /**
     * Fetch teachers by department from Firestore and update local database
     */
    suspend fun syncTeachersByDepartment(department: String) {
        try {
            val teachersSnapshot = teachersCollection
                .whereEqualTo("department", department)
                .get()
                .await()
            
            val teachers = teachersSnapshot.toObjects(Teacher::class.java)
            teacherDao.insertTeachers(teachers)
        } catch (e: Exception) {
            // Handle errors
            throw e
        }
    }
    
    /**
     * Get total number of teachers
     */
    suspend fun getTeacherCount(): Int = teacherDao.getTeacherCount()
    
    /**
     * Assign a class to a teacher
     */
    suspend fun assignClass(teacherId: String, classId: String) {
        // Get current teacher
        val teacher = teacherDao.getTeacherById(teacherId).firstOrNull() ?: return
        
        // Add class to taught classes
        val classes = teacher.classesTaught?.toMutableList() ?: mutableListOf()
        if (!classes.contains(classId)) {
            classes.add(classId)
            
            // Update in Firestore
            teachersCollection.document(teacherId)
                .update("classesTaught", classes)
                .await()
            
            // Update in local database
            val updatedTeacher = teacher.copy(classesTaught = classes)
            teacherDao.updateTeacher(updatedTeacher)
        }
    }
    
    /**
     * Unassign a class from a teacher
     */
    suspend fun unassignClass(teacherId: String, classId: String) {
        // Get current teacher
        val teacher = teacherDao.getTeacherById(teacherId).firstOrNull() ?: return
        
        // Remove class from taught classes
        val classes = teacher.classesTaught?.toMutableList() ?: mutableListOf()
        if (classes.contains(classId)) {
            classes.remove(classId)
            
            // Update in Firestore
            teachersCollection.document(teacherId)
                .update("classesTaught", classes)
                .await()
            
            // Update in local database
            val updatedTeacher = teacher.copy(classesTaught = classes)
            teacherDao.updateTeacher(updatedTeacher)
        }
    }
    
    /**
     * Assign a course to a teacher
     */
    suspend fun assignCourse(teacherId: String, courseId: String) {
        // Get current teacher
        val teacher = teacherDao.getTeacherById(teacherId).firstOrNull() ?: return
        
        // Add course to taught subjects
        val subjects = teacher.subjectsTaught?.toMutableList() ?: mutableListOf()
        if (!subjects.contains(courseId)) {
            subjects.add(courseId)
            
            // Update in Firestore
            teachersCollection.document(teacherId)
                .update("subjectsTaught", subjects)
                .await()
            
            // Update in local database
            val updatedTeacher = teacher.copy(subjectsTaught = subjects)
            teacherDao.updateTeacher(updatedTeacher)
        }
    }
    
    /**
     * Unassign a course from a teacher
     */
    suspend fun unassignCourse(teacherId: String, courseId: String) {
        // Get current teacher
        val teacher = teacherDao.getTeacherById(teacherId).firstOrNull() ?: return
        
        // Remove course from taught subjects
        val subjects = teacher.subjectsTaught?.toMutableList() ?: mutableListOf()
        if (subjects.contains(courseId)) {
            subjects.remove(courseId)
            
            // Update in Firestore
            teachersCollection.document(teacherId)
                .update("subjectsTaught", subjects)
                .await()
            
            // Update in local database
            val updatedTeacher = teacher.copy(subjectsTaught = subjects)
            teacherDao.updateTeacher(updatedTeacher)
        }
    }
    
    /**
     * Update teacher employment details
     */
    suspend fun updateEmploymentDetails(
        teacherId: String,
        employeeId: String,
        joiningDate: Long,
        employmentType: String,
        salary: Double
    ) {
        // Update in Firestore
        val updates = mapOf(
            "employeeId" to employeeId,
            "joiningDate" to joiningDate,
            "employmentType" to employmentType,
            "salary" to salary
        )
        
        teachersCollection.document(teacherId).update(updates).await()
        
        // Update in local database
        teacherDao.updateEmploymentDetails(
            teacherId = teacherId,
            employeeId = employeeId,
            joiningDate = joiningDate,
            employmentType = employmentType,
            salary = salary
        )
    }
} 