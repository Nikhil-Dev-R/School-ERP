package com.titanflaws.erp.data.model

/**
 * Data class representing an authentication response
 */
data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val userId: String,
    val role: String,
    val isNewUser: Boolean = false
) 