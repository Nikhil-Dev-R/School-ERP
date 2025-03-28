package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.titanflaws.erp.data.datasource.local.converters.DateConverter
import com.titanflaws.erp.data.datasource.local.converters.ListConverter
import com.titanflaws.erp.data.datasource.local.converters.MapConverter
import java.util.Date

/**
 * Entity representing an exam
 */
@Entity(tableName = "exams")
@TypeConverters(DateConverter::class, ListConverter::class, MapConverter::class)
data class Exam(
    @PrimaryKey
    val examId: String,
    val classId: String,
    val title: String,
    val description: String? = null,
    val examType: String, // MIDTERM, FINAL, QUIZ, etc.
    val courseId: String,
    val courseName: String? = null,
    val startDate: Date,
    val endDate: Date? = null,
    val duration: Int? = null, // in minutes
    val totalMarks: Int,
    val passingMarks: Int,
    val gradeScale: Map<String, Float>? = null, // e.g. {"A+": 90.0, "A": 80.0, ...}
    val isPublished: Boolean = false,
    val status: String = "SCHEDULED", // SCHEDULED, ONGOING, COMPLETED, CANCELLED
    val createdBy: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 