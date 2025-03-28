package com.titanflaws.erp.domain.repository

import com.titanflaws.erp.data.model.ExamResult
import com.titanflaws.erp.utils.Resource

/**
 * Repository interface for exam result operations
 */
interface ExamResultRepository {
    
    /**
     * Get a student's result for a specific exam
     */
    suspend fun getStudentExamResult(examId: String, studentId: String): Resource<ExamResult>
    
    /**
     * Get all results for a specific exam
     */
    suspend fun getResultsForExam(examId: String): Resource<List<ExamResult>>
    
    /**
     * Get all results for a specific student
     */
    suspend fun getResultsForStudent(studentId: String): Resource<List<ExamResult>>
    
    /**
     * Create a new exam result
     */
    suspend fun createExamResult(examResult: ExamResult): Resource<ExamResult>
    
    /**
     * Update an existing exam result
     */
    suspend fun updateExamResult(examResult: ExamResult): Resource<ExamResult>
    
    /**
     * Delete an exam result
     */
    suspend fun deleteExamResult(resultId: String): Resource<Boolean>
} 