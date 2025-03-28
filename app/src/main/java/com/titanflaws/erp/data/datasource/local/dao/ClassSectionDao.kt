package com.titanflaws.erp.data.datasource.local.dao

import androidx.room.*
import com.titanflaws.erp.data.model.ClassSection
import kotlinx.coroutines.flow.Flow

/**
 * DAO for ClassSection entity
 */
@Dao
interface ClassSectionDao {

    /**
     * Insert a single class section
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassSection(classSection: ClassSection)

    /**
     * Insert multiple class sections
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassSections(classSections: List<ClassSection>)

    /**
     * Update a class section
     */
    @Update
    suspend fun updateClassSection(classSection: ClassSection)

    /**
     * Delete a class section
     */
    @Delete
    suspend fun deleteClassSection(classSection: ClassSection)

    /**
     * Delete a class section by ID
     */
    @Query("DELETE FROM class_sections WHERE classSectionId = :classSectionId")
    suspend fun deleteClassSectionById(classSectionId: String)

    /**
     * Get a class section by ID
     */
    @Query("SELECT * FROM class_sections WHERE classSectionId = :classSectionId")
    fun getClassSectionById(classSectionId: String): Flow<ClassSection?>

    /**
     * Get all class sections
     */
    @Query("SELECT * FROM class_sections")
    fun getAllClassSections(): Flow<List<ClassSection>>

    /**
     * Get class sections by academic year
     */
    @Query("SELECT * FROM class_sections WHERE academicYearId = :academicYearId")
    fun getClassSectionsByAcademicYear(academicYearId: String): Flow<List<ClassSection>>

    /**
     * Get class sections by class name
     */
    @Query("SELECT * FROM class_sections WHERE className = :className")
    fun getClassSectionsByClassName(className: String): Flow<List<ClassSection>>

    /**
     * Get class sections by teacher
     */
    @Query("SELECT * FROM class_sections WHERE classTeacherId = :teacherId")
    fun getClassSectionsByTeacher(teacherId: String): Flow<List<ClassSection>>

    /**
     * Get active class sections
     */
    @Query("SELECT * FROM class_sections WHERE isActive = 1")
    fun getActiveClassSections(): Flow<List<ClassSection>>

    /**
     * Count class sections
     */
    @Query("SELECT COUNT(*) FROM class_sections")
    suspend fun getClassSectionCount(): Int

    /**
     * Get class sections by academic year and class name
     */
    @Query("SELECT * FROM class_sections WHERE academicYearId = :academicYearId AND className = :className")
    fun getClassSectionsByYearAndClass(academicYearId: String, className: String): Flow<List<ClassSection>>
} 