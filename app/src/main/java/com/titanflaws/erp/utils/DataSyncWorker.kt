package com.titanflaws.erp.utils

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.titanflaws.erp.data.repository.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker to synchronize data between Firebase and local database
 */
@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
//    private val userRepository: UserRepository,
//    private val studentRepository: StudentRepository,
//    private val teacherRepository: TeacherRepository,
//    private val attendanceRepository: AttendanceRepository,
//    private val examRepository: ExamRepository,
//    private val feeRepository: FeeRepository,
//    private val courseRepository: CourseRepository,
//    private val classSectionRepository: ClassSectionRepository,
//    private val auth: FirebaseAuth
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Check if user is logged in
            /*val currentUser = auth.currentUser
                ?: // No need to sync if user is not logged in
                return@withContext Result.success()

            // Get user role to determine what data to sync
            val userId = currentzUser.uid
            val userRole = params.inputData.getString(KEY_USER_ROLE) ?: syncAllData()
            
            // Sync based on role and data type
            when (userRole) {
                "admin" -> syncAdminData()
                "teacher" -> syncTeacherData(userId)
                "student" -> syncStudentData(userId)
                "parent" -> syncParentData(userId)
                "staff" -> syncStaffData(userId)
                else -> syncAllData()
            }*/
            
            Result.success()
        } catch (e: Exception) {
            // Retry if failed
            Result.retry()
        }
    }
/*
    // Sync all data types (for admin)
    private suspend fun syncAdminData() {
        userRepository.syncAllUsers()
        studentRepository.syncAllStudents()
        teacherRepository.syncAllTeachers()
        attendanceRepository.syncAttendance()
        examRepository.syncAllExams()
        feeRepository.syncFees()
        courseRepository.syncAllCourses()
        classSectionRepository.syncAllClassSections()
    }
    
    // Sync teacher-specific data
    private suspend fun syncTeacherData(userId: String) {
        userRepository.refreshUserData(userId)
        teacherRepository.syncTeacher(userId)
        
        // Get teacher's classes and sync related data
        val teacher = teacherRepository.getTeacherByUserId(userId).firstOrNull()
        teacher?.classesTaught?.forEach { classId ->
            classSectionRepository.syncClassSection(classId)
            studentRepository.syncAllStudents() // Optimize: could filter by class
            attendanceRepository.syncClassAttendance(classId)
        }
        
        // Sync courses taught by this teacher
        teacher?.subjectsTaught?.forEach { courseId ->
            courseRepository.syncCourse(courseId)
        }
        
        // Sync exams created by this teacher
        examRepository.syncExamsByCreator(userId)
    }
    
    // Sync student-specific data
    private suspend fun syncStudentData(userId: String) {
        userRepository.refreshUserData(userId)
        
        // Get student info
        val student = studentRepository.getStudentByUserId(userId).firstOrNull()
        if (student != null) {
            // Sync class/section data
            student.classId?.let { classId ->
                classSectionRepository.syncClassSection(classId)
            }
            
            // Sync subjects for this student
            student.subjects?.forEach { courseId ->
                courseRepository.syncCourse(courseId)
            }
            
            // Sync attendance for this student
            attendanceRepository.syncAttendance() // Optimize: filter by student
            
            // Sync exams and results for this student
            examRepository.syncExams() // Optimize: filter by class
            
            // Sync fee data for this student
            feeRepository.syncStudentFees(student.studentId)
        }
    }
    
    // Sync parent-specific data
    private suspend fun syncParentData(userId: String) {
        userRepository.refreshUserData(userId)
        
        // Sync children data
        studentRepository.syncAllStudents() // Optimize: filter by parent
        
        // Fee payments for children
        feeRepository.syncFees() // Optimize: filter by children
        
        // Attendance for children
        attendanceRepository.syncAttendance() // Optimize: filter by children
        
        // Exams for children
        examRepository.syncExams() // Optimize: filter by children's classes
    }
    
    // Sync staff-specific data
    private suspend fun syncStaffData(userId: String) {
        userRepository.refreshUserData(userId)
        
        // Staff may need different data based on their department
        // For now, sync minimal data
        userRepository.syncAllUsers()
    }
    
    // Sync all data (fallback option)
    private suspend fun syncAllData(): String {
        userRepository.syncAllUsers()
        studentRepository.syncAllStudents()
        teacherRepository.syncAllTeachers()
        attendanceRepository.syncAttendance()
        examRepository.syncExams()
        feeRepository.syncFees()
        courseRepository.syncAllCourses()
        classSectionRepository.syncAllClassSections()
        
        // Try to get user role for next sync
        val userId = auth.currentUser?.uid ?: return ""
        val user = userRepository.getUserById(userId).firstOrNull()
        return user?.role ?: ""
    }*/
    
    companion object {
        private const val SYNC_WORK_NAME = "data_sync_work"
        private const val KEY_USER_ROLE = "user_role"
        
        /**
         * Schedule periodic data synchronization
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val syncRequest = PeriodicWorkRequestBuilder<DataSyncWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }
        
        /**
         * Schedule one-time immediate data synchronization
         */
        fun syncNow(context: Context, userRole: String? = null) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val inputData = if (userRole != null) {
                Data.Builder()
                    .putString(KEY_USER_ROLE, userRole)
                    .build()
            } else {
                Data.EMPTY
            }
            
            val syncRequest = OneTimeWorkRequestBuilder<DataSyncWorker>()
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()
            
            WorkManager.getInstance(context).enqueue(syncRequest)
        }
        
        /**
         * Cancel all scheduled synchronization
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
        }
    }
} 