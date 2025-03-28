package com.titanflaws.erp.data.repository

import com.titanflaws.erp.data.datasource.remote.api.ExamApi
import com.titanflaws.erp.data.model.ExamResult
import com.titanflaws.erp.domain.repository.ExamResultRepository
import com.titanflaws.erp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of the ExamResultRepository interface
 */
class ExamResultRepositoryImpl @Inject constructor(
    private val examApi: ExamApi
) : ExamResultRepository {

    override suspend fun getStudentExamResult(examId: String, studentId: String): Resource<ExamResult> = withContext(Dispatchers.IO) {
        try {
            val response = examApi.getStudentExamResult(examId, studentId)
            Resource.Success(response)
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

    override suspend fun getResultsForExam(examId: String): Resource<List<ExamResult>> = withContext(Dispatchers.IO) {
        try {
            val response = examApi.getExamResults(examId)
            Resource.Success(response)
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

    override suspend fun getResultsForStudent(studentId: String): Resource<List<ExamResult>> = withContext(Dispatchers.IO) {
        try {
            val response = examApi.getStudentResults(studentId)
            Resource.Success(response)
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

    override suspend fun createExamResult(examResult: ExamResult): Resource<ExamResult> = withContext(Dispatchers.IO) {
        try {
            val response = examApi.createExamResult(examResult)
            Resource.Success(response)
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

    override suspend fun updateExamResult(examResult: ExamResult): Resource<ExamResult> = withContext(Dispatchers.IO) {
        try {
            val response = examApi.updateExamResult(examResult.resultId, examResult)
            Resource.Success(response)
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

    override suspend fun deleteExamResult(resultId: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        try {
            examApi.deleteExamResult(resultId)
            Resource.Success(true)
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
} 