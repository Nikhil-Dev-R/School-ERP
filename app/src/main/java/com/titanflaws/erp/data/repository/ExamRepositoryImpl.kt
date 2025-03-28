package com.titanflaws.erp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.titanflaws.erp.data.datasource.local.dao.ExamDao
import com.titanflaws.erp.data.datasource.local.dao.ExamResultDao
import com.titanflaws.erp.data.datasource.remote.api.ExamApi
import com.titanflaws.erp.data.model.Exam
import com.titanflaws.erp.data.model.ExamResult
import com.titanflaws.erp.domain.repository.ExamRepository
import com.titanflaws.erp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the ExamRepository interface
 */
@Singleton
class ExamRepositoryImpl @Inject constructor(
    private val examApi: ExamApi,
    private val examDao: ExamDao,
    private val examResultDao: ExamResultDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ExamRepository {

    private val examsCollection = firestore.collection("exams")
    private val resultsCollection = firestore.collection("exam_results")

    override suspend fun getExamById(examId: String): Resource<Exam> = withContext(Dispatchers.IO) {
        try {
            val localExam = examDao.getExamById(examId).firstOrNull()
            
            if (localExam != null) {
                Resource.Success(localExam)
            } else {
                // Try to fetch from Firestore
                val document = examsCollection.document(examId).get().await()
                if (document.exists()) {
                    val exam = document.toObject(Exam::class.java)
                    // Cache in local database
                    exam?.let { examDao.insertExam(it) }
                    Resource.Success(exam!!)
                } else {
                    // Fall back to API if needed
                    val remoteExam = examApi.getExam(examId)
                    examDao.insertExam(remoteExam)
                    Resource.Success(remoteExam)
                }
            }
        } catch (e: HttpException) {
            Resource.Error(
                message = "Error ${e.code()}: ${e.message()}",
            )
        } catch (e: IOException) {
            Resource.Error(
                message = "Couldn't reach server. Check your internet connection."
            )
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "An unknown error occurred."
            )
        }
    }

    override suspend fun getExamsByCourse(courseId: String): Resource<List<Exam>> = withContext(Dispatchers.IO) {
        try {
            val localExams = examDao.getExamsByCourse(courseId).firstOrNull()
            
            if (!localExams.isNullOrEmpty()) {
                Resource.Success(localExams)
            } else {
                // Try to fetch from Firestore
                val snapshot = examsCollection
                    .whereEqualTo("courseId", courseId)
                    .get().await()
                
                val exams = snapshot.toObjects(Exam::class.java)
                
                // Cache in local database
                if (exams.isNotEmpty()) {
                    examDao.insertExams(exams)
                    Resource.Success(exams)
                } else {
                    // Fall back to API if needed
                    val remoteExams = examApi.getExamsByCourse(courseId)
                    examDao.insertExams(remoteExams)
                    Resource.Success(remoteExams)
                }
            }
        } catch (e: HttpException) {
            Resource.Error(
                message = "Error ${e.code()}: ${e.message()}",
            )
        } catch (e: IOException) {
            Resource.Error(
                message = "Couldn't reach server. Check your internet connection."
            )
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "An unknown error occurred."
            )
        }
    }

    override suspend fun getExamsForStudent(studentId: String): Resource<List<Exam>> = withContext(Dispatchers.IO) {
        try {
            // First get the student's class
            val studentDoc = firestore.collection("students").document(studentId).get().await()
            val classId = studentDoc.getString("classId")
            
            if (classId.isNullOrEmpty()) {
                // Fall back to API if we can't get the class ID
                val remoteExams = examApi.getStudentExams(studentId)
                examDao.insertExams(remoteExams)
                return@withContext Resource.Success(remoteExams)
            }
            
            // Then get exams for that class
            val localExams = examDao.getExamsByClass(classId).firstOrNull()
            
            if (!localExams.isNullOrEmpty()) {
                Resource.Success(localExams)
            } else {
                // Try to fetch from Firestore
                val snapshot = examsCollection.whereEqualTo("classId", classId).get().await()
                val exams = snapshot.toObjects(Exam::class.java)
                
                // Cache in local database
                if (exams.isNotEmpty()) {
                    examDao.insertExams(exams)
                    Resource.Success(exams)
                } else {
                    // Fall back to API if needed
                    val remoteExams = examApi.getStudentExams(studentId)
                    examDao.insertExams(remoteExams)
                    Resource.Success(remoteExams)
                }
            }
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "An unknown error occurred."
            )
        }
    }
    
    override suspend fun getUpcomingExamsForCourse(courseId: String): Resource<List<Exam>> = withContext(Dispatchers.IO) {
        try {
            val now = Date()
            val localExams: List<Exam>? = null
//            = examDao.getUpcomingExamsByCourse(courseId, now).firstOrNull()
            
            if (!localExams.isNullOrEmpty()) {
                Resource.Success(localExams)
            } else {
                // Try to fetch from Firestore
                val snapshot = examsCollection
                    .whereEqualTo("courseId", courseId)
                    .whereGreaterThan("startDate", now)
                    .get().await()
                
                val exams = snapshot.toObjects(Exam::class.java)
                
                // Cache in local database
                if (exams.isNotEmpty()) {
                    examDao.insertExams(exams)
                    Resource.Success(exams)
                } else {
                    // Fall back to API if needed
                    val remoteExams = examApi.getUpcomingExamsForCourse(courseId)
                    examDao.insertExams(remoteExams)
                    Resource.Success(remoteExams)
                }
            }
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "An unknown error occurred."
            )
        }
    }
    
    override suspend fun createExam(exam: Exam): Resource<Exam> = withContext(Dispatchers.IO) {
        try {
            // Generate ID if not provided
            val examId = if (exam.examId.isBlank()) UUID.randomUUID().toString() else exam.examId
            val currentUser = auth.currentUser?.uid
            
            val newExam = exam.copy(
                examId = examId,
                createdBy = currentUser ?: exam.createdBy,
                createdAt = Date(),
                updatedAt = Date()
            )
            
            // Save to Firestore
            examsCollection.document(examId).set(newExam).await()
            
            // Save to local database
            examDao.insertExam(newExam)
            
            Resource.Success(newExam)
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "Failed to create exam"
            )
        }
    }
    
    override suspend fun updateExam(exam: Exam): Resource<Exam> = withContext(Dispatchers.IO) {
        try {
            val updatedExam = exam.copy(updatedAt = Date())
            
            // Update in Firestore
            examsCollection.document(exam.examId).set(updatedExam).await()
            
            // Update in local database
            examDao.updateExam(updatedExam)
            
            Resource.Success(updatedExam)
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "Failed to update exam"
            )
        }
    }
    
    override suspend fun deleteExam(examId: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Delete from Firestore
            examsCollection.document(examId).delete().await()
            
            // Delete from local database
            examDao.deleteExamById(examId)
            
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "Failed to delete exam"
            )
        }
    }
    
    /**
     * Insert an exam to local database (for DataSyncWorker)
     */
    suspend fun insertExamToLocal(exam: Exam) {
        examDao.insertExam(exam)
    }
    
    /**
     * Insert an exam result to local database (for DataSyncWorker)
     */
    suspend fun insertExamResultToLocal(examResult: ExamResult) {
        examResultDao.insertResult(examResult)
    }
    
    /**
     * Sync all exams from Firestore to local database
     */
    suspend fun syncExams() {
        try {
            val snapshot = examsCollection.get().await()
            val exams = snapshot.toObjects(Exam::class.java)
            examDao.insertExams(exams)
        } catch (e: Exception) {
            // Handle errors
        }
    }
    
    /**
     * Sync exams created by a specific teacher
     */
    suspend fun syncExamsByCreator(creatorId: String) {
        try {
            val snapshot = examsCollection
                .whereEqualTo("createdBy", creatorId)
                .get().await()
            
            val exams = snapshot.toObjects(Exam::class.java)
            examDao.insertExams(exams)
        } catch (e: Exception) {
            // Handle errors
        }
    }
} 