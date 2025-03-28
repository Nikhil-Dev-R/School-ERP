package com.titanflaws.erp.data.datasource.remote.api

import com.titanflaws.erp.data.model.Exam
import com.titanflaws.erp.data.model.ExamResult
import retrofit2.http.*

/**
 * Retrofit API interface for exam related operations
 */
interface ExamApi {

    /**
     * Get an exam by ID
     */
    @GET("exams/{examId}")
    suspend fun getExam(@Path("examId") examId: String): Exam

    /**
     * Get all exams for a specific course
     */
    @GET("exams")
    suspend fun getExamsByCourse(@Query("courseId") courseId: String): List<Exam>

    /**
     * Get all exams for a specific student
     */
    @GET("students/{studentId}/exams")
    suspend fun getStudentExams(@Path("studentId") studentId: String): List<Exam>

    /**
     * Get all upcoming exams for a course
     */
    @GET("courses/{courseId}/exams/upcoming")
    suspend fun getUpcomingExamsForCourse(@Path("courseId") courseId: String): List<Exam>

    /**
     * Create a new exam
     */
    @POST("exams")
    suspend fun createExam(@Body exam: Exam): Exam

    /**
     * Update an existing exam
     */
    @PUT("exams/{examId}")
    suspend fun updateExam(
        @Path("examId") examId: String,
        @Body exam: Exam
    ): Exam

    /**
     * Delete an exam
     */
    @DELETE("exams/{examId}")
    suspend fun deleteExam(@Path("examId") examId: String)

    /**
     * Get a student's result for a specific exam
     */
    @GET("exams/{examId}/results/{studentId}")
    suspend fun getStudentExamResult(
        @Path("examId") examId: String,
        @Path("studentId") studentId: String
    ): ExamResult

    /**
     * Get all results for a specific exam
     */
    @GET("exams/{examId}/results")
    suspend fun getExamResults(@Path("examId") examId: String): List<ExamResult>

    /**
     * Get all results for a specific student
     */
    @GET("students/{studentId}/results")
    suspend fun getStudentResults(@Path("studentId") studentId: String): List<ExamResult>

    /**
     * Create a new exam result
     */
    @POST("exams/results")
    suspend fun createExamResult(@Body examResult: ExamResult): ExamResult

    /**
     * Update an existing exam result
     */
    @PUT("exams/results/{resultId}")
    suspend fun updateExamResult(
        @Path("resultId") resultId: String,
        @Body examResult: ExamResult
    ): ExamResult

    /**
     * Delete an exam result
     */
    @DELETE("exams/results/{resultId}")
    suspend fun deleteExamResult(@Path("resultId") resultId: String)
} 