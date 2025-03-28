package com.titanflaws.erp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.titanflaws.erp.data.datasource.local.dao.UserDao
import com.titanflaws.erp.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repository for managing User data from both Firestore and local Room database
 */
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val usersCollection = firestore.collection("users")
    
    // Get the current authenticated user ID
    fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    // Get a user from the local database
    fun getUserById(userId: String): Flow<User?> = userDao.getUserById(userId)
    
    // Get all users by role
    fun getUsersByRole(role: String): Flow<List<User>> = userDao.getUsersByRole(role)
    
    // Get all users from the local database
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    
    // Create or update a user in both Firestore and local database
    suspend fun saveUser(user: User) {
        // Save to Firestore
        usersCollection.document(user.uid).set(user).await()
        // Save to local database
        userDao.insertUser(user)
    }
    
    // Update user profile picture
    suspend fun updateProfilePicture(userId: String, pictureUrl: String) {
        // Update in Firestore
        usersCollection.document(userId).update("profilePicUrl", pictureUrl).await()
        // Update in local database
        userDao.updateProfilePic(userId, pictureUrl)
    }
    
    // Fetch fresh user data from Firestore and update local database
    suspend fun refreshUserData(userId: String) {
        try {
            val userDoc = usersCollection.document(userId).get().await()
            userDoc.toObject(User::class.java)?.let { user ->
                userDao.insertUser(user)
            }
        } catch (e: Exception) {
            // Handle errors here
        }
    }
    
    // Fetch all users from Firestore and update local database
    suspend fun syncAllUsers() {
        try {
            val usersSnapshot = usersCollection.get().await()
            val users = usersSnapshot.toObjects(User::class.java)
            userDao.insertUsers(users)
        } catch (e: Exception) {
            // Handle errors here
        }
    }
    
    // Update user's active status
    suspend fun updateUserActiveStatus(userId: String, isActive: Boolean) {
        // Update in Firestore
        usersCollection.document(userId).update("isActive", isActive).await()
        // Update in local database
        userDao.updateActiveStatus(userId, isActive)
    }
    
    // Update user's FCM token
    suspend fun updateFcmToken(userId: String, token: String) {
        // Update in Firestore
        usersCollection.document(userId).update("fcmToken", token).await()
        // Update in local database
        userDao.updateFcmToken(userId, token)
    }
    
    // Delete a user
    suspend fun deleteUser(userId: String) {
        // Delete from Firestore
        usersCollection.document(userId).delete().await()
        // Delete from local database
        userDao.deleteUserById(userId)
    }
    
    // Get user counts by role
    suspend fun getUserCountByRole(role: String): Int = userDao.countUsersByRole(role)
    
    // Get total user count
    suspend fun getTotalUserCount(): Int = userDao.countUsers()
} 