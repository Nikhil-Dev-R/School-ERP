package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a staff member in the system (non-teaching staff)
 * @property staffId Unique identifier for the staff member
 * @property userId Firebase Auth user ID
 * @property employeeId Employee ID assigned by the school
 * @property firstName First name of the staff member
 * @property lastName Last name of the staff member
 * @property dob Date of birth
 * @property gender Gender
 * @property department Department (Administration, Accounts, Library, etc.)
 * @property designation Job designation
 * @property qualification Educational qualification
 * @property experience Years of work experience
 * @property joiningDate Date when staff joined the school
 * @property salary Current salary
 * @property address Residential address
 * @property contactNumber Contact phone number
 * @property emergencyContact Emergency contact number
 * @property documents List of document URLs uploaded for the staff
 * @property isActive Whether the staff member is currently active
 * @property registeredAt Registration timestamp
 */
@Entity(
    tableName = "staff",
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
        Index("employeeId", unique = true)
    ]
)
data class Staff(
    @PrimaryKey
    val staffId: String,
    val userId: String,
    val employeeId: String,
    val firstName: String,
    val lastName: String,
    val dob: Date,
    val gender: String,
    val department: String,
    val designation: String,
    val qualification: String,
    val experience: Int,
    val joiningDate: Date,
    val salary: Double,
    val address: String,
    val contactNumber: String,
    val emergencyContact: String,
    val documents: List<String>?,
    val isActive: Boolean = true,
    val registeredAt: Date = Date()
) 