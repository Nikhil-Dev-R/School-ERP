package com.titanflaws.erp.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.titanflaws.erp.presentation.viewmodel.UserRole

/**
 * Repository interface for authentication operations
 */
interface AuthRepository {
    /**
     * Sign in with email and password
     */
    suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser?
    
    /**
     * Register with email and password
     */
    suspend fun registerWithEmailPassword(email: String, password: String, fullName: String, phoneNumber: String, role: String): FirebaseUser?
    
    /**
     * Sign in with Google
     */
    suspend fun signInWithGoogle(idToken: String): FirebaseUser?
    
    /**
     * Sign in with phone
     */
    suspend fun verifyPhoneNumber(phoneNumber: String, onCodeSent: (String) -> Unit, onVerificationFailed: (Exception) -> Unit)
    
    /**
     * Verify OTP code
     */
    suspend fun verifyOtpCode(verificationId: String, code: String): FirebaseUser?
    
    /**
     * Get current user
     */
    fun getCurrentUser(): FirebaseUser?
    
    /**
     * Get user role
     */
    suspend fun getUserRole(userId: String): UserRole
    
    /**
     * Update user role
     */
    suspend fun updateUserRole(userId: String, newRole: String)
    
    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(email: String)
    
    /**
     * Sign out
     */
    fun signOut()
    
    /**
     * Update last login timestamp
     */
    suspend fun updateLastLogin(userId: String)
} 