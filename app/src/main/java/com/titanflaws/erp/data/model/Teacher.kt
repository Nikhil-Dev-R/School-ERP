package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.titanflaws.erp.data.datasource.local.converters.ListConverter
import com.titanflaws.erp.data.datasource.local.converters.MapConverter

/**
 * Data class representing a teacher in the system
 */
@Entity(tableName = "teachers")
@TypeConverters(ListConverter::class, MapConverter::class)
data class Teacher(
    @PrimaryKey
    val teacherId: String,
    val userId: String,
    val employeeId: String,
    val department: String? = null,
    val designation: String? = null,
    val qualifications: List<String>? = null,
    val specialization: String? = null,
    val classesTaught: List<String>? = null,
    val subjectsTaught: List<String>? = null,
    val joinDate: Long? = null,
    val joiningDate: Long? = null,
    val employmentType: String? = null, // Full-time, Part-time, Contract
    val salary: Double? = null,
    val isActive: Boolean = true,
    val experience: Int? = null, // Years of experience
    val schedule: Map<String, List<String>>? = null // Day of week -> list of periods
) 