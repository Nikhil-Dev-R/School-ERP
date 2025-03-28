package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a parent in the system
 * @property parentId Unique identifier for the parent
 * @property userId Firebase Auth user ID
 * @property firstName First name of the parent
 * @property lastName Last name of the parent
 * @property relation Relation to the student (Father, Mother, Guardian)
 * @property occupation Occupation of the parent
 * @property education Education qualification
 * @property annualIncome Annual income
 * @property studentIds List of student IDs linked to this parent
 * @property address Residential address
 * @property phoneNumber Primary phone number
 * @property alternatePhoneNumber Alternate phone number
 * @property email Email address
 * @property documents List of document URLs uploaded for the parent
 * @property isActive Whether the parent is currently active
 * @property registeredAt Registration timestamp
 */
@Entity(
    tableName = "parents",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class Parent(
    @PrimaryKey
    val parentId: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val relation: String,
    val occupation: String?,
    val education: String?,
    val annualIncome: Double?,
    val studentIds: List<String>?,
    val address: String,
    val phoneNumber: String,
    val alternatePhoneNumber: String?,
    val email: String,
    val documents: List<String>?,
    val isActive: Boolean = true,
    val registeredAt: Date = Date()
) 