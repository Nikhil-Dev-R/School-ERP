package com.titanflaws.erp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.titanflaws.erp.data.datasource.local.dao.UserDao
import com.titanflaws.erp.data.model.User
import com.titanflaws.erp.domain.repository.UserRepository
import com.titanflaws.erp.utils.FirebaseConstants
import com.titanflaws.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRepository {

    private val usersCollection = firestore.collection(FirebaseConstants.COLLECTION_USERS)

    // Get the current authenticated user ID
    override fun getCurrentUserId(): String? = auth.currentUser?.uid

    // Get a user from the local database
    override fun getUserById(userId: String): Flow<User?> = userDao.getUserById(userId)

    // Get all users by role
    override fun getUsersByRole(role: String): Flow<List<User>> = userDao.getUsersByRole(role)

    // Get all users from the local database
    override fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    
    // Search users by name
    override fun searchUsersByName(name: String): Flow<List<User>> {
        return userDao.searchUsersByName(name)
    }

    // Create or update a user in both Firestore and local database
    override suspend fun saveUser(user: User): Resource<User> {
        return try {
            // Save to Firestore
            usersCollection.document(user.uid).set(user).await()
            // Save to local database
            userDao.insertUser(user)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save user")
        }
    }
    
    // Update user in both Firestore and local database
    override suspend fun updateUser(user: User): Resource<User> {
        return try {
            // Update in Firestore
            usersCollection.document(user.uid).set(user).await()
            // Update in local database
            userDao.updateUser(user)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update user")
        }
    }
    
    // Delete user from both Firestore and local database
    override suspend fun deleteUser(userId: String): Resource<Boolean> {
        return try {
            // Delete from Firestore
            usersCollection.document(userId).delete().await()
            // Delete from local database
            userDao.deleteUserById(userId)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete user")
        }
    }

    // Update user profile picture
    override suspend fun updateProfilePicture(userId: String, pictureUrl: String): Resource<Boolean> {
        return try {
            // Update in Firestore
            usersCollection.document(userId).update("profilePicUrl", pictureUrl).await()
            // Update in local database
            userDao.updateProfilePic(userId, pictureUrl)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update profile picture")
        }
    }

    // Update user's last login time
    override suspend fun updateLastLogin(userId: String): Resource<Boolean> {
        return try {
            val timestamp = Date().time
            // Update in Firestore
            usersCollection.document(userId).update("lastLogin", Date()).await()
            // Update in local database
            userDao.updateLastLogin(userId, timestamp)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update last login")
        }
    }
    
    // Methods for DataSyncWorker
    
    /**
     * Insert a user to local database
     */
    suspend fun insertUserToLocal(user: User) {
        userDao.insertUser(user)
    }
    
    /**
     * Sync all users from Firestore to local database
     */
    suspend fun syncAllUsers() {
        try {
            val snapshot = usersCollection.get().await()
            val users = snapshot.toObjects(User::class.java)
            userDao.insertUsers(users)
        } catch (e: Exception) {
            // Handle errors
        }
    }
    
    /**
     * Sync a specific user from Firestore to local database
     */
    suspend fun refreshUserData(userId: String) {
        try {
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                user?.let { userDao.insertUser(it) }
            }
        } catch (e: Exception) {
            // Handle errors
        }
    }
} 