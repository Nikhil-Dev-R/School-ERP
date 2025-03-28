package com.titanflaws.erp.data.repository

import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.titanflaws.erp.data.datasource.remote.api.AuthApi
import com.titanflaws.erp.data.model.*
import com.titanflaws.erp.domain.repository.AuthRepository
import com.titanflaws.erp.presentation.viewmodel.UserRole
import com.titanflaws.erp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Implementation of the AuthRepository interface
 */
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private val usersCollection = firestore.collection("users")
    
    suspend fun login(email: String, password: String): Resource<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            // First try to login with Firebase
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Authentication failed")
            
            // Then get the user role from Firestore
            val userDoc = firestore.collection("users")
                .document(user.uid)
                .get()
                .await()
            
            val role = userDoc.getString("role") ?: "unknown"
            
            // Get Firebase token
            val token = user.getIdToken(false).await().token
                ?: throw Exception("Failed to get authentication token")
            
            // Update last login timestamp
            firestore.collection("users")
                .document(user.uid)
                .update("lastLogin", Date())
                .await()
            
            // Return success with the token and user info
            Resource.Success(
                AuthResponse(
                    token = token,
                    refreshToken = "",  // Firebase doesn't expose refresh tokens directly
                    userId = user.uid,
                    role = role,
                    isNewUser = false
                )
            )
        } catch (e: Exception) {
            // If Firebase auth fails, try with our backend API
            try {
                val request = LoginRequest(email = email, password = password)
                val response = authApi.login(request)
                Resource.Success(response)
            } catch (e: HttpException) {
                Resource.Error(
                    message = "Error ${e.code()}: ${e.message()}",
                )
            } catch (e: IOException) {
                Resource.Error(
                    message = "Couldn't reach server. Check your internet connection."
                )
            } catch (e: Exception) {
                Resource.Error(
                    message = e.message ?: "Login failed. Please try again."
                )
            }
        }
    }
    
    suspend fun register(
        email: String, 
        password: String, 
        fullName: String, 
        phoneNumber: String, 
        role: String
    ): Resource<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            // First try to register with Firebase
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User registration failed")
            
            // Create user document in Firestore
            val userDoc = User(
                uid = user.uid,
                email = email,
                fullName = fullName,
                phoneNumber = phoneNumber,
                address = "",
                role = role,
                profilePicUrl = null,
                isActive = true,
                lastLogin = Date(),
                createdAt = Date(),
                fcmToken = null
            )
            
            // Save user to Firestore
            firestore.collection("users")
                .document(user.uid)
                .set(userDoc)
                .await()
            
            // Create role-specific documents based on user role
            createRoleSpecificData(user.uid, UserRole.fromString(role))
            
            // Get Firebase token
            val token = user.getIdToken(false).await().token
                ?: throw Exception("Failed to get authentication token")
            
            // Return success with the token and user info
            Resource.Success(
                AuthResponse(
                    token = token,
                    refreshToken = "",  // Firebase doesn't expose refresh tokens directly
                    userId = user.uid,
                    role = role,
                    isNewUser = true
                )
            )
        } catch (e: Exception) {
            // If Firebase auth fails, try with our backend API
            try {
                val request = RegisterRequest(
                    email = email,
                    password = password,
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    role = role
                )
                val response = authApi.register(request)
                Resource.Success(response)
            } catch (e: HttpException) {
                Resource.Error(
                    message = "Error ${e.code()}: ${e.message()}",
                )
            } catch (e: IOException) {
                Resource.Error(
                    message = "Couldn't reach server. Check your internet connection."
                )
            } catch (e: Exception) {
                Resource.Error(
                    message = e.message ?: "Registration failed. Please try again."
                )
            }
        }
    }
    
    private suspend fun createRoleSpecificData(userId: String, userRole: UserRole) {
        when (userRole) {
            is UserRole.Teacher -> {
                firestore.collection("teacher_details")
                    .document(userId)
                    .set(mapOf(
                        "assignedClasses" to emptyList<String>(),
                        "assignedCourses" to emptyList<String>()
                    ))
                    .await()
            }
            is UserRole.Student -> {
                firestore.collection("student_details")
                    .document(userId)
                    .set(mapOf(
                        "classId" to null,
                        "rollNumber" to null,
                        "enrolledCourses" to emptyList<String>()
                    ))
                    .await()
            }
            is UserRole.Parent -> {
                firestore.collection("parent_details")
                    .document(userId)
                    .set(mapOf(
                        "childrenIds" to emptyList<String>()
                    ))
                    .await()
            }
            is UserRole.Staff -> {
                firestore.collection("staff_details")
                    .document(userId)
                    .set(mapOf(
                        "staffType" to null
                    ))
                    .await()
            }
            else -> {
                // No additional details needed for admin
            }
        }
    }

    suspend fun signInWithGoogle(idToken: String): Resource<AuthResponse> = withContext(Dispatchers.IO) {
        return@withContext signInWithSocialProvider(idToken, "google")
    }

    suspend fun signInWithFacebook(token: String): Resource<AuthResponse> = withContext(Dispatchers.IO) {
        return@withContext signInWithSocialProvider(token, "facebook")
    }
    
    suspend fun signInWithMicrosoft(token: String): Resource<AuthResponse> = withContext(Dispatchers.IO) {
        return@withContext signInWithSocialProvider(token, "microsoft.com")
    }
    
    suspend fun signInWithApple(token: String): Resource<AuthResponse> = withContext(Dispatchers.IO) {
        return@withContext signInWithSocialProvider(token, "apple.com")
    }
    
    private suspend fun signInWithSocialProvider(token: String, provider: String): Resource<AuthResponse> {
        try {
            // Create the appropriate credential based on provider
            val credential = when (provider) {
                "google" -> GoogleAuthProvider.getCredential(token, null)
                "facebook" -> FacebookAuthProvider.getCredential(token)
//                "microsoft.com" -> OAuthProvider.getCredential(provider, token, null)
//                "apple.com" -> OAuthProvider.getCredential(provider, token, null)
                else -> throw Exception("Unsupported provider: $provider")
            }
            
            // Sign in with credential
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user ?: throw Exception("Authentication failed")
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            
            // Handle new user registration
            if (isNewUser) {
                // Create default user document for new social auth user
                val userDoc = User(
                    uid = user.uid,
                    email = user.email ?: "",
                    fullName = user.displayName ?: "",
                    phoneNumber = user.phoneNumber ?: "",
                    address = "",
                    role = UserRole.Student.value, // Default role for social auth users
                    profilePicUrl = user.photoUrl?.toString(),
                    isActive = true,
                    lastLogin = Date(),
                    createdAt = Date(),
                    fcmToken = null
                )
                
                // Save user to Firestore
                firestore.collection("users")
                    .document(user.uid)
                    .set(userDoc)
                    .await()
                
                // Create student details by default
                createRoleSpecificData(user.uid, UserRole.Student)
            } else {
                // Update last login timestamp for existing user
                firestore.collection("users")
                    .document(user.uid)
                    .update("lastLogin", Date())
                    .await()
            }
            
            // Get role from Firestore
            val userDoc = firestore.collection("users")
                .document(user.uid)
                .get()
                .await()
            
            val role = userDoc.getString("role") ?: "student"
            
            // Get Firebase token
            val idToken = user.getIdToken(false).await().token
                ?: throw Exception("Failed to get authentication token")
            
            // Return success with the token and user info
            return Resource.Success(
                AuthResponse(
                    token = idToken,
                    refreshToken = "",
                    userId = user.uid,
                    role = role,
                    isNewUser = isNewUser
                )
            )
        } catch (e: Exception) {
            // If Firebase auth fails, try with our API
            return try {
                val request = mapOf(
                    "token" to token,
                    "provider" to provider
                )
                val response = authApi.authWithFirebaseToken(request)
                Resource.Success(response)
            } catch (e: HttpException) {
                Resource.Error(
                    message = "Error ${e.code()}: ${e.message()}",
                )
            } catch (e: IOException) {
                Resource.Error(
                    message = "Couldn't reach server. Check your internet connection."
                )
            } catch (e: Exception) {
                Resource.Error(
                    message = e.message ?: "Social authentication failed. Please try again."
                )
            }
        }
    }
    
    suspend fun startPhoneAuth(phoneNumber: String): Resource<String> = suspendCancellableCoroutine { continuation ->
        try {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // This will not be called in most cases as we're using manual verification
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    continuation.resume(
                        Resource.Error(
                            message = e.message ?: "Phone verification failed. Please try again."
                        )
                    )
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    continuation.resume(
                        Resource.Success(verificationId)
                    )
                }
            }

            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callbacks)
                .build()
            
            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            continuation.resume(
                Resource.Error(
                    message = e.message ?: "Failed to start phone verification. Please try again."
                )
            )
        }
    }
    
    suspend fun verifyOtp(phoneNumber: String, otp: String): Resource<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            // First try direct OTP verification with our API
            val request = mapOf(
                "phoneNumber" to phoneNumber,
                "otp" to otp
            )
            
            try {
                val response = authApi.verifyOtp(request)
                return@withContext Resource.Success(response)
            } catch (e: Exception) {
                // If API verification fails, fall back to Firebase verification
                // This requires the verificationId that we don't have here, so we'll return an error
                return@withContext Resource.Error(
                    message = "Direct OTP verification failed. Firebase verification requires a verification ID from startPhoneAuth."
                )
            }
        } catch (e: Exception) {
            return@withContext Resource.Error(
                message = e.message ?: "OTP verification failed. Please try again."
            )
        }
    }
    
    suspend fun linkPhoneNumber(phoneNumber: String): Resource<String> = suspendCancellableCoroutine { continuation ->
        try {
            val user = firebaseAuth.currentUser
            
            if (user == null) {
                continuation.resume(
                    Resource.Error(
                        message = "No user is currently signed in."
                    )
                )
                return@suspendCancellableCoroutine
            }
            
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // This will not be called in most cases as we're using manual verification
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    continuation.resume(
                        Resource.Error(
                            message = e.message ?: "Phone verification failed. Please try again."
                        )
                    )
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    continuation.resume(
                        Resource.Success(verificationId)
                    )
                }
            }

            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callbacks)
                .build()
            
            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            continuation.resume(
                Resource.Error(
                    message = e.message ?: "Failed to start phone verification. Please try again."
                )
            )
        }
    }
    
    suspend fun verifyLinkOtp(otp: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        // In a real implementation, we would use the stored verificationId and OTP to get the credential
        // Since we don't have the verificationId here, this is a placeholder
        return@withContext Resource.Error(
            message = "Direct OTP verification requires verificationId. Please use a complete implementation."
        )
    }
    
    suspend fun updateUserRole(userId: String, newRole: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Update role in Firestore
            firestore.collection("users")
                .document(userId)
                .update("role", newRole)
                .await()
            
            // Create role-specific data if needed
            createRoleSpecificData(userId, UserRole.fromString(newRole))
            
            return@withContext Resource.Success(true)
        } catch (e: Exception) {
            return@withContext Resource.Error(
                message = e.message ?: "Failed to update user role. Please try again."
            )
        }
    }

    suspend fun resetPassword(email: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Send password reset email via Firebase
            firebaseAuth.sendPasswordResetEmail(email).await()
            
            return@withContext Resource.Success(true)
        } catch (e: Exception) {
            try {
                // If Firebase fails, try with our API
                val request = PasswordResetRequest(email = email)
                val response = authApi.resetPassword(request)
                
                return@withContext if (response["success"] == true) {
                    Resource.Success(true)
                } else {
                    Resource.Error(
                        message = "Failed to send password reset email. Please try again."
                    )
                }
            } catch (e: HttpException) {
                return@withContext Resource.Error(
                    message = "Error ${e.code()}: ${e.message()}",
                )
            } catch (e: IOException) {
                return@withContext Resource.Error(
                    message = "Couldn't reach server. Check your internet connection."
                )
            } catch (e: Exception) {
                return@withContext Resource.Error(
                    message = e.message ?: "Failed to send password reset email. Please try again."
                )
            }
        }
    }
    
    suspend fun checkAuthState(): Resource<AuthResponse?> = withContext(Dispatchers.IO) {
        val currentUser = firebaseAuth.currentUser
        
        if (currentUser == null) {
            return@withContext Resource.Success(null)
        }
        
        try {
            // Get user role from Firestore
            val userDoc = firestore.collection("users")
                .document(currentUser.uid)
                .get()
                .await()
            
            val role = userDoc.getString("role") ?: "unknown"
            
            // Get Firebase token
            val token = currentUser.getIdToken(false).await().token
                ?: throw Exception("Failed to get authentication token")
            
            return@withContext Resource.Success(
                AuthResponse(
                    token = token,
                    refreshToken = "",
                    userId = currentUser.uid,
                    role = role,
                    isNewUser = false
                )
            )
        } catch (e: Exception) {
            return@withContext Resource.Error(
                message = e.message ?: "Failed to check authentication state. Please try again."
            )
        }
    }
    
    suspend fun signOut(): Resource<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Sign out from Firebase
            firebaseAuth.signOut()
            
            return@withContext Resource.Success(true)
        } catch (e: Exception) {
            return@withContext Resource.Error(
                message = e.message ?: "Failed to sign out. Please try again."
            )
        }
    }
} 