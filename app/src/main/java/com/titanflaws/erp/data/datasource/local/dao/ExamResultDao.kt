package com.titanflaws.erp.data.datasource.local.dao

import androidx.room.*
import com.titanflaws.erp.data.model.ExamResult
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Data Access Object for the ExamResult entity
 */
@Dao
interface ExamResultDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ExamResult)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(results: List<ExamResult>)
    
    @Update
    suspend fun updateResult(result: ExamResult)
    
    @Delete
    suspend fun deleteResult(result: ExamResult)
    
    @Query("DELETE FROM exam_results WHERE resultId = :resultId")
    suspend fun deleteResultById(resultId: String)
    
    @Query("SELECT * FROM exam_results ORDER BY updatedAt DESC")
    fun getAllResults(): Flow<List<ExamResult>>
    
    @Query("SELECT * FROM exam_results WHERE resultId = :resultId")
    fun getResultById(resultId: String): Flow<ExamResult?>
    
    @Query("SELECT * FROM exam_results WHERE examId = :examId ORDER BY studentId")
    fun getResultsByExam(examId: String): Flow<List<ExamResult>>
    
    @Query("SELECT * FROM exam_results WHERE studentId = :studentId ORDER BY updatedAt DESC")
    fun getResultsByStudent(studentId: String): Flow<List<ExamResult>>
    
    @Query("SELECT * FROM exam_results WHERE examId = :examId AND studentId = :studentId")
    fun getStudentResultForExam(examId: String, studentId: String): Flow<ExamResult?>
    
    @Query("UPDATE exam_results SET marksObtained = :marks, percentage = :percentage, grade = :grade, status = :status, remarks = :remarks, evaluatedBy = :evaluatedBy, evaluatedAt = :evaluatedAt, updatedAt = :updatedAt WHERE resultId = :resultId")
    suspend fun updateResultEvaluation(
        resultId: String,
        marks: Float,
        percentage: Float,
        grade: String?,
        status: String,
        remarks: String?,
        evaluatedBy: String,
        evaluatedAt: Date = Date(),
        updatedAt: Date = Date()
    )
    
    @Query("UPDATE exam_results SET updatedAt = :updatedAt WHERE resultId = :resultId")
    suspend fun verifyResult(
        resultId: String,
        updatedAt: Date = Date()
    )
    
//    @Query("UPDATE exam_results SET isPublished = :isPublished, updatedAt = :updatedAt WHERE examId = :examId")
//    suspend fun updateResultsPublishStatus(
//        examId: String,
//        isPublished: Boolean,
//        updatedAt: Date = Date()
//    )
    
    @Query("SELECT AVG(percentage) FROM exam_results WHERE examId = :examId AND status = 'PASS'")
    suspend fun getAveragePercentageForExam(examId: String): Float?
    
    @Query("SELECT COUNT(*) FROM exam_results WHERE examId = :examId AND status = 'PASS'")
    suspend fun countPassedStudentsForExam(examId: String): Int
    
    @Query("SELECT COUNT(*) FROM exam_results WHERE examId = :examId AND status = 'FAIL'")
    suspend fun countFailedStudentsForExam(examId: String): Int
    
    @Query("SELECT COUNT(*) FROM exam_results WHERE examId = :examId")
    suspend fun countTotalResultsForExam(examId: String): Int
} 