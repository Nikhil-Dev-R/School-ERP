package com.titanflaws.erp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.titanflaws.erp.data.datasource.local.dao.ExamDao
import com.titanflaws.erp.data.datasource.local.dao.ExamResultDao
import com.titanflaws.erp.data.model.Exam
import com.titanflaws.erp.data.model.ExamResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * Repository for managing Exam data from both Firestore and local Room database
 */
class ExamRepository @Inject constructor(
    private val examDao: ExamDao,
    private val examResultDao: ExamResultDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val examsCollection = firestore.collection("exams")
    private val resultsCollection = firestore.collection("exam_results")
    
    // Exam operations
    
    // Get an exam by ID
    fun getExamById(examId: String): Flow<Exam?> = 
        examDao.getExamById(examId)
    
    // Get all exams
    fun getAllExams(): Flow<List<Exam>> = 
        examDao.getAllExams()
    
    // Get exams by course
    fun getExamsByCourse(courseId: String): Flow<List<Exam>> = 
        examDao.getExamsByCourse(courseId)
    
    // Get exams by class
    fun getExamsByClass(classId: String): Flow<List<Exam>> = 
        examDao.getExamsByClass(classId)
    
    // Get exams created by a specific teacher
    fun getExamsByTeacher(teacherId: String): Flow<List<Exam>> = 
        examDao.getExamsByTeacher(teacherId)
    
    // Get upcoming exams
    fun getUpcomingExams(): Flow<List<Exam>> = 
        examDao.getUpcomingExams(Date())
    
    // Get exams by date range
    fun getExamsByDateRange(startDate: Date, endDate: Date): Flow<List<Exam>> = 
        examDao.getExamsByDateRange(startDate, endDate)
    
    // Get exams by status
    fun getExamsByStatus(status: String): Flow<List<Exam>> = 
        examDao.getExamsByStatus(status)
    
    // Create a new exam
    suspend fun createExam(
        title: String, description: String?,
        examType: String, classId: String,
        courseId: String, startDate: Date,
        endDate: Date, totalMarks: Int,
        passingMarks: Int, availableSections: List<String>?,
        instructions: String?, examDuration: Int?,
        gradingScheme: String?
    ): String {
        val currentUser = auth.currentUser?.uid ?: throw Exception("Not authenticated")
        
        val examId = UUID.randomUUID().toString()
        val exam = Exam(
            examId = examId, title = title,
            description = description, examType = examType,
            classId = classId, courseId = courseId,
            startDate = startDate, endDate = endDate,
            totalMarks = totalMarks, passingMarks = passingMarks,
            createdBy = currentUser, duration = examDuration,
        )
        
        // Save to Firestore
        examsCollection.document(examId).set(exam).await()
        
        // Save to local database
        examDao.insertExam(exam)
        
        return examId
    }
    
    // Update an existing exam
    suspend fun updateExam(exam: Exam) {
        // Update in Firestore
        examsCollection.document(exam.examId).set(exam).await()
        
        // Update in local database
        examDao.updateExam(exam)
    }
    
    // Delete an exam
    suspend fun deleteExam(examId: String) {
        // Delete from Firestore
        examsCollection.document(examId).delete().await()
        
        // Delete from local database
        examDao.deleteExamById(examId)
    }
    
    // Update exam status
    suspend fun updateExamStatus(examId: String, status: String) {
        val updatedAt = Date()
        
        // Update in Firestore
        examsCollection.document(examId).update(
            mapOf(
                "status" to status,
                "updatedAt" to updatedAt
            )
        ).await()
        
        // Update in local database
        examDao.updateExamStatus(examId, status, updatedAt)
    }
    
    // Publish exam results
    suspend fun publishExamResults(examId: String, publish: Boolean) {
        val updatedAt = Date()
        
        // Update in Firestore
        examsCollection.document(examId).update(
            mapOf(
                "publishResults" to publish,
                "updatedAt" to updatedAt
            )
        ).await()
        
        // Update in local database
//        examDao.updateResultsPublishStatus(examId, publish, updatedAt)
        
        // Also update all exam results
        resultsCollection.whereEqualTo("examId", examId).get().await().documents.forEach { doc ->
            doc.reference.update("isPublished", publish)
        }
        
        // Update in local database
//        examResultDao.updateResultsPublishStatus(examId, publish, updatedAt)
    }
    
    // Exam result operations
    
    // Get a result by ID
    fun getResultById(resultId: String): Flow<ExamResult?> = 
        examResultDao.getResultById(resultId)
    
    // Get results for a specific exam
    fun getResultsByExam(examId: String): Flow<List<ExamResult>> = 
        examResultDao.getResultsByExam(examId)
    
    // Get results for a specific student
    fun getResultsByStudent(studentId: String): Flow<List<ExamResult>> = 
        examResultDao.getResultsByStudent(studentId)
    
    // Get a specific student's result for a specific exam
    fun getStudentResultForExam(examId: String, studentId: String): Flow<ExamResult?> = 
        examResultDao.getStudentResultForExam(examId, studentId)
    
    // Create or update a student's exam result
    suspend fun saveExamResult(
        examId: String,
        studentId: String,
        marksObtained: Float,
        totalMarks: Int,
        remarks: String?,
        courseName: String?
    ) {
        val currentUser = auth.currentUser?.uid ?: throw Exception("Not authenticated")
        
        // Get the exam to calculate percentage and status
        val exam = examDao.getExamById(examId).collect { exam ->
            if (exam != null) {
                val resultId = UUID.randomUUID().toString()
                val percentage = (marksObtained / totalMarks) * 100
                val status = if (marksObtained >= exam.passingMarks) "PASS" else "FAIL"
                
                // Determine grade based on percentage (example logic)
                val grade = when {
                    percentage >= 90 -> "A+"
                    percentage >= 80 -> "A"
                    percentage >= 70 -> "B"
                    percentage >= 60 -> "C"
                    percentage >= 50 -> "D"
                    else -> "F"
                }
                
                val result = ExamResult(
                    resultId = resultId,
                    examId = examId,
                    studentId = studentId,
                    marksObtained = marksObtained,
                    percentage = percentage,
                    grade = grade,
                    remarks = remarks,
                    status = status,
                    evaluatedBy = currentUser,
                    evaluatedAt = Date(),
                    courseName = courseName
                )
                
                // Save to Firestore
                resultsCollection.document(resultId).set(result).await()
                
                // Save to local database
                examResultDao.insertResult(result)
            }
        }
    }
    
    // Update an exam result's evaluation
    suspend fun updateResultEvaluation(
        resultId: String,
        marks: Float,
        totalMarks: Int,
        remarks: String?
    ) {
        val currentUser = auth.currentUser?.uid ?: throw Exception("Not authenticated")
        
        // Get the existing result first
        examResultDao.getResultById(resultId).collect { result ->
            result?.let {
                val examId = result.examId
                
                // Get the exam to calculate percentage and status
                examDao.getExamById(examId).collect { exam ->
                    if (exam != null) {
                        val percentage = (marks / totalMarks) * 100
                        val status = if (marks >= exam.passingMarks) "PASS" else "FAIL"
                        
                        // Determine grade based on percentage (example logic)
                        val grade = when {
                            percentage >= 90 -> "A+"
                            percentage >= 80 -> "A"
                            percentage >= 70 -> "B"
                            percentage >= 60 -> "C"
                            percentage >= 50 -> "D"
                            else -> "F"
                        }
                        
                        // Update in Firestore
                        val updates = mapOf(
                            "marksObtained" to marks,
                            "percentage" to percentage,
                            "grade" to grade,
                            "status" to status,
                            "remarks" to remarks,
                            "evaluatedBy" to currentUser,
                            "evaluatedAt" to Date(),
                            "updatedAt" to Date()
                        )
                        
                        resultsCollection.document(resultId).update(updates).await()
                        
                        // Update in local database
                        examResultDao.updateResultEvaluation(
                            resultId = resultId,
                            marks = marks,
                            percentage = percentage,
                            grade = grade,
                            status = status,
                            remarks = remarks,
                            evaluatedBy = currentUser
                        )
                    }
                }
            }
        }
    }
    
    // Verify an exam result
    suspend fun verifyResult(resultId: String) {
        val currentUser = auth.currentUser?.uid ?: throw Exception("Not authenticated")
        val verifiedAt = Date()
        
        // Update in Firestore
        resultsCollection.document(resultId).update(
            mapOf(
                "verifiedBy" to currentUser,
                "verifiedAt" to verifiedAt,
                "updatedAt" to verifiedAt
            )
        ).await()
        
        // Update in local database
//        examResultDao.verifyResult(resultId, currentUser, verifiedAt)
    }
    
    // Synchronization methods
    
    // Sync all exams from Firestore to local database
    suspend fun syncAllExams() {
        try {
            val examsSnapshot = examsCollection.get().await()
            val exams = examsSnapshot.toObjects(Exam::class.java)
            examDao.insertExams(exams)
        } catch (e: Exception) {
            // Handle errors
        }
    }
    
    // Sync exams for a specific class
    suspend fun syncExamsByClass(classId: String) {
        try {
            val examsSnapshot = examsCollection.whereEqualTo("classId", classId).get().await()
            val exams = examsSnapshot.toObjects(Exam::class.java)
            examDao.insertExams(exams)
        } catch (e: Exception) {
            // Handle errors
        }
    }
    
    // Sync exams created by a specific teacher
    suspend fun syncExamsByCreator(teacherId: String) {
        try {
            val examsSnapshot = examsCollection.whereEqualTo("createdById", teacherId).get().await()
            val exams = examsSnapshot.toObjects(Exam::class.java)
            examDao.insertExams(exams)
        } catch (e: Exception) {
            // Handle errors
        }
    }
    
    // Sync all exam results
    suspend fun syncExamResults() {
        try {
            val resultsSnapshot = resultsCollection.get().await()
            val results = resultsSnapshot.toObjects(ExamResult::class.java)
            examResultDao.insertResults(results)
        } catch (e: Exception) {
            // Handle errors
        }
    }
    
    // Sync results for a specific exam
    suspend fun syncResultsByExam(examId: String) {
        try {
            val resultsSnapshot = resultsCollection.whereEqualTo("examId", examId).get().await()
            val results = resultsSnapshot.toObjects(ExamResult::class.java)
            examResultDao.insertResults(results)
        } catch (e: Exception) {
            // Handle errors
        }
    }
    
    // Sync results for a specific student
    suspend fun syncResultsByStudent(studentId: String) {
        try {
            val resultsSnapshot = resultsCollection.whereEqualTo("studentId", studentId).get().await()
            val results = resultsSnapshot.toObjects(ExamResult::class.java)
            examResultDao.insertResults(results)
        } catch (e: Exception) {
            // Handle errors
        }
    }
    
    // Analytics methods
    
    // Get exam performance statistics
    suspend fun getExamStatistics(examId: String): Map<String, Any> {
        val totalStudents = examResultDao.countTotalResultsForExam(examId)
        val passedStudents = examResultDao.countPassedStudentsForExam(examId)
        val failedStudents = examResultDao.countFailedStudentsForExam(examId)
        val averagePercentage = examResultDao.getAveragePercentageForExam(examId) ?: 0f
        
        return mapOf(
            "totalStudents" to totalStudents,
            "passedStudents" to passedStudents,
            "failedStudents" to failedStudents,
            "passPercentage" to if (totalStudents > 0) (passedStudents * 100.0 / totalStudents) else 0.0,
            "averagePercentage" to averagePercentage
        )
    }
} 