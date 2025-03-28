package com.titanflaws.erp.data.datasource.remote.api

import com.titanflaws.erp.data.model.AuthResponse
import com.titanflaws.erp.data.model.LoginRequest
import com.titanflaws.erp.data.model.PasswordResetRequest
import com.titanflaws.erp.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API interface for authentication operations
 */
interface AuthApi {
    
    /**
     * Login with email and password
     * @param request The login request object
     * @return AuthResponse object on success
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
    
    /**
     * Register a new user
     * @param request The registration request object
     * @return AuthResponse object on success
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
    
    /**
     * Authenticate with a Firebase token
     * @param request The request containing the token and provider
     * @return AuthResponse object on success
     */
    @POST("auth/firebase-token")
    suspend fun authWithFirebaseToken(@Body request: Map<String, String>): AuthResponse
    
    /**
     * Verify OTP for phone authentication
     * @param request The request containing the phone number and OTP
     * @return AuthResponse object on success
     */
    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: Map<String, String>): AuthResponse
    
    /**
     * Reset password
     * @param request The password reset request object
     * @return Map containing status of the request
     */
    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: PasswordResetRequest): Map<String, Boolean>
} 