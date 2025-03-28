package com.titanflaws.erp.data.datasource.local.dao

import androidx.room.*
import com.titanflaws.erp.data.model.Student
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Student entity
 */
@Dao
interface StudentDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)
    
    @Update
    suspend fun updateStudent(student: Student)
    
    @Delete
    suspend fun deleteStudent(student: Student)
    
    @Query("DELETE FROM students WHERE studentId = :studentId")
    suspend fun deleteStudentById(studentId: String)
    
    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<Student>>
    
    @Query("SELECT * FROM students WHERE studentId = :studentId")
    fun getStudentById(studentId: String): Flow<Student?>
    
    @Query("SELECT * FROM students WHERE userId = :userId")
    fun getStudentByUserId(userId: String): Flow<Student?>
    
    @Query("SELECT * FROM students WHERE classId = :classId AND sectionId = :sectionId")
    fun getStudentsByClassSection(classId: String, sectionId: String): Flow<List<Student>>
    
    @Query("SELECT * FROM students WHERE parentIds LIKE '%' || :parentId || '%'")
    fun getStudentsByParentId(parentId: String): Flow<List<Student>>
    
    @Query("UPDATE students SET firstName = :firstName, lastName = :lastName, currentAddress = :currentAddress, permanentAddress = :permanentAddress, emergencyContact = :emergencyContact WHERE studentId = :studentId")
    suspend fun updateStudentProfile(
        studentId: String, 
        firstName: String, 
        lastName: String, 
        currentAddress: String, 
        permanentAddress: String, 
        emergencyContact: String
    )
    
    @Query("UPDATE students SET classId = :classId, sectionId = :sectionId WHERE studentId = :studentId")
    suspend fun updateStudentClass(studentId: String, classId: String, sectionId: String)
    
    @Query("UPDATE students SET isActive = :isActive WHERE studentId = :studentId")
    suspend fun updateStudentActiveStatus(studentId: String, isActive: Boolean)
    
    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int
    
    @Query("SELECT COUNT(*) FROM students WHERE classId = :classId")
    suspend fun getStudentCountByClass(classId: String): Int
    
    @Query("SELECT * FROM students WHERE rollNumber = :rollNumber AND classId = :classId")
    suspend fun getStudentByRollNumber(rollNumber: Int, classId: String): Student?

    @Query("SELECT COUNT(*) FROM students WHERE sectionId = :sectionId")
    suspend fun getStudentCountByClassSection(sectionId: String): Int

    @Query("SELECT * FROM students WHERE classId = :classId")
    fun getStudentsByClass(classId: String): Flow<List<Student>>

    @Query("UPDATE students SET attendancePercentage = :attendancePercentage WHERE studentId = :studentId")
    suspend fun updateAttendancePercentage(studentId: String, attendancePercentage: Float)
} 