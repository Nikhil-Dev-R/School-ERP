package com.titanflaws.erp.data.datasource.local.dao

import androidx.room.*
import com.titanflaws.erp.data.model.Exam
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Data Access Object for the Exam entity
 */
@Dao
interface ExamDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExams(exams: List<Exam>)
    
    @Update
    suspend fun updateExam(exam: Exam)
    
    @Delete
    suspend fun deleteExam(exam: Exam)
    
    @Query("DELETE FROM exams WHERE examId = :examId")
    suspend fun deleteExamById(examId: String)
    
    @Query("SELECT * FROM exams ORDER BY startDate DESC")
    fun getAllExams(): Flow<List<Exam>>
    
    @Query("SELECT * FROM exams WHERE examId = :examId")
    fun getExamById(examId: String): Flow<Exam?>
    
    @Query("SELECT * FROM exams WHERE courseId = :courseId ORDER BY startDate DESC")
    fun getExamsByCourse(courseId: String): Flow<List<Exam>>
    
    @Query("SELECT * FROM exams WHERE classId = :classId ORDER BY startDate DESC")
    fun getExamsByClass(classId: String): Flow<List<Exam>>
    
    @Query("SELECT * FROM exams WHERE createdBy = :teacherId ORDER BY startDate DESC")
    fun getExamsByTeacher(teacherId: String): Flow<List<Exam>>
    
    @Query("SELECT * FROM exams WHERE startDate >= :startDate ORDER BY startDate")
    fun getUpcomingExams(startDate: Date): Flow<List<Exam>>
    
    @Query("SELECT * FROM exams WHERE startDate BETWEEN :startDate AND :endDate ORDER BY startDate")
    fun getExamsByDateRange(startDate: Date, endDate: Date): Flow<List<Exam>>
    
    @Query("SELECT * FROM exams WHERE status = :status ORDER BY startDate DESC")
    fun getExamsByStatus(status: String): Flow<List<Exam>>
    
    @Query("SELECT COUNT(*) FROM exams WHERE classId = :classId AND status = 'SCHEDULED'")
    suspend fun countUpcomingExamsByClass(classId: String): Int
    
    @Query("UPDATE exams SET status = :status, updatedAt = :updatedAt WHERE examId = :examId")
    suspend fun updateExamStatus(examId: String, status: String, updatedAt: Date = Date())
} 