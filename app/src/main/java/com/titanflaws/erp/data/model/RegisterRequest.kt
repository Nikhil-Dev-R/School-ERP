package com.titanflaws.erp.data.model

/**
 * Data class representing a registration request
 */
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String,
    val role: String
) 