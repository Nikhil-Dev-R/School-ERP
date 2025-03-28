package com.titanflaws.erp.domain.usecase

import com.titanflaws.erp.data.model.User
import com.titanflaws.erp.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for getting user profile data with offline support
 */
class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    /**
     * Get user profile data with offline support
     * @param userId The user ID
     * @param forceRefresh Whether to force a refresh from the server
     * @return Flow of User object
     */
    operator fun invoke(userId: String, forceRefresh: Boolean = false): Flow<Result<User>> = flow {
        try {
            // Attempt to get data from local database first
            val localData = userRepository.getUserById(userId).first()
            
            // Emit local data if available
            localData?.let {
                emit(Result.success(it))
            }
            
            // If forceRefresh is true or no local data, fetch from server
            if (forceRefresh || localData == null) {
                userRepository.refreshUserData(userId)
                
                // Emit updated data from local database after refresh
                val refreshedData = userRepository.getUserById(userId).first()
                
                refreshedData?.let {
                    emit(Result.success(it))
                } ?: emit(Result.failure(Exception("User not found")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 