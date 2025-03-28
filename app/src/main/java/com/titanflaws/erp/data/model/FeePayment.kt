package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a fee payment made by a student
 * @property paymentId Unique identifier for the payment
 * @property studentId ID of the student
 * @property feeId ID of the fee type
 * @property invoiceNumber Invoice number
 * @property amount Amount paid
 * @property paymentDate Date of payment
 * @property dueDate Due date for the payment
 * @property paymentMethod Payment method (CASH, ONLINE, CHEQUE, etc.)
 * @property transactionId Transaction ID for online payments
 * @property receiptNumber Receipt number generated
 * @property status Payment status (PENDING, PAID, FAILED, REFUNDED)
 * @property paymentDetails Additional payment details (JSON string)
 * @property period Period for which payment is made (e.g., "April 2023")
 * @property lateFee Late fee charged, if any
 * @property discount Discount applied, if any
 * @property collectedBy ID of the user who collected the payment
 * @property remarks Additional remarks
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 */
@Entity(
    tableName = "fee_payments",
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["studentId"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Fee::class,
            parentColumns = ["feeId"],
            childColumns = ["feeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("studentId"),
        Index("feeId"),
        Index("invoiceNumber", unique = true),
        Index("receiptNumber", unique = true)
    ]
)
data class FeePayment(
    @PrimaryKey
    val paymentId: String,
    val studentId: String,
    val feeId: String,
    val invoiceNumber: String,
    val amount: Double,
    val paymentDate: Date?,
    val dueDate: Date,
    val paymentMethod: String?,
    val transactionId: String?,
    val receiptNumber: String?,
    val status: String,
    val paymentDetails: String?, // JSON string
    val period: String?,
    val lateFee: Double = 0.0,
    val discount: Double = 0.0,
    val collectedBy: String?,
    val remarks: String?,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 