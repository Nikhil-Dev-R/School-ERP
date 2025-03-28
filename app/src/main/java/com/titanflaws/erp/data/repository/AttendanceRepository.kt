package com.titanflaws.erp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.titanflaws.erp.data.datasource.local.dao.AttendanceDao
import com.titanflaws.erp.data.model.Attendance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * Repository for managing Attendance data from both Firestore and local Room database
 */
class AttendanceRepository @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val attendanceCollection = firestore.collection("attendance")
    
    // Get attendance records for a specific user
    fun getAttendanceByUserId(userId: String): Flow<List<Attendance>> = 
        attendanceDao.getAttendanceByUserId(userId)
    
    // Get attendance records for a specific date
    fun getAttendanceByDate(date: Date): Flow<List<Attendance>> = 
        attendanceDao.getAttendanceByDate(date)
    
    // Get attendance records for a specific class and date
    fun getAttendanceByClassAndDate(classId: String, date: Date): Flow<List<Attendance>> = 
        attendanceDao.getAttendanceByClassAndDate(classId, date)
    
    // Get attendance records for a specific course/subject
    fun getAttendanceByCourse(courseId: String): Flow<List<Attendance>> = 
        attendanceDao.getAttendanceByCourse(courseId)
    
    // Mark attendance for a student
    suspend fun markAttendance(
        userId: String,
        userType: String,
        date: Date,
        status: String,
        courseId: String?,
        classId: String?,
        sectionId: String?,
        reason: String?,
        remarks: String?
    ) {
        val currentUser = auth.currentUser?.uid ?: throw Exception("Not authenticated")
        
        val attendanceId = UUID.randomUUID().toString()
        val attendance = Attendance(
            attendanceId = attendanceId,
            userId = userId,
            userType = userType,
            date = date,
            status = status,
            courseId = courseId,
            classId = classId,
            sectionId = sectionId,
            markedById = currentUser,
            reason = reason,
            remarks = remarks
        )
        
        // Save to Firestore
        attendanceCollection.document(attendanceId).set(attendance).await()
        
        // Save to local database
        attendanceDao.insertAttendance(attendance)
    }
    
    // Update attendance status
    suspend fun updateAttendanceStatus(attendanceId: String, status: String, reason: String?, remarks: String?) {
        // Update in Firestore
        val updates = mutableMapOf<String, Any>()
        updates["status"] = status
        updates["updatedAt"] = Date()
        
        reason?.let { updates["reason"] = it }
        remarks?.let { updates["remarks"] = it }
        
        attendanceCollection.document(attendanceId).update(updates).await()
        
        // Update in local database
        attendanceDao.updateAttendanceStatus(attendanceId, status, reason, remarks, Date())
    }
    
    // Get attendance statistics for a user
    suspend fun getAttendanceStatistics(userId: String, startDate: Date, endDate: Date): Map<String, Int> {
        val attendanceList = attendanceDao.getAttendanceByUserIdAndDateRange(userId, startDate, endDate)
        
        val statistics = mutableMapOf<String, Int>()
        statistics["PRESENT"] = attendanceList.count { it.status == "PRESENT" }
        statistics["ABSENT"] = attendanceList.count { it.status == "ABSENT" }
        statistics["LATE"] = attendanceList.count { it.status == "LATE" }
        statistics["HALF_DAY"] = attendanceList.count { it.status == "HALF_DAY" }
        statistics["LEAVE"] = attendanceList.count { it.status == "LEAVE" }
        
        return statistics
    }
    
    // Sync attendance data with Firestore
    suspend fun syncAttendance() {
        try {
            val attendanceSnapshot = attendanceCollection.get().await()
            val attendanceList = attendanceSnapshot.toObjects(Attendance::class.java)
            attendanceDao.insertAttendances(attendanceList)
        } catch (e: Exception) {
            // Handle errors
        }
    }
    
    // Sync attendance for a specific class
    suspend fun syncClassAttendance(classId: String) {
        try {
            val attendanceSnapshot = attendanceCollection
                .whereEqualTo("classId", classId)
                .get().await()
            val attendanceList = attendanceSnapshot.toObjects(Attendance::class.java)
            attendanceDao.insertAttendances(attendanceList)
        } catch (e: Exception) {
            // Handle errors
        }
    }
} 