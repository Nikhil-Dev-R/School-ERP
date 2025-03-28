package com.titanflaws.erp.data.datasource.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.titanflaws.erp.data.model.*
import com.titanflaws.erp.utils.FirebaseConstants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for fetching data from Firebase Firestore
 */
@Singleton
class FirebaseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    /**
     * Get all users from Firebase
     */
    suspend fun getAllUsers(): List<User> {
        return try {
            val snapshot = firestore.collection(FirebaseConstants.COLLECTION_USERS)
                .get()
                .await()
            
            snapshot.toObjects(User::class.java)
        } catch (e: Exception) {
            Log.e(e.toString(), "Error fetching users from Firebase")
            emptyList()
        }
    }

    /**
     * Get all teachers from Firebase
     */
    suspend fun getAllTeachers(): List<Teacher> {
        return try {
            val snapshot = firestore.collection(FirebaseConstants.COLLECTION_TEACHERS)
                .get()
                .await()
            
            snapshot.toObjects(Teacher::class.java)
        } catch (e: Exception) {
            Log.e(e.toString(),  "Error fetching teachers from Firebase")
            emptyList()
        }
    }

    /**
     * Get all students from Firebase
     */
    suspend fun getAllStudents(): List<Student> {
        return try {
            val snapshot = firestore.collection(FirebaseConstants.COLLECTION_STUDENTS)
                .get()
                .await()
            
            snapshot.toObjects(Student::class.java)
        } catch (e: Exception) {
            Log.e(e.toString(),  "Error fetching students from Firebase")
            emptyList()
        }
    }

    /**
     * Get all courses from Firebase
     */
    suspend fun getAllCourses(): List<Course> {
        return try {
            val snapshot = firestore.collection(FirebaseConstants.COLLECTION_COURSES)
                .get()
                .await()
            
            snapshot.toObjects(Course::class.java)
        } catch (e: Exception) {
            Log.e(e.toString(),  "Error fetching courses from Firebase")
            emptyList()
        }
    }

    /**
     * Get all class sections from Firebase
     */
    suspend fun getAllClassSections(): List<ClassSection> {
        return try {
            val snapshot = firestore.collection(FirebaseConstants.COLLECTION_CLASS_SECTIONS)
                .get()
                .await()
            
            snapshot.toObjects(ClassSection::class.java)
        } catch (e: Exception) {
            Log.e(e.toString(),  "Error fetching class sections from Firebase")
            emptyList()
        }
    }

    /**
     * Get all attendance records from Firebase
     */
    suspend fun getAllAttendance(): List<Attendance> {
        return try {
            val snapshot = firestore.collection(FirebaseConstants.COLLECTION_ATTENDANCE)
                .get()
                .await()
            
            snapshot.toObjects(Attendance::class.java)
        } catch (e: Exception) {
            Log.e(e.toString(), "Error fetching attendance from Firebase")
            emptyList()
        }
    }

    /**
     * Get all exams from Firebase
     */
    suspend fun getAllExams(): List<Exam> {
        return try {
            val snapshot = firestore.collection(FirebaseConstants.COLLECTION_EXAMS)
                .get()
                .await()
            
            snapshot.toObjects(Exam::class.java)
        } catch (e: Exception) {
            Log.e(e.toString(),  "Error fetching exams from Firebase")
            emptyList()
        }
    }

    /**
     * Get all exam results from Firebase
     */
    suspend fun getAllExamResults(): List<ExamResult> {
        return try {
            val snapshot = firestore.collection(FirebaseConstants.COLLECTION_EXAM_RESULTS)
                .get()
                .await()
            
            snapshot.toObjects(ExamResult::class.java)
        } catch (e: Exception) {
            Log.e(e.toString(), "Error fetching exam results from Firebase")
            emptyList()
        }
    }

    /**
     * Get all fees from Firebase
     */
    suspend fun getAllFees(): List<Fee> {
        return try {
            val snapshot = firestore.collection(FirebaseConstants.COLLECTION_FEES)
                .get()
                .await()
            
            snapshot.toObjects(Fee::class.java)
        } catch (e: Exception) {
            Log.e(e.toString(), "Error fetching fees from Firebase")
            emptyList()
        }
    }

    /**
     * Get all fee payments from Firebase
     */
    suspend fun getAllFeePayments(): List<FeePayment> {
        return try {
            val snapshot = firestore.collection(FirebaseConstants.COLLECTION_FEE_PAYMENTS)
                .get()
                .await()
            
            snapshot.toObjects(FeePayment::class.java)
        } catch (e: Exception) {
            Log.e(e.toString(), "Error fetching fee payments from Firebase")
            emptyList()
        }
    }

    /**
     * Get user by ID from Firebase
     */
    suspend fun getUserById(userId: String): User? {
        return try {
            val document = firestore.collection(FirebaseConstants.COLLECTION_USERS)
                .document(userId)
                .get()
                .await()
            
            if (document.exists()) {
                document.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(e.toString(),  "Error fetching user from Firebase")
            null
        }
    }

    /**
     * Get teacher by ID from Firebase
     */
    suspend fun getTeacherById(teacherId: String): Teacher? {
        return try {
            val document = firestore.collection(FirebaseConstants.COLLECTION_TEACHERS)
                .document(teacherId)
                .get()
                .await()
            
            if (document.exists()) {
                document.toObject(Teacher::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(e.toString(), "Error fetching teacher from Firebase")
            null
        }
    }

    /**
     * Get student by ID from Firebase
     */
    suspend fun getStudentById(studentId: String): Student? {
        return try {
            val document = firestore.collection(FirebaseConstants.COLLECTION_STUDENTS)
                .document(studentId)
                .get()
                .await()
            
            if (document.exists()) {
                document.toObject(Student::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(e.toString(), "Error fetching student from Firebase")
            null
        }
    }

    /**
     * Get course by ID from Firebase
     */
    suspend fun getCourseById(courseId: String): Course? {
        return try {
            val document = firestore.collection(FirebaseConstants.COLLECTION_COURSES)
                .document(courseId)
                .get()
                .await()
            
            if (document.exists()) {
                document.toObject(Course::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(e.toString(), "Error fetching course from Firebase")
            null
        }
    }
} 