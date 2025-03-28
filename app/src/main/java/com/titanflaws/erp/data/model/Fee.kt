package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a fee structure or type in the system
 * @property feeId Unique identifier for the fee
 * @property name Fee name (e.g., "Tuition Fee", "Library Fee")
 * @property description Fee description
 * @property amount Fee amount
 * @property frequency Payment frequency (MONTHLY, QUARTERLY, ANNUAL, ONE_TIME)
 * @property dueDay Day of the month when fee is due (for recurring fees)
 * @property academicYearId Academic year ID this fee belongs to
 * @property classIds List of class IDs this fee applies to
 * @property isOptional Whether this fee is optional
 * @property createdBy ID of the user who created this fee
 * @property isActive Whether this fee is currently active
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 */
@Entity(tableName = "fees")
data class Fee(
    @PrimaryKey
    val feeId: String,
    val name: String,
    val description: String?,
    val amount: Double,
    val frequency: String,
    val dueDay: Int?,
    val academicYearId: String,
    val classIds: List<String>?,
    val isOptional: Boolean = false,
    val createdBy: String,
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 