package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a book issue/borrow record
 * @property issueId Unique identifier for the issue record
 * @property bookId ID of the book
 * @property userId ID of the user who borrowed the book
 * @property userType Type of user (STUDENT, TEACHER, STAFF)
 * @property issueDate Date when the book was issued
 * @property dueDate Expected return date
 * @property returnDate Actual return date (null if not returned yet)
 * @property status Status of the issue (ISSUED, RETURNED, OVERDUE, LOST)
 * @property fine Fine amount for late return or lost book
 * @property fineStatus Status of the fine (PENDING, PAID, WAIVED)
 * @property issuedBy ID of the staff who issued the book
 * @property receivedBy ID of the staff who received the returned book
 * @property remarks Additional remarks
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 */
@Entity(
    tableName = "book_issues",
    foreignKeys = [
        ForeignKey(
            entity = Library::class,
            parentColumns = ["bookId"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("bookId"),
        Index("userId"),
        Index(value = ["bookId", "userId", "status"], unique = true)
    ]
)
data class BookIssue(
    @PrimaryKey
    val issueId: String,
    val bookId: String,
    val userId: String,
    val userType: String,
    val issueDate: Date,
    val dueDate: Date,
    val returnDate: Date?,
    val status: String,
    val fine: Double = 0.0,
    val fineStatus: String?,
    val issuedBy: String,
    val receivedBy: String?,
    val remarks: String?,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 