package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a timetable entry for classes
 * @property timeTableId Unique identifier for the timetable entry
 * @property classSectionId ID of the class section
 * @property courseId ID of the course/subject
 * @property teacherId ID of the teacher
 * @property dayOfWeek Day of the week (1-7, Monday-Sunday)
 * @property startTime Start time of the period (stored as minutes from midnight)
 * @property endTime End time of the period (stored as minutes from midnight)
 * @property roomNumber Room number where the class is held
 * @property isActive Whether this timetable entry is active
 * @property academicYearId Academic year ID this timetable entry belongs to
 * @property createdBy ID of the user who created this entry
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 */
@Entity(
    tableName = "timetable",
    foreignKeys = [
        ForeignKey(
            entity = ClassSection::class,
            parentColumns = ["classSectionId"],
            childColumns = ["classSectionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Course::class,
            parentColumns = ["courseId"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Teacher::class,
            parentColumns = ["teacherId"],
            childColumns = ["teacherId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("classSectionId"),
        Index("courseId"),
        Index("teacherId"),
        Index(value = ["classSectionId", "dayOfWeek", "startTime"], unique = true)
    ]
)
data class TimeTable(
    @PrimaryKey
    val timeTableId: String,
    val classSectionId: String,
    val courseId: String,
    val teacherId: String,
    val dayOfWeek: Int,
    val startTime: Int,
    val endTime: Int,
    val roomNumber: String?,
    val isActive: Boolean = true,
    val academicYearId: String,
    val createdBy: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 