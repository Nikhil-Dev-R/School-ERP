package com.titanflaws.erp.domain.repository

import com.titanflaws.erp.data.model.User
import com.titanflaws.erp.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user operations
 */
interface UserRepository {

    /**
     * Get the current authenticated user ID
     */
    fun getCurrentUserId(): String?

    /**
     * Get a user by ID
     */
    fun getUserById(userId: String): Flow<User?>

    /**
     * Get all users by role
     */
    fun getUsersByRole(role: String): Flow<List<User>>

    /**
     * Get all users
     */
    fun getAllUsers(): Flow<List<User>>
    
    /**
     * Search users by name
     */
    fun searchUsersByName(name: String): Flow<List<User>>

    /**
     * Save a user (create or update)
     */
    suspend fun saveUser(user: User): Resource<User>

    /**
     * Update an existing user
     */
    suspend fun updateUser(user: User): Resource<User>

    /**
     * Delete a user
     */
    suspend fun deleteUser(userId: String): Resource<Boolean>

    /**
     * Update user's profile picture
     */
    suspend fun updateProfilePicture(userId: String, pictureUrl: String): Resource<Boolean>

    /**
     * Update user's last login timestamp
     */
    suspend fun updateLastLogin(userId: String): Resource<Boolean>
} 