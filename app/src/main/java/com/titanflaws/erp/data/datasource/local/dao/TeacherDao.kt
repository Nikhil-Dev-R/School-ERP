package com.titanflaws.erp.data.datasource.local.dao

import androidx.room.*
import com.titanflaws.erp.data.model.Teacher
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Teacher entity
 */
@Dao
interface TeacherDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: Teacher)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeachers(teachers: List<Teacher>)
    
    @Update
    suspend fun updateTeacher(teacher: Teacher)
    
    @Delete
    suspend fun deleteTeacher(teacher: Teacher)
    
    @Query("DELETE FROM teachers WHERE teacherId = :teacherId")
    suspend fun deleteTeacherById(teacherId: String)
    
    @Query("SELECT * FROM teachers WHERE teacherId = :teacherId")
    fun getTeacherById(teacherId: String): Flow<Teacher?>
    
    @Query("SELECT * FROM teachers WHERE userId = :userId")
    fun getTeacherByUserId(userId: String): Flow<Teacher?>
    
    @Query("SELECT * FROM teachers")
    fun getAllTeachers(): Flow<List<Teacher>>
    
    @Query("SELECT * FROM teachers WHERE department = :department")
    fun getTeachersByDepartment(department: String): Flow<List<Teacher>>
    
    @Query("SELECT * FROM teachers WHERE :classId IN (classesTaught)")
    fun getTeachersByClass(classId: String): Flow<List<Teacher>>
    
    @Query("SELECT * FROM teachers WHERE :courseId IN (subjectsTaught)")
    fun getTeachersByCourse(courseId: String): Flow<List<Teacher>>
    
    @Query("SELECT COUNT(*) FROM teachers")
    suspend fun getTeacherCount(): Int
    
    @Query("UPDATE teachers SET employeeId = :employeeId, joiningDate = :joiningDate, employmentType = :employmentType, salary = :salary WHERE teacherId = :teacherId")
    suspend fun updateEmploymentDetails(
        teacherId: String,
        employeeId: String,
        joiningDate: Long,
        employmentType: String,
        salary: Double
    )
    
    @Query("SELECT * FROM teachers WHERE isActive = 1")
    fun getActiveTeachers(): Flow<List<Teacher>>
    
    @Query("UPDATE teachers SET isActive = :isActive WHERE teacherId = :teacherId")
    suspend fun updateActiveStatus(teacherId: String, isActive: Boolean)
    
    @Query("SELECT * FROM teachers WHERE designation = :designation")
    fun getTeachersByDesignation(designation: String): Flow<List<Teacher>>
    
    @Query("SELECT * FROM teachers ORDER BY joiningDate DESC LIMIT :limit")
    fun getRecentTeachers(limit: Int): Flow<List<Teacher>>
} 