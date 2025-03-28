package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents an assignment given to students
 * @property assignmentId Unique identifier for the assignment
 * @property title Assignment title
 * @property description Assignment description
 * @property courseId ID of the course/subject
 * @property classIds List of class IDs this assignment is for
 * @property teacherId ID of the teacher who created this assignment
 * @property dueDate Due date for submission
 * @property totalMarks Maximum marks for this assignment
 * @property assignmentType Type of assignment (HOMEWORK, PROJECT, PRESENTATION, etc.)
 * @property attachmentUrls List of attachment URLs
 * @property submissionType How to submit (ONLINE, OFFLINE)
 * @property status Assignment status (DRAFT, PUBLISHED, CLOSED)
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 */
@Entity(
    tableName = "assignments",
    foreignKeys = [
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
        Index("courseId"),
        Index("teacherId")
    ]
)
data class Assignment(
    @PrimaryKey
    val assignmentId: String,
    val title: String,
    val description: String?,
    val courseId: String,
    val classIds: List<String>,
    val teacherId: String,
    val dueDate: Date,
    val totalMarks: Int,
    val assignmentType: String,
    val attachmentUrls: List<String>?,
    val submissionType: String,
    val status: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 