package com.titanflaws.erp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.titanflaws.erp.data.datasource.local.dao.ClassSectionDao
import com.titanflaws.erp.data.datasource.local.dao.StudentDao
import com.titanflaws.erp.data.model.ClassSection
import com.titanflaws.erp.domain.repository.ClassSectionRepository
import com.titanflaws.erp.utils.FirebaseConstants
import com.titanflaws.erp.utils.Resource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassSectionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val classSectionDao: ClassSectionDao,
    private val studentDao: StudentDao
) : ClassSectionRepository {

    private val classSectionsCollection = firestore.collection(FirebaseConstants.COLLECTION_CLASS_SECTIONS)

    override suspend fun getClassSectionById(classSectionId: String): Resource<ClassSection> {
        return try {
            // Check the local database first
            val localClassSection = classSectionDao.getClassSectionById(classSectionId).first()
            
            if (localClassSection != null) {
                Resource.Success(localClassSection)
            } else {
                // Fetch from Firestore if not in local DB
                val document = classSectionsCollection
                    .document(classSectionId)
                    .get()
                    .await()
                
                if (document.exists()) {
                    val classSection = document.toObject(ClassSection::class.java)
                    // Save to local DB
                    classSection?.let { classSectionDao.insertClassSection(it) }
                    Resource.Success(classSection!!)
                } else {
                    Resource.Error("Class section not found")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getAllClassSections(): Resource<List<ClassSection>> {
        return try {
            // Try to get from local database first
            val localClassSections = classSectionDao.getAllClassSections().first()
            
            if (localClassSections.isNotEmpty()) {
                Resource.Success(localClassSections)
            } else {
                // Fetch from Firestore
                val documents = classSectionsCollection
                    .get()
                    .await()
                
                val classSections = documents.toObjects(ClassSection::class.java)
                // Save to local database
                classSectionDao.insertClassSections(classSections)
                Resource.Success(classSections)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getClassSectionsByAcademicYear(academicYearId: String): Resource<List<ClassSection>> {
        return try {
            // Check local database first
            val localClassSections = classSectionDao.getClassSectionsByAcademicYear(academicYearId).first()
            
            if (localClassSections.isNotEmpty()) {
                Resource.Success(localClassSections)
            } else {
                // Fetch from Firestore
                val documents = classSectionsCollection
                    .whereEqualTo("academicYearId", academicYearId)
                    .get()
                    .await()
                
                val classSections = documents.toObjects(ClassSection::class.java)
                classSectionDao.insertClassSections(classSections)
                Resource.Success(classSections)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getClassSectionsByClassName(className: String): Resource<List<ClassSection>> {
        return try {
            // Check local database first
            val localClassSections = classSectionDao.getClassSectionsByClassName(className).first()
            
            if (localClassSections.isNotEmpty()) {
                Resource.Success(localClassSections)
            } else {
                // Fetch from Firestore
                val documents = classSectionsCollection
                    .whereEqualTo("className", className)
                    .get()
                    .await()
                
                val classSections = documents.toObjects(ClassSection::class.java)
                classSectionDao.insertClassSections(classSections)
                Resource.Success(classSections)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getClassSectionsByTeacher(teacherId: String): Resource<List<ClassSection>> {
        return try {
            // Check local database first
            val localClassSections = classSectionDao.getClassSectionsByTeacher(teacherId).first()
            
            if (localClassSections.isNotEmpty()) {
                Resource.Success(localClassSections)
            } else {
                // Fetch from Firestore
                val documents = classSectionsCollection
                    .whereEqualTo("classTeacherId", teacherId)
                    .get()
                    .await()
                
                val classSections = documents.toObjects(ClassSection::class.java)
                classSectionDao.insertClassSections(classSections)
                Resource.Success(classSections)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun createClassSection(classSection: ClassSection): Resource<ClassSection> {
        return try {
            // Create a document reference if ID is not present
            val newClassSection = if (classSection.classSectionId.isBlank()) {
                val docRef = classSectionsCollection.document()
                classSection.copy(classSectionId = docRef.id)
            } else {
                classSection
            }
            
            // Save to Firestore
            classSectionsCollection
                .document(newClassSection.classSectionId)
                .set(newClassSection)
                .await()
            
            // Save to local DB
            classSectionDao.insertClassSection(newClassSection)
            
            Resource.Success(newClassSection)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create class section")
        }
    }

    override suspend fun updateClassSection(classSection: ClassSection): Resource<ClassSection> {
        return try {
            if (classSection.classSectionId.isBlank()) {
                return Resource.Error("Class section ID cannot be null or empty")
            }
            
            // Update in Firestore
            classSectionsCollection
                .document(classSection.classSectionId)
                .set(classSection)
                .await()
            
            // Update in local DB
            classSectionDao.updateClassSection(classSection)
            
            Resource.Success(classSection)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update class section")
        }
    }

    override suspend fun deleteClassSection(classSectionId: String): Resource<Boolean> {
        return try {
            // Delete from Firestore
            classSectionsCollection
                .document(classSectionId)
                .delete()
                .await()
            
            // Delete from local DB
            classSectionDao.deleteClassSectionById(classSectionId)
            
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete class section")
        }
    }

    override suspend fun assignClassTeacher(classSectionId: String, teacherId: String): Resource<Boolean> {
        return try {
            // Get the class section
            val classSection = when (val result = getClassSectionById(classSectionId)) {
                is Resource.Success -> result.data
                is Resource.Error -> return Resource.Error(result.message ?: "Failed to get class section")
                is Resource.Loading -> return Resource.Error("Operation in progress")
            }

            // Update class teacher ID
            val updatedClassSection = classSection?.copy(classTeacherId = teacherId)

            // Update in Firestore
            classSectionsCollection
                .document(classSectionId)
                .update("classTeacherId", teacherId)
                .await()

            // Update in local DB
            if (updatedClassSection != null) {
                classSectionDao.updateClassSection(updatedClassSection)
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to assign class teacher")
        }
    }

    override suspend fun getActiveClassSections(): Resource<List<ClassSection>> {
        return try {
            // Check local database first
            val localClassSections = classSectionDao.getActiveClassSections().first()
            
            if (localClassSections.isNotEmpty()) {
                Resource.Success(localClassSections)
            } else {
                // Fetch from Firestore
                val documents = classSectionsCollection
                    .whereEqualTo("isActive", true)
                    .get()
                    .await()
                
                val classSections = documents.toObjects(ClassSection::class.java)
                classSectionDao.insertClassSections(classSections)
                Resource.Success(classSections)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getStudentCount(classSectionId: String): Resource<Int> {
        return try {
            // Get student count from local DB
            val count = studentDao.getStudentCountByClassSection(classSectionId)
            
            if (count > 0) {
                Resource.Success(count)
            } else {
                // Get count from Firestore
                val snapshot = firestore.collection(FirebaseConstants.COLLECTION_STUDENTS)
                    .whereEqualTo("classId", classSectionId)
                    .get()
                    .await()
                
                Resource.Success(snapshot.size())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get student count")
        }
    }
    
    /**
     * Method for DataSyncWorker to insert a ClassSection into local database
     */
    suspend fun insertClassSectionToLocal(classSection: ClassSection) {
        classSectionDao.insertClassSection(classSection)
    }
    
    /**
     * Sync all class sections from Firestore to local database
     */
    suspend fun syncAllClassSections() {
        try {
            val snapshot = classSectionsCollection.get().await()
            val classSections = snapshot.toObjects(ClassSection::class.java)
            classSectionDao.insertClassSections(classSections)
        } catch (e: Exception) {
            // Handle errors
        }
    }
    
    /**
     * Sync a specific class section from Firestore to local database
     */
    suspend fun syncClassSection(classSectionId: String) {
        try {
            val document = classSectionsCollection.document(classSectionId).get().await()
            if (document.exists()) {
                val classSection = document.toObject(ClassSection::class.java)
                classSection?.let { classSectionDao.insertClassSection(it) }
            }
        } catch (e: Exception) {
            // Handle errors
        }
    }
} 