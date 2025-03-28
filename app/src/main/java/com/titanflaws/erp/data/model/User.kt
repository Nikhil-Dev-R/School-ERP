package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Data class representing a user in the system
 */
@Entity(
    tableName = "users"
)
data class User(
    @PrimaryKey val uid: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String = "",
    val address: String = "",
    val role: String,
    val profilePicUrl: String? = null,
    val isActive: Boolean = true,
    val lastLogin: Date? = null,
    val createdAt: Date = Date(),
    val fcmToken: String? = null
)