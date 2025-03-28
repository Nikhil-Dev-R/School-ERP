package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents an attendance record for students/staff
 * @property attendanceId Unique identifier for the attendance record
 * @property userId User ID (can be student, teacher, or staff)
 * @property userType Type of user (STUDENT, TEACHER, STAFF)
 * @property date Date of attendance
 * @property status Attendance status (PRESENT, ABSENT, LATE, HALF_DAY, LEAVE)
 * @property courseId Course ID (if for a specific class/subject)
 * @property classId Class ID
 * @property sectionId Section ID
 * @property markedById ID of the user who marked this attendance
 * @property reason Reason for absence/leave if applicable
 * @property remarks Additional remarks
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 */
@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId"),
        Index("date"),
        Index(value = ["userId", "date", "courseId"], unique = true)
    ]
)
data class Attendance(
    @PrimaryKey
    val attendanceId: String,
    val userId: String,
    val userType: String,
    val date: Date,
    val status: String,
    val courseId: String?,
    val classId: String?,
    val sectionId: String?,
    val markedById: String,
    val reason: String?,
    val remarks: String?,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 