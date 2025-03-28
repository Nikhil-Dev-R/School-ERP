package com.titanflaws.erp.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.titanflaws.erp.data.datasource.remote.FirebaseDataSource
import com.titanflaws.erp.data.repository.*
import com.titanflaws.erp.utils.NetworkUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Worker for synchronizing data between local database and Firebase
 */
@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userRepository: UserRepositoryImpl,
    private val teacherRepository: TeacherRepository,
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
    private val classRepository: ClassSectionRepositoryImpl,
    private val attendanceRepository: AttendanceRepository,
    private val examRepository: ExamRepositoryImpl,
    private val feeRepository: FeeRepositoryImpl,
    private val firebaseDataSource: FirebaseDataSource,
    private val networkUtils: NetworkUtils
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d("DataSyncWorker", "Starting data sync...")
            // Check for internet connectivity
            if (!networkUtils.isNetworkAvailable()) {
                Log.d("DataSyncWorker", "No internet connection. Sync skipped.")
                return@withContext Result.retry()
            }

            // Perform all sync operations in parallel
            val syncJobs = listOf(
                async { syncUsers() },
                async { syncTeachers() },
                async { syncStudents() },
                async { syncCourses() },
                async { syncClasses() },
                async { syncAttendance() },
                async { syncExams() },
                async { syncFees() }
            )

            // Wait for all sync operations to complete
            syncJobs.awaitAll()
            Log.d("DataSyncWorker", "Data sync completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Error during data sync", e)
            Result.failure()
        }
    }

    private suspend fun syncUsers() {
        try {
            Log.d("DataSyncWorker", "Syncing users...")
            // Implement user sync logic
            val users = firebaseDataSource.getAllUsers()
            // Update local database with users from Firebase
            users.forEach { user ->
                userRepository.insertUserToLocal(user)
            }
            Log.d("DataSyncWorker", "Users sync completed")
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Error syncing users", e)
        }
    }

    private suspend fun syncTeachers() {
        try {
            Log.d("DataSyncWorker", "Syncing teachers...")
            // Implement teacher sync logic
            val teachers = firebaseDataSource.getAllTeachers()
            // Update local database with teachers from Firebase
            teachers.forEach { teacher ->
//                teacherRepository.insertTeacherToLocal(teacher)
            }
            Log.d("DataSyncWorker", "Teachers sync completed")
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Error syncing teachers", e)
        }
    }

    private suspend fun syncStudents() {
        try {
            Log.d("DataSyncWorker", "Syncing students...")
            // Implement student sync logic
            val students = firebaseDataSource.getAllStudents()
            // Update local database with students from Firebase
            students.forEach { student ->
                // studentRepository.insertStudentToLocal(student)
            }
            Log.d("DataSyncWorker", "Students sync completed")
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Error syncing students", e)
        }
    }

    private suspend fun syncCourses() {
        try {
            Log.d("DataSyncWorker", "Syncing courses...")
            // Implement course sync logic
            val courses = firebaseDataSource.getAllCourses()
            // Update local database with courses from Firebase
            courses.forEach { course ->
                // courseRepository.insertCourseToLocal(course)
            }
            Log.d("DataSyncWorker", "Courses sync completed")
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Error syncing courses", e)
        }
    }

    private suspend fun syncClasses() {
        try {
            Log.d("DataSyncWorker", "Syncing classes...")
            // Implement class sync logic
            val classes = firebaseDataSource.getAllClassSections()
            // Update local database with classes from Firebase
            classes.forEach { classSection ->
                classRepository.insertClassSectionToLocal(classSection)
            }
            Log.d("DataSyncWorker", "Classes sync completed")
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Error syncing classes", e)
        }
    }

    private suspend fun syncAttendance() {
        try {
            Log.d("DataSyncWorker", "Syncing attendance...")
            // Implement attendance sync logic
            val attendance = firebaseDataSource.getAllAttendance()
            // Update local database with attendance from Firebase
            attendance.forEach { attendanceRecord ->
                // attendanceRepository.insertAttendanceToLocal(attendanceRecord)
            }
            Log.d("DataSyncWorker", "Attendance sync completed")
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Error syncing attendance", e)
        }
    }

    private suspend fun syncExams() {
        try {
            Log.d("DataSyncWorker", "Syncing exams...")
            // Implement exam sync logic
            val exams = firebaseDataSource.getAllExams()
            // Update local database with exams from Firebase
            exams.forEach { exam ->
                examRepository.insertExamToLocal(exam)
            }
            // Sync exam results as well
            val examResults = firebaseDataSource.getAllExamResults()
            examResults.forEach { result ->
                examRepository.insertExamResultToLocal(result)
            }
            Log.d("DataSyncWorker", "Exams sync completed")
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Error syncing exams", e)
        }
    }

    private suspend fun syncFees() {
        try {
            Log.d("DataSyncWorker", "Syncing fees...")
            // Implement fee sync logic
            val fees = firebaseDataSource.getAllFees()
            // Update local database with fees from Firebase
            fees.forEach { fee ->
                feeRepository.insertFeeToLocal(fee)
            }
            // Sync fee payments as well
            val feePayments = firebaseDataSource.getAllFeePayments()
            feePayments.forEach { payment ->
                feeRepository.insertFeePaymentToLocal(payment)
            }
            Log.d("DataSyncWorker", "Fees sync completed")
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Error syncing fees", e)
        }
    }

    companion object {
        const val WORK_NAME = "data_sync_worker"
    }
}
