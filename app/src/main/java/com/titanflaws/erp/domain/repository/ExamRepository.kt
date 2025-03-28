package com.titanflaws.erp.domain.repository

import com.titanflaws.erp.data.model.Exam
import com.titanflaws.erp.utils.Resource

/**
 * Repository interface for exam operations
 */
interface ExamRepository {

    /**
     * Get an exam by ID
     */
    suspend fun getExamById(examId: String): Resource<Exam>
    
    /**
     * Get all exams for a specific course
     */
    suspend fun getExamsByCourse(courseId: String): Resource<List<Exam>>
    
    /**
     * Get all exams for a specific student
     */
    suspend fun getExamsForStudent(studentId: String): Resource<List<Exam>>
    
    /**
     * Get all upcoming exams for a course
     */
    suspend fun getUpcomingExamsForCourse(courseId: String): Resource<List<Exam>>
    
    /**
     * Create a new exam
     */
    suspend fun createExam(exam: Exam): Resource<Exam>
    
    /**
     * Update an existing exam
     */
    suspend fun updateExam(exam: Exam): Resource<Exam>
    
    /**
     * Delete an exam
     */
    suspend fun deleteExam(examId: String): Resource<Boolean>
} 