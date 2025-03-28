package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity class for class sections
 */
@Entity(tableName = "class_sections")
data class ClassSection(
    @PrimaryKey
    val classSectionId: String,

    /** Name/number of the class (e.g., "Class 10", "Grade 8") */
    val className: String = "",

    /** Section identifier (e.g., "A", "B", "Science", "Commerce") */
    val sectionName: String = "",

    /** Display name combining class and section (e.g., "10th A", "8th Science") */
    val displayName: String = "",

    /** ID of the academic year this class section belongs to */
    val academicYearId: String = "",

    /** Maximum number of students allowed in this section */
    val capacity: Int = 0,

    /** Current number of students enrolled */
    val currentStudentCount: Int = 0,

    /** ID of the teacher assigned as class teacher */
    val classTeacherId: String? = null,

    /** Whether this class section is currently active */
    val isActive: Boolean = true,

    /** Room number or location where the class is primarily held */
    val roomNumber: String? = null,

    /** Description or notes about the class section */
    val description: String? = null,

    /** Date when this class section was created */
    val createdAt: Date = Date(),

    /** Date when this class section was last updated */
    val updatedAt: Date = Date(),

    /** Time slot for this class section (morning, afternoon, evening) */
    val timeSlot: String? = null,

    /** Start time of classes for this section */
    val startTime: String? = null,

    /** End time of classes for this section */
    val endTime: String? = null
) 