package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents an exam result for a student
 */
@Entity(
    tableName = "exam_results"
)
data class ExamResult(
    @PrimaryKey val resultId: String,
    val examId: String,
    val studentId: String,
    val marksObtained: Float,
    val percentage: Float,
    val status: String, // PASS, FAIL, ABSENT, INCOMPLETE
    val grade: String? = null,
    val courseId: String? = null,
    val courseName: String? = null,
    val remarks: String? = null,
    val evaluatedBy: String? = null,
    val evaluatedAt: Date? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 