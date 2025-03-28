package com.titanflaws.erp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titanflaws.erp.data.model.User
import com.titanflaws.erp.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing user profile data
 */
@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    // UI state for the profile screen
    data class UserProfileState(
        val user: User? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UserProfileState(isLoading = true))
    val uiState: StateFlow<UserProfileState> = _uiState

    /**
     * Loads user profile data
     * @param userId The user ID to load
     * @param forceRefresh Whether to force a refresh from the server
     */
    fun loadUserProfile(userId: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            getUserProfileUseCase(userId, forceRefresh)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { user ->
                            _uiState.value = _uiState.value.copy(
                                user = user,
                                isLoading = false,
                                error = null
                            )
                        },
                        onFailure = { e ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = e.message ?: "Unknown error occurred"
                            )
                        }
                    )
                }
        }
    }
    
    /**
     * Refreshes the user profile data from the server
     * @param userId The user ID to refresh
     */
    fun refreshUserProfile(userId: String) {
        loadUserProfile(userId, true)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
} 