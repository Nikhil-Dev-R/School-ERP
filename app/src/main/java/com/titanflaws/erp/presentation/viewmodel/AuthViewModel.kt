package com.titanflaws.erp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.titanflaws.erp.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Sealed class representing the available user roles in the system with role-specific attributes
 */
sealed class UserRole(val value: String) {
    object Admin : UserRole("admin") {
        val canManageUsers = true
        val canManageSettings = true
        val canManageCourses = true
        val canViewAllData = true
    }
    
    object Teacher : UserRole("teacher") {
        val canCreateExams = true
        val canManageGrades = true
        val canMarkAttendance = true
        val canViewStudentDetails = true
        val assignedClasses = mutableListOf<String>()
        val assignedCourses = mutableListOf<String>()
    }
    
    object Student : UserRole("student") {
        val canViewOwnGrades = true
        val canViewOwnAttendance = true
        val classId: String? = null
        val enrolledCourses = mutableListOf<String>()
        val rollNumber: String? = null
    }
    
    object Parent : UserRole("parent") {
        val canViewChildrenData = true
        val canPayFees = true
        val childrenIds = mutableListOf<String>()
    }
    
    object Staff : UserRole("staff") {
        val canManageTransport = true
        val canManageInventory = true
        val canManageHostel = true
        val staffType: String? = null
    }
    
    object Unknown : UserRole("unknown")
    
    companion object {
        fun fromString(role: String?): UserRole {
            return when(role?.lowercase()) {
                "admin" -> Admin
                "teacher" -> Teacher
                "student" -> Student
                "parent" -> Parent
                "staff" -> Staff
                else -> Unknown
            }
        }
    }
}

/**
 * Authentication method types
 */
enum class AuthMethod {
    EMAIL_PASSWORD,
    PHONE_OTP,
    GOOGLE,
    FACEBOOK,
    MICROSOFT,
    APPLE
}

/**
 * ViewModel for authentication operations
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    // UI state for auth screens
    data class AuthState(
        val isLoading: Boolean = false,
        val isLoggedIn: Boolean = false,
        val userId: String? = null,
        val userRole: UserRole = UserRole.Unknown,
        val error: String? = null,
        val message: String? = null,
        val verificationId: String? = null,
        val verificationInProgress: Boolean = false,
        val phoneNumber: String? = null,
        val authMethod: AuthMethod? = null,
        val otpSent: Boolean = false,
        val email: String? = null,
        val resetEmailSent: Boolean = false,
        val isNewUser: Boolean = false
    )

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    init {
        // Check if user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            checkUserRole(currentUser.uid)
        }
    }

    /**
     * Check user role from Firestore
     */
    private fun checkUserRole(userId: String) {
        viewModelScope.launch {
            try {
                val userDoc = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()
                
                if (userDoc.exists()) {
                    val userRole = UserRole.fromString(userDoc.getString("role"))
                    
                    // Load role-specific data if needed
                    when (userRole) {
                        is UserRole.Teacher -> {
                            // Load assigned classes and courses
                            val teacherData = firestore.collection("teacher_details")
                                .document(userId)
                                .get()
                                .await()
                            
                            if (teacherData.exists()) {
                                userRole.assignedClasses.clear()
                                userRole.assignedClasses.addAll(
                                    teacherData.get("assignedClasses") as? List<String> ?: emptyList()
                                )
                                
                                userRole.assignedCourses.clear()
                                userRole.assignedCourses.addAll(
                                    teacherData.get("assignedCourses") as? List<String> ?: emptyList()
                                )
                            }
                        }
                        is UserRole.Student -> {
                            // Load enrolled courses and class info
                            val studentData = firestore.collection("student_details")
                                .document(userId)
                                .get()
                                .await()
                            
                            if (studentData.exists()) {
                                studentData.getString("classId")?.let { classId ->
                                    val fieldValue = studentData.get("enrolledCourses")
                                    if (fieldValue is List<*>) {
                                        userRole.enrolledCourses.clear()
                                        userRole.enrolledCourses.addAll(
                                            fieldValue.filterIsInstance<String>()
                                        )
                                    }
                                }
                            }
                        }
                        is UserRole.Parent -> {
                            // Load children IDs
                            val parentData = firestore.collection("parent_details")
                                .document(userId)
                                .get()
                                .await()
                            
                            if (parentData.exists()) {
                                userRole.childrenIds.clear()
                                userRole.childrenIds.addAll(
                                    parentData.get("childrenIds") as? List<String> ?: emptyList()
                                )
                            }
                        }
                        is UserRole.Staff -> {
                            // Load staff type
                            val staffData = firestore.collection("staff_details")
                                .document(userId)
                                .get()
                                .await()
                            
                            if (staffData.exists()) {
                                staffData.getString("staffType")?.let { staffType ->
                                    // Cannot set staffType directly, so update permissions based on it
                                    if (staffType == "transport") {
                                        userRole.canManageTransport
                                    } else if (staffType == "inventory") {
                                        userRole.canManageInventory
                                    } else if (staffType == "hostel") {
                                        userRole.canManageHostel
                                    } else {

                                    }
                                }
                            }
                        }
                        else -> {
                            // No additional data needed for Admin or Unknown roles
                        }
                    }
                    
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        userId = userId,
                        userRole = userRole
                    )
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        userId = userId,
                        userRole = UserRole.Unknown
                    )
                }
            } catch (e: Exception) {
                // Default to logged in but with unknown role if we can't fetch the role
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    userId = userId,
                    userRole = UserRole.Unknown,
                    error = "Failed to fetch user role: ${e.message}"
                )
            }
        }
    }

    /**
     * Login with email and password
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(
                    isLoading = true, 
                    error = null,
                    authMethod = AuthMethod.EMAIL_PASSWORD,
                    email = email
                )
                
                val result = auth.signInWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    // Update last login timestamp
                    updateLastLogin(user.uid)
                    
                    // Fetch user role
                    checkUserRole(user.uid)
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Login failed. Please try again."
                )
            }
        }
    }

    /**
     * Register a new user with email and password
     */
    fun register(email: String, password: String, fullName: String, phoneNumber: String, role: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(
                    isLoading = true, 
                    error = null,
                    authMethod = AuthMethod.EMAIL_PASSWORD,
                    email = email
                )
                
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let { firebaseUser ->
                    // Create user document in Firestore
                    val userRole = UserRole.fromString(role)
                    val user = User(
                        uid = firebaseUser.uid,
                        email = email,
                        fullName = fullName,
                        phoneNumber = phoneNumber,
                        address = "",
                        role = userRole.value,
                        profilePicUrl = null,
                        isActive = true,
                        lastLogin = Date(),
                        createdAt = Date(),
                        fcmToken = null
                    )
                    
                    // Save user to Firestore
                    firestore.collection("users")
                        .document(firebaseUser.uid)
                        .set(user)
                        .await()
                    
                    // Create role-specific document if needed
                    createRoleSpecificData(firebaseUser.uid, userRole)
                    
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        userId = firebaseUser.uid,
                        userRole = userRole,
                        isNewUser = true
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Registration failed. Please try again."
                )
            }
        }
    }
    
    /**
     * Create role-specific data for a user
     */
    private suspend fun createRoleSpecificData(userId: String, userRole: UserRole) {
        when (userRole) {
            is UserRole.Teacher -> {
                // Create empty teacher details
                firestore.collection("teacher_details")
                    .document(userId)
                    .set(mapOf(
                        "assignedClasses" to emptyList<String>(),
                        "assignedCourses" to emptyList<String>()
                    ))
                    .await()
            }
            is UserRole.Student -> {
                // Create empty student details
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
                // Create empty parent details
                firestore.collection("parent_details")
                    .document(userId)
                    .set(mapOf(
                        "childrenIds" to emptyList<String>()
                    ))
                    .await()
            }
            is UserRole.Staff -> {
                // Create empty staff details
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

    /**
     * Sign in with Google
     * Note: The actual Google sign-in flow needs to be handled in the activity/fragment
     * and the IdToken should be passed to this method
     */
    fun signInWithGoogle(idToken: String?) {
        viewModelScope.launch {
            try {
                if (idToken == null) {
                    _authState.value = _authState.value.copy(
                        error = "Google sign-in process needs to be initiated from the UI layer"
                    )
                    return@launch
                }
                
                _authState.value = _authState.value.copy(
                    isLoading = true, 
                    error = null,
                    authMethod = AuthMethod.GOOGLE
                )
                
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val isNewUser = result.additionalUserInfo?.isNewUser ?: false
                
                result.user?.let { firebaseUser ->
                    handleSocialAuthResult(firebaseUser, isNewUser)
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Google sign-in failed. Please try again."
                )
            }
        }
    }
    
    /**
     * Sign in with Facebook
     */
    fun signInWithFacebook(token: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(
                    isLoading = true, 
                    error = null,
                    authMethod = AuthMethod.FACEBOOK
                )
                
                val credential = FacebookAuthProvider.getCredential(token)
                val result = auth.signInWithCredential(credential).await()
                val isNewUser = result.additionalUserInfo?.isNewUser ?: false
                
                result.user?.let { firebaseUser ->
                    handleSocialAuthResult(firebaseUser, isNewUser)
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Facebook sign-in failed. Please try again."
                )
            }
        }
    }
    
    /**
     * Sign in with Microsoft
     */
    fun signInWithMicrosoft(token: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(
                    isLoading = true, 
                    error = null,
                    authMethod = AuthMethod.MICROSOFT
                )
                
                val credential = OAuthProvider.newCredentialBuilder("microsoft.com").setIdToken(token).build()
//                getCredential("microsoft.com", token, null)
                val result = auth.signInWithCredential(credential).await()
                val isNewUser = result.additionalUserInfo?.isNewUser ?: false
                
                result.user?.let { firebaseUser ->
                    handleSocialAuthResult(firebaseUser, isNewUser)
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Microsoft sign-in failed. Please try again."
                )
            }
        }
    }
    
    /**
     * Sign in with Apple
     */
    fun signInWithApple(token: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(
                    isLoading = true, 
                    error = null,
                    authMethod = AuthMethod.APPLE
                )
                
                val credential = OAuthProvider.newCredentialBuilder("apple.com").setIdToken(token).build()
                val result = auth.signInWithCredential(credential).await()
                val isNewUser = result.additionalUserInfo?.isNewUser ?: false
                
                result.user?.let { firebaseUser ->
                    handleSocialAuthResult(firebaseUser, isNewUser)
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Apple sign-in failed. Please try again."
                )
            }
        }
    }
    
    /**
     * Handle social authentication result (Google, Facebook, Microsoft, Apple)
     */
    private suspend fun handleSocialAuthResult(firebaseUser: FirebaseUser, isNewUser: Boolean) {
        try {
            if (isNewUser) {
                // Create new user in Firestore for first-time social sign-in
                val defaultRole = UserRole.Student
                val user = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    fullName = firebaseUser.displayName ?: "",
                    phoneNumber = firebaseUser.phoneNumber ?: "",
                    address = "",
                    role = defaultRole.value,
                    profilePicUrl = firebaseUser.photoUrl?.toString(),
                    isActive = true,
                    lastLogin = Date(),
                    createdAt = Date(),
                    fcmToken = null
                )
                
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(user)
                    .await()
                    
                // Create role-specific data
                createRoleSpecificData(firebaseUser.uid, defaultRole)
                    
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    userId = firebaseUser.uid,
                    userRole = defaultRole,
                    isNewUser = true
                )
            } else {
                // Update last login for existing user
                updateLastLogin(firebaseUser.uid)
                
                // Fetch user role
                checkUserRole(firebaseUser.uid)
            }
        } catch (e: Exception) {
            _authState.value = _authState.value.copy(
                isLoading = false,
                error = e.message ?: "Failed to process authentication. Please try again."
            )
        }
    }

    /**
     * Initiate phone authentication
     * Sends OTP to the provided phone number
     */
    fun startPhoneAuth(phoneNumber: String) {
        _authState.value = _authState.value.copy(
            isLoading = true,
            error = null,
            verificationInProgress = true,
            phoneNumber = phoneNumber,
            authMethod = AuthMethod.PHONE_OTP
        )
        
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Auto-verification on some devices
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    verificationInProgress = false,
                    error = e.message ?: "Phone verification failed. Please try again."
                )
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    verificationInProgress = false,
                    verificationId = verificationId,
                    otpSent = true,
                    message = "OTP has been sent to $phoneNumber"
                )
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks)
            .build()
        
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /**
     * Verify OTP code entered by user
     */
    fun verifyOtp(otp: String) {
        val verificationId = _authState.value.verificationId
        
        if (verificationId.isNullOrBlank()) {
            _authState.value = _authState.value.copy(
                error = "Verification session expired. Please try again."
            )
            return
        }
        
        _authState.value = _authState.value.copy(isLoading = true, error = null)
        
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            signInWithPhoneAuthCredential(credential)
        } catch (e: Exception) {
            _authState.value = _authState.value.copy(
                isLoading = false,
                error = e.message ?: "Invalid verification code. Please try again."
            )
        }
    }

    /**
     * Sign in with phone credential
     */
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithCredential(credential).await()
                val isNewUser = result.additionalUserInfo?.isNewUser ?: false
                
                result.user?.let { firebaseUser ->
                    if (isNewUser) {
                        // New user, create profile
                        val defaultRole = UserRole.Student
                        val user = User(
                            uid = firebaseUser.uid,
                            email = "",
                            fullName = "",
                            phoneNumber = _authState.value.phoneNumber ?: "",
                            address = "",
                            role = defaultRole.value,
                            profilePicUrl = null,
                            isActive = true,
                            lastLogin = Date(),
                            createdAt = Date(),
                            fcmToken = null
                        )
                        
                        firestore.collection("users")
                            .document(firebaseUser.uid)
                            .set(user)
                            .await()
                            
                        // Create student details
                        createRoleSpecificData(firebaseUser.uid, defaultRole)
                        
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            userId = firebaseUser.uid,
                            userRole = defaultRole,
                            isNewUser = true
                        )
                    } else {
                        // Existing user
                        updateLastLogin(firebaseUser.uid)
                        checkUserRole(firebaseUser.uid)
                    }
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Phone authentication failed. Please try again."
                )
            }
        }
    }

    /**
     * Link phone number to existing account
     */
    fun linkPhoneNumber(phoneNumber: String) {
        val user = auth.currentUser
        
        if (user == null) {
            _authState.value = _authState.value.copy(
                error = "No user is currently signed in."
            )
            return
        }
        
        _authState.value = _authState.value.copy(
            isLoading = true,
            error = null,
            verificationInProgress = true,
            phoneNumber = phoneNumber
        )
        
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                linkPhoneCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    verificationInProgress = false,
                    error = e.message ?: "Phone verification failed. Please try again."
                )
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    verificationInProgress = false,
                    verificationId = verificationId,
                    otpSent = true,
                    message = "OTP has been sent to $phoneNumber"
                )
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks)
            .build()
        
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /**
     * Verify OTP for linking phone number
     */
    fun verifyLinkOtp(otp: String) {
        val verificationId = _authState.value.verificationId
        
        if (verificationId.isNullOrBlank()) {
            _authState.value = _authState.value.copy(
                error = "Verification session expired. Please try again."
            )
            return
        }
        
        _authState.value = _authState.value.copy(isLoading = true, error = null)
        
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            linkPhoneCredential(credential)
        } catch (e: Exception) {
            _authState.value = _authState.value.copy(
                isLoading = false,
                error = e.message ?: "Invalid verification code. Please try again."
            )
        }
    }

    /**
     * Link phone credential to existing account
     */
    private fun linkPhoneCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                
                if (user == null) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = "No user is currently signed in."
                    )
                    return@launch
                }
                
                user.linkWithCredential(credential).await()
                
                // Update phone number in Firestore
                firestore.collection("users")
                    .document(user.uid)
                    .update("phoneNumber", _authState.value.phoneNumber)
                    .await()
                
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    message = "Phone number linked successfully."
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to link phone number. Please try again."
                )
            }
        }
    }

    /**
     * Update user role
     */
    fun updateUserRole(userId: String, newRole: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                
                // Update role in Firestore
                firestore.collection("users")
                    .document(userId)
                    .update("role", newRole)
                    .await()
                
                // Create role-specific data if needed
                val userRole = UserRole.fromString(newRole)
                createRoleSpecificData(userId, userRole)
                
                // Update state if this is the current user
                if (userId == auth.currentUser?.uid) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        userRole = userRole,
                        message = "User role updated successfully."
                    )
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        message = "User role updated successfully."
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update user role. Please try again."
                )
            }
        }
    }

    /**
     * Send password reset email
     */
    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(
                    isLoading = true, 
                    error = null,
                    email = email
                )
                
                auth.sendPasswordResetEmail(email).await()
                
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    resetEmailSent = true,
                    message = "Password reset email sent to $email"
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to send password reset email. Please try again."
                )
            }
        }
    }

    /**
     * Sign out current user
     */
    fun signOut() {
        auth.signOut()
        _authState.value = AuthState()
    }

    /**
     * Clear current error message
     */
    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
    
    /**
     * Clear current success message
     */
    fun clearMessage() {
        _authState.value = _authState.value.copy(message = null)
    }
    
    /**
     * Reset authentication state
     */
    fun resetAuthState() {
        _authState.value = _authState.value.copy(
            verificationId = null,
            verificationInProgress = false,
            otpSent = false,
            resetEmailSent = false,
            message = null,
            error = null
        )
    }

    /**
     * Update the last login timestamp for a user
     */
    private suspend fun updateLastLogin(userId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .update("lastLogin", Date())
                .await()
        } catch (e: Exception) {
            // Quietly fail if we can't update the timestamp
        }
    }
} 