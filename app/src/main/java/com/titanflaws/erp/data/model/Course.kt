package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.titanflaws.erp.data.datasource.local.converters.ListConverter
import com.titanflaws.erp.presentation.screens.student.DisplayCourse

/**
 * Data class representing a course in the system
 */
@Entity(tableName = "courses")
@TypeConverters(ListConverter::class)
data class Course(
    @PrimaryKey
    val courseId: String,
    val name: String,
    val code: String,
    val description: String = "",
    val department: String? = null,
    val credits: Int = 0,
    val teacherId: String? = null,
    val classIds: List<String>? = null,
    val status: String = "active", // active, completed, upcoming
    val syllabus: String? = null,
    val academicYear: String? = null,
    val semester: String? = null
)

fun Course.toDisplayCourse() = DisplayCourse(
    id = courseId,
    name = name,
    code = code,
    description = description,
    department = department,
    credits = credits,
    teacherName = teacherId + "id 0 00 ",
    status = status,
)