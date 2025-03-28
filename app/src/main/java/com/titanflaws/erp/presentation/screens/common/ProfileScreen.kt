package com.titanflaws.erp.presentation.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.titanflaws.erp.presentation.components.common.ErrorMessage
import com.titanflaws.erp.presentation.components.common.LoadingIndicator
import com.titanflaws.erp.presentation.viewmodel.UserProfileViewModel
import com.titanflaws.erp.presentation.viewmodel.UserRole

/**
 * Generic profile screen for all user roles
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel(),
    userId: String = ""
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showEditDialog by remember { mutableStateOf(false) }
    
    // Load profile data when the screen is first displayed
    LaunchedEffect(key1 = true) {
        viewModel.loadUserProfile(userId = userId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Error message
            uiState.error?.let { message ->
                ErrorMessage(
                    message = message,
                    onDismiss = {
                        viewModel.clearError()
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (uiState.isLoading) {
                LoadingIndicator("Loading profile...")
            } else {
                // Profile header with photo
                ProfileHeader(
                    fullName = uiState.user?.fullName ?: "",
                    role = uiState.user?.role ?: "",
                    photoUrl = uiState.user?.profilePicUrl,
                    onChangePhoto = {
                        // Handle photo change
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Profile information
                ProfileInfoSection(
                    email = uiState.user?.email ?: "",
                    phoneNumber = uiState.user?.phoneNumber ?: "",
                    address = uiState.user?.address ?: ""
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Role-specific information
                when (val userRole = UserRole.fromString( uiState.user?.role )) {
                    is UserRole.Teacher -> {
                        TeacherInfoSection(
                            assignedClasses = userRole.assignedClasses,
                            assignedCourses = userRole.assignedCourses
                        )
                    }
                    
                    is UserRole.Student -> {
                        StudentInfoSection(
                            classId = userRole.classId ?: "Not assigned",
                            rollNumber = userRole.rollNumber ?: "Not assigned",
                            enrolledCourses = userRole.enrolledCourses
                        )
                    }
                    
                    is UserRole.Parent -> {
                        ParentInfoSection(
                            childrenIds = userRole.childrenIds
                        )
                    }
                    
                    is UserRole.Staff -> {
                        StaffInfoSection(
                            staffType = userRole.staffType ?: "General"
                        )
                    }
                    
                    else -> {
                        // No additional info for Admin or Unknown
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Account actions
                AccountActionsSection(
                    onChangePassword = {
                        // Handle password change
                    },
                    onLogout = {
                        // Handle logout
                    }
                )
            }
        }
    }
    
    // Edit profile dialog
    if (showEditDialog) {
        EditProfileDialog(
            fullName = uiState.user?.fullName ?: "",
            phoneNumber = uiState.user?.phoneNumber ?: "",
            address = uiState.user?.address ?: "",
            onDismiss = { showEditDialog = false },
            onSave = { name, phone, address ->
//                viewModel.updateUserProfile(name, phone, address)
                showEditDialog = false
            }
        )
    }
}

/**
 * Profile header section with photo
 */
@Composable
fun ProfileHeader(
    fullName: String,
    role: String,
    photoUrl: String?,
    onChangePhoto: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                // Profile photo
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUrl ?: "https://via.placeholder.com/150")
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                
                // Change photo button
                FilledIconButton(
                    onClick = onChangePhoto,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // User name
            Text(
                text = fullName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            // User role
            Text(
                text = roleDisplayName(role),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Profile information section
 */
@Composable
fun ProfileInfoSection(
    email: String,
    phoneNumber: String,
    address: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                title = "Personal Information",
                icon = Icons.Default.Info
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ProfileInfoRow(
                icon = Icons.Default.Email,
                label = "Email",
                value = email
            )
            
            ProfileInfoRow(
                icon = Icons.Default.Phone,
                label = "Phone",
                value = phoneNumber.ifEmpty { "Not provided" }
            )
            
            ProfileInfoRow(
                icon = Icons.Default.Home,
                label = "Address",
                value = address.ifEmpty { "Not provided" }
            )
        }
    }
}

/**
 * Teacher-specific information section
 */
@Composable
fun TeacherInfoSection(
    assignedClasses: List<String>,
    assignedCourses: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                title = "Teacher Information",
                icon = Icons.Default.School
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ProfileInfoRow(
                icon = Icons.Default.Class,
                label = "Classes",
                value = if (assignedClasses.isEmpty()) "None assigned" else assignedClasses.joinToString(", ")
            )
            
            ProfileInfoRow(
                icon = Icons.Default.Book,
                label = "Courses",
                value = if (assignedCourses.isEmpty()) "None assigned" else assignedCourses.joinToString(", ")
            )
        }
    }
}

/**
 * Student-specific information section
 */
@Composable
fun StudentInfoSection(
    classId: String,
    rollNumber: String,
    enrolledCourses: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                title = "Student Information",
                icon = Icons.Default.School
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ProfileInfoRow(
                icon = Icons.Default.Class,
                label = "Class",
                value = classId
            )
            
            ProfileInfoRow(
                icon = Icons.Default.Badge,
                label = "Roll Number",
                value = rollNumber
            )
            
            ProfileInfoRow(
                icon = Icons.Default.Book,
                label = "Courses",
                value = if (enrolledCourses.isEmpty()) "None enrolled" else enrolledCourses.joinToString(", ")
            )
        }
    }
}

/**
 * Parent-specific information section
 */
@Composable
fun ParentInfoSection(
    childrenIds: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                title = "Parent Information",
                icon = Icons.Default.People
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ProfileInfoRow(
                icon = Icons.Default.Person,
                label = "Children",
                value = if (childrenIds.isEmpty()) "No children linked" else "Children: ${childrenIds.size}"
            )
        }
    }
}

/**
 * Staff-specific information section
 */
@Composable
fun StaffInfoSection(
    staffType: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                title = "Staff Information",
                icon = Icons.Default.Work
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ProfileInfoRow(
                icon = Icons.Default.Business,
                label = "Staff Type",
                value = staffType
            )
        }
    }
}

/**
 * Account actions section
 */
@Composable
fun AccountActionsSection(
    onChangePassword: () -> Unit,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                title = "Account",
                icon = Icons.Default.AccountCircle
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Change password button
            OutlinedButton(
                onClick = onChangePassword,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Change Password")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Logout button
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out")
            }
        }
    }
}

/**
 * Section header with icon
 */
@Composable
fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Profile information row
 */
@Composable
fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Edit profile dialog
 */
@Composable
fun EditProfileDialog(
    fullName: String,
    phoneNumber: String,
    address: String,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, address: String) -> Unit
) {
    var nameInput by remember { mutableStateOf(fullName) }
    var phoneInput by remember { mutableStateOf(phoneNumber) }
    var addressInput by remember { mutableStateOf(address) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = addressInput,
                    onValueChange = { addressInput = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(nameInput, phoneInput, addressInput) }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Helper function to get display name for role
 */
fun roleDisplayName(role: String): String {
    return when (role.lowercase()) {
        "admin" -> "Administrator"
        "teacher" -> "Teacher"
        "student" -> "Student"
        "parent" -> "Parent"
        "staff" -> "Staff Member"
        else -> "User"
    }
} 