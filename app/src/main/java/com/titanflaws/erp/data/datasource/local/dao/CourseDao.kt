package com.titanflaws.erp.data.datasource.local.dao

import androidx.room.*
import com.titanflaws.erp.data.model.Course
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Course entity
 */
@Dao
interface CourseDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<Course>)
    
    @Update
    suspend fun updateCourse(course: Course)
    
    @Delete
    suspend fun deleteCourse(course: Course)
    
    @Query("DELETE FROM courses WHERE courseId = :courseId")
    suspend fun deleteCourseById(courseId: String)
    
    @Query("SELECT * FROM courses WHERE courseId = :courseId")
    fun getCourseById(courseId: String): Flow<Course?>
    
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<Course>>
    
    @Query("SELECT * FROM courses WHERE department = :department")
    fun getCoursesByDepartment(department: String): Flow<List<Course>>
    
    @Query("SELECT * FROM courses WHERE teacherId = :teacherId")
    fun getCoursesByTeacher(teacherId: String): Flow<List<Course>>
    
    @Query("SELECT * FROM courses WHERE :classId IN (classIds)")
    fun getCoursesByClass(classId: String): Flow<List<Course>>
    
    @Query("SELECT * FROM courses WHERE status = :status")
    fun getCoursesByStatus(status: String): Flow<List<Course>>
    
    @Query("SELECT COUNT(*) FROM courses")
    suspend fun getCourseCount(): Int
    
    @Query("SELECT * FROM courses ORDER BY courseId DESC LIMIT :limit")
    fun getLatestCourses(limit: Int): Flow<List<Course>>
    
    @Query("SELECT * FROM courses WHERE name LIKE '%' || :searchQuery || '%' OR code LIKE '%' || :searchQuery || '%'")
    fun searchCourses(searchQuery: String): Flow<List<Course>>
} 