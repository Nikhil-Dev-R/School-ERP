package com.titanflaws.erp.data.datasource.remote.api

import com.titanflaws.erp.data.model.User
import retrofit2.http.*

/**
 * Retrofit API interface for user related operations
 */
interface UserApi {

    /**
     * Get user by ID
     */
    @GET("users/{userId}")
    suspend fun getUserById(@Path("userId") userId: String): User

    /**
     * Get users by role
     */
    @GET("users")
    suspend fun getUsersByRole(@Query("role") role: String): List<User>

    /**
     * Get all users
     */
    @GET("users")
    suspend fun getAllUsers(): List<User>

    /**
     * Create or update user
     */
    @POST("users")
    suspend fun createUser(@Body user: User): User

    /**
     * Update user
     */
    @PUT("users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: String,
        @Body user: User
    ): User

    /**
     * Update user profile picture
     */
    @PATCH("users/{userId}/profile-picture")
    suspend fun updateProfilePicture(
        @Path("userId") userId: String,
        @Body request: Map<String, String>
    ): User

    /**
     * Delete user
     */
    @DELETE("users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: String)
    
    /**
     * Get current user profile
     */
    @GET("users/me")
    suspend fun getCurrentUser(): User
    
    /**
     * Update current user's FCM token
     */
    @PATCH("users/me/fcm-token")
    suspend fun updateFcmToken(@Body request: Map<String, String>): User
} 