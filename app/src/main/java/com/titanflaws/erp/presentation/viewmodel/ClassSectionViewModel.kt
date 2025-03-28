package com.titanflaws.erp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titanflaws.erp.data.model.ClassSection
import com.titanflaws.erp.data.repository.ClassSectionRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClassSectionUiState(
    val classSections: List<ClassSection> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ClassSectionViewModel @Inject constructor(
    private val classSectionRepositoryImpl: ClassSectionRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClassSectionUiState())
    val uiState: StateFlow<ClassSectionUiState> = _uiState

    fun loadAllClassSections() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val classSections = classSectionRepositoryImpl.getAllClassSections()
                classSections.data?.let {
                    _uiState.update {
                        it.copy(
                            classSections = it.classSections,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load classes: ${e.message}") }
            }
        }
    }

    fun createClassSection(className: String, section: String, capacity: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
//                classSectionRepositoryImpl.createClassSection(className, section, gradeLevel, capacity)
                loadAllClassSections() // Refresh the list after creating
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to create class: ${e.message}") }
            }
        }
    }

    fun updateClassSection(classId: String, className: String, section: String, capacity: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
//                classSectionRepositoryImpl.updateClassSection(classId, className, section, gradeLevel, capacity)
                loadAllClassSections() // Refresh the list after updating
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to update class: ${e.message}") }
            }
        }
    }

    fun deleteClassSection(classId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                classSectionRepositoryImpl.deleteClassSection(classId)
                loadAllClassSections() // Refresh the list after deleting
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to delete class: ${e.message}") }
            }
        }
    }
}