package com.titanflaws.erp.data.datasource.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.titanflaws.erp.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the User entity
 */
@Dao
interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("DELETE FROM users WHERE uid = :userId")
    suspend fun deleteUserById(userId: String)
    
    @Query("SELECT * FROM users WHERE uid = :userId")
    fun getUserById(userId: String): Flow<User?>
    
    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: String): Flow<List<User>>
    
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
    
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
    
    @Query("SELECT * FROM users WHERE fullName LIKE '%' || :name || '%'")
    fun searchUsersByName(name: String): Flow<List<User>>
    
    @Query("UPDATE users SET fcmToken = :token WHERE uid = :userId")
    suspend fun updateFcmToken(userId: String, token: String)
    
    @Query("UPDATE users SET profilePicUrl = :profilePicUrl WHERE uid = :userId")
    suspend fun updateProfilePic(userId: String, profilePicUrl: String)
    
    @Query("UPDATE users SET lastLogin = :timestamp WHERE uid = :userId")
    suspend fun updateLastLogin(userId: String, timestamp: Long)
    
    @Query("UPDATE users SET isActive = :isActive WHERE uid = :userId")
    suspend fun updateActiveStatus(userId: String, isActive: Boolean)
    
    @Query("SELECT COUNT(*) FROM users WHERE role = :role")
    suspend fun countUsersByRole(role: String): Int
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun countUsers(): Int
} 