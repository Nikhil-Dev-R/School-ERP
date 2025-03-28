package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.titanflaws.erp.data.datasource.local.converters.ListConverter
import com.titanflaws.erp.data.datasource.local.converters.MapConverter

/**
 * Data class representing a student in the system
 */
@Entity(tableName = "students")
@TypeConverters(ListConverter::class, MapConverter::class)
data class Student(
    @PrimaryKey
    val studentId: String,
    val userId: String,
    val sectionId: String = "A",
    val firstName: String,
    val lastName: String,
    val rollNumber: String? = null,
    val admissionNumber: String? = null,
    val classId: String? = null,
    val parentIds: List<String>? = null,
    val subjects: List<String>? = null,
    val attendancePercentage: Float = 0f,
    val courseProgress: Map<String, Float>? = null, // courseId -> progress percentage
    val currentAddress: String? = null,
    val permanentAddress: String? = null,
    val bloodGroup: String? = null,
    val emergencyContact: String? = null,
    val admissionDate: Long? = null,
    val dob: Long? = null,
    val gender: String? = null,
    val isActive: Boolean = true,
    val category: String? = null, // General, OBC, SC, ST, etc.
    val fees: Map<String, Boolean>? = null, // feeType -> isPaid
    val previousSchool: String? = null,
    val previousClass: String? = null,
    val transferCertificate: String? = null, // Document URL
    val medicalHistory: String? = null,
    val achievements: List<String>? = null,
) 