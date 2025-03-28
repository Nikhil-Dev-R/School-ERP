package com.titanflaws.erp.domain.repository

import com.titanflaws.erp.data.model.ClassSection
import com.titanflaws.erp.utils.Resource

/**
 * Repository interface for class section operations
 */
interface ClassSectionRepository {
    
    /**
     * Get a class section by ID
     */
    suspend fun getClassSectionById(classSectionId: String): Resource<ClassSection>
    
    /**
     * Get all class sections
     */
    suspend fun getAllClassSections(): Resource<List<ClassSection>>
    
    /**
     * Get class sections by academic year
     */
    suspend fun getClassSectionsByAcademicYear(academicYearId: String): Resource<List<ClassSection>>
    
    /**
     * Get class sections by class name
     */
    suspend fun getClassSectionsByClassName(className: String): Resource<List<ClassSection>>
    
    /**
     * Get class sections by teacher (class teacher)
     */
    suspend fun getClassSectionsByTeacher(teacherId: String): Resource<List<ClassSection>>
    
    /**
     * Create a new class section
     */
    suspend fun createClassSection(classSection: ClassSection): Resource<ClassSection>
    
    /**
     * Update an existing class section
     */
    suspend fun updateClassSection(classSection: ClassSection): Resource<ClassSection>
    
    /**
     * Delete a class section
     */
    suspend fun deleteClassSection(classSectionId: String): Resource<Boolean>
    
    /**
     * Assign a teacher to a class section as class teacher
     */
    suspend fun assignClassTeacher(classSectionId: String, teacherId: String): Resource<Boolean>
    
    /**
     * Get active class sections
     */
    suspend fun getActiveClassSections(): Resource<List<ClassSection>>
    
    /**
     * Get student count for a class section
     */
    suspend fun getStudentCount(classSectionId: String): Resource<Int>
} 