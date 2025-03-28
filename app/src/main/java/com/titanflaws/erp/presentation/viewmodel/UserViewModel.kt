package com.titanflaws.erp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.titanflaws.erp.data.model.Teacher
import com.titanflaws.erp.data.model.User
import com.titanflaws.erp.data.repository.TeacherRepository
import com.titanflaws.erp.data.repository.UserRepository
import com.titanflaws.erp.domain.repository.AuthRepository
import com.titanflaws.erp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val teacherRepository: TeacherRepository
) : ViewModel() {

    // Current user state
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    // User role state
    private val _userRole = MutableStateFlow<UserRole?>(null)
    val userRole: StateFlow<UserRole?> = _userRole

    // User profile state
    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    // User list state (for admin/management)
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    // Operation states
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _success = MutableLiveData<String?>(null)
    val success: LiveData<String?> = _success

    // Initialize with current user
    init {
        getCurrentUser()
    }

    /**
     * Get the current authenticated user
     */
    fun getCurrentUser() {
        viewModelScope.launch {
            val firebaseUser = authRepository.getCurrentUser()
            _currentUser.value = firebaseUser
            
            // Load role and profile if user is logged in
            firebaseUser?.let {
                loadUserRole(it.uid)
                loadUserProfile(it.uid)
            }
        }
    }

    /**
     * Load the user's role
     */
    private fun loadUserRole(userId: String) {
        viewModelScope.launch {
            _userRole.value = authRepository.getUserRole(userId)
        }
    }

    /**
     * Load the user's profile
     */
    private fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            userRepository.getUserById(userId).collectLatest {
                _userProfile.value = it
            }
//            when (val result = userRepository.getUserById(userId)) {
//                is Resource.Success -> {
//                    _userProfile.value = result.data
//                    _isLoading.value = false
//                }
//                is Resource.Error -> {
//                    _error.value = result.message
//                    _isLoading.value = false
//                }
//                is Resource.Loading -> {
//                    _isLoading.value = true
//                }
//            }
            _isLoading.value = false
        }
    }

    /**
     * Sign in with email and password
     */
    fun signInWithEmailPassword(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = authRepository.signInWithEmailPassword(email, password)
                if (user != null) {
                    _currentUser.value = user
                    loadUserRole(user.uid)
                    loadUserProfile(user.uid)
                    _success.value = "Sign in successful"
                } else {
                    _error.value = "Sign in failed"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Register a new user
     */
    fun registerUser(email: String, password: String, fullName: String, phoneNumber: String, role: UserRole) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = authRepository.registerWithEmailPassword(email, password, fullName, phoneNumber, role.toString())
                if (user != null) {
                    _currentUser.value = user
                    _userRole.value = role
                    _success.value = "Registration successful"
                    
                    // Load the newly created user profile
                    loadUserProfile(user.uid)
                } else {
                    _error.value = "Registration failed"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Sign out the current user
     */
    fun signOut() {
        authRepository.signOut()
        _currentUser.value = null
        _userRole.value = null
        _userProfile.value = null
        _success.value = "Signed out successfully"
    }

    /**
     * Load all users (for admin)
     */
    fun loadAllUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.getAllUsers().collectLatest { usersList ->
                    _users.value = usersList
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred while loading users"
                _isLoading.value = false
            }
        }
    }

    /**
     * Load users by role
     */
    fun loadUsersByRole(role: UserRole) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.getUsersByRole(role.toString()).collectLatest { usersList ->
                    _users.value = usersList
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred while loading users by role"
                _isLoading.value = false
            }
        }
    }

    /**
     * Update a user's role
     */
    fun updateUserRole(userId: String, newRole: UserRole) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                authRepository.updateUserRole(userId, newRole.toString())
                _success.value = "User role updated successfully"
                
                // Reload users list
                loadAllUsers()
            } catch (e: Exception) {
                _error.value = "Failed to update role: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Send password reset email
     */
    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                authRepository.sendPasswordResetEmail(email)
                _success.value = "Password reset email sent"
            } catch (e: Exception) {
                _error.value = "Failed to send reset email: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update user profile
     */
    fun updateUserProfile(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
//                val result = userRepository.updateUser(user)
//                when (result) {
//                    is Resource.Success -> {
//                        _userProfile.value = result.data
//                        _success.value = "Profile updated successfully"
//                    }
//                    is Resource.Error -> {
//                        _error.value = result.message
//                    }
//                    is Resource.Loading -> {
//                        // Already set loading state
//                    }
//                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred while updating profile"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Delete a user (admin function)
     */
    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
//                val result = userRepository.deleteUser(userId)
//                when (result) {
//                    is Resource.Success -> {
//                        _success.value = "User deleted successfully"
//                        // Reload the users list
//                        loadAllUsers()
//                    }
//                    is Resource.Error -> {
//                        _error.value = result.message
//                    }
//                    is Resource.Loading -> {
//                        // Already set loading state
//                    }
//                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred while deleting user"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Search users by name
     */
    fun searchUsersByName(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
//                userRepository.searchUsersByName(name).collectLatest { usersList ->
//                    _users.value = usersList
//                }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred while searching"
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Clear success message
     */
    fun clearSuccess() {
        _success.value = null
    }
} 