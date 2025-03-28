package com.titanflaws.erp.data.datasource.local.dao

import androidx.room.*
import com.titanflaws.erp.data.model.Attendance
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Data Access Object for the Attendance entity
 */
@Dao
interface AttendanceDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendances(attendances: List<Attendance>)
    
    @Update
    suspend fun updateAttendance(attendance: Attendance)
    
    @Delete
    suspend fun deleteAttendance(attendance: Attendance)
    
    @Query("DELETE FROM attendance WHERE attendanceId = :attendanceId")
    suspend fun deleteAttendanceById(attendanceId: String)
    
    @Query("SELECT * FROM attendance WHERE userId = :userId ORDER BY date DESC")
    fun getAttendanceByUserId(userId: String): Flow<List<Attendance>>
    
    @Query("SELECT * FROM attendance WHERE date = :date ORDER BY userId")
    fun getAttendanceByDate(date: Date): Flow<List<Attendance>>
    
    @Query("SELECT * FROM attendance WHERE classId = :classId AND date = :date ORDER BY userId")
    fun getAttendanceByClassAndDate(classId: String, date: Date): Flow<List<Attendance>>
    
    @Query("SELECT * FROM attendance WHERE courseId = :courseId ORDER BY date DESC, userId")
    fun getAttendanceByCourse(courseId: String): Flow<List<Attendance>>
    
    @Query("SELECT * FROM attendance WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date")
    suspend fun getAttendanceByUserIdAndDateRange(userId: String, startDate: Date, endDate: Date): List<Attendance>
    
    @Query("SELECT COUNT(*) FROM attendance WHERE userId = :userId AND status = :status AND date BETWEEN :startDate AND :endDate")
    suspend fun countAttendanceByStatus(userId: String, status: String, startDate: Date, endDate: Date): Int
    
    @Query("UPDATE attendance SET status = :status, reason = :reason, remarks = :remarks, updatedAt = :updatedAt WHERE attendanceId = :attendanceId")
    suspend fun updateAttendanceStatus(attendanceId: String, status: String, reason: String?, remarks: String?, updatedAt: Date)
    
    @Query("SELECT * FROM attendance WHERE date = :date AND classId = :classId AND courseId = :courseId")
    fun getSubjectAttendance(date: Date, classId: String, courseId: String): Flow<List<Attendance>>
    
    @Query("SELECT COUNT(*) FROM attendance WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalAttendanceDays(userId: String, startDate: Date, endDate: Date): Int
} 