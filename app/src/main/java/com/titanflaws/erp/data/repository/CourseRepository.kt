package com.titanflaws.erp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.titanflaws.erp.data.datasource.local.dao.CourseDao
import com.titanflaws.erp.data.model.Course
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

/**
 * Repository for managing Course data from both Firestore and local Room database
 */
class CourseRepository @Inject constructor(
    private val courseDao: CourseDao,
    private val firestore: FirebaseFirestore
) {
    private val coursesCollection = firestore.collection("courses")
    
    /**
     * Get a course by ID from local database
     */
    fun getCourseById(courseId: String): Flow<Course?> = courseDao.getCourseById(courseId)
    
    /**
     * Get all courses from local database
     */
    fun getAllCourses(): Flow<List<Course>> = courseDao.getAllCourses()
    
    /**
     * Get courses by department from local database
     */
    fun getCoursesByDepartment(department: String): Flow<List<Course>> = 
        courseDao.getCoursesByDepartment(department)
    
    /**
     * Get courses by teacher ID from local database
     */
    fun getCoursesByTeacher(teacherId: String): Flow<List<Course>> = 
        courseDao.getCoursesByTeacher(teacherId)
    
    /**
     * Get courses by class ID from local database
     */
    fun getCoursesByClass(classId: String): Flow<List<Course>> = 
        courseDao.getCoursesByClass(classId)
    
    /**
     * Get courses by status from local database
     */
    fun getCoursesByStatus(status: String): Flow<List<Course>> = 
        courseDao.getCoursesByStatus(status)
    
    /**
     * Create a new course in both Firestore and local database
     */
    suspend fun createCourse(course: Course) {
        // Generate a new ID if one isn't provided
        val courseId = if (course.courseId.isBlank()) UUID.randomUUID().toString() else course.courseId
        val newCourse = course.copy(courseId = courseId)
        
        // Add to Firestore
        coursesCollection.document(courseId).set(newCourse).await()
        
        // Add to local database
        courseDao.insertCourse(newCourse)
    }
    
    /**
     * Update an existing course in both Firestore and local database
     */
    suspend fun updateCourse(course: Course) {
        // Update in Firestore
        coursesCollection.document(course.courseId).set(course).await()
        
        // Update in local database
        courseDao.updateCourse(course)
    }
    
    /**
     * Delete a course by ID from both Firestore and local database
     */
    suspend fun deleteCourse(courseId: String) {
        // Delete from Firestore
        coursesCollection.document(courseId).delete().await()
        
        // Delete from local database
        courseDao.deleteCourseById(courseId)
    }
    
    /**
     * Fetch a specific course from Firestore and update local database
     */
    suspend fun syncCourse(courseId: String) {
        try {
            val courseDoc = coursesCollection.document(courseId).get().await()
            courseDoc.toObject(Course::class.java)?.let { course ->
                courseDao.insertCourse(course)
            }
        } catch (e: Exception) {
            // Handle errors
            throw e
        }
    }
    
    /**
     * Fetch all courses from Firestore and update local database
     */
    suspend fun syncAllCourses() {
        try {
            val coursesSnapshot = coursesCollection.get().await()
            val courses = coursesSnapshot.toObjects(Course::class.java)
            courseDao.insertCourses(courses)
        } catch (e: Exception) {
            // Handle errors
            throw e
        }
    }
    
    /**
     * Fetch courses by department from Firestore and update local database
     */
    suspend fun syncCoursesByDepartment(department: String) {
        try {
            val coursesSnapshot = coursesCollection
                .whereEqualTo("department", department)
                .get()
                .await()
            
            val courses = coursesSnapshot.toObjects(Course::class.java)
            courseDao.insertCourses(courses)
        } catch (e: Exception) {
            // Handle errors
            throw e
        }
    }
    
    /**
     * Fetch courses by teacher from Firestore and update local database
     */
    suspend fun syncCoursesByTeacher(teacherId: String) {
        try {
            val coursesSnapshot = coursesCollection
                .whereEqualTo("teacherId", teacherId)
                .get()
                .await()
            
            val courses = coursesSnapshot.toObjects(Course::class.java)
            courseDao.insertCourses(courses)
        } catch (e: Exception) {
            // Handle errors
            throw e
        }
    }
    
    /**
     * Fetch courses by class from Firestore and update local database
     */
    suspend fun syncCoursesByClass(classId: String) {
        try {
            val coursesSnapshot = coursesCollection
                .whereArrayContains("classIds", classId)
                .get()
                .await()
            
            val courses = coursesSnapshot.toObjects(Course::class.java)
            courseDao.insertCourses(courses)
        } catch (e: Exception) {
            // Handle errors
            throw e
        }
    }
    
    /**
     * Get total number of courses
     */
    suspend fun getCourseCount(): Int = courseDao.getCourseCount()
    
    /**
     * Get the latest courses
     */
    fun getLatestCourses(limit: Int): Flow<List<Course>> = courseDao.getLatestCourses(limit)
} 