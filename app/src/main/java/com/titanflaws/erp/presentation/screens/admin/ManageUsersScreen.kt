package com.titanflaws.erp.presentation.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.titanflaws.erp.data.model.User
import com.titanflaws.erp.presentation.viewmodel.UserRole
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen for managing users in the system
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf<String?>(null) }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    
    // Mockup users list - Replace with actual data from ViewModel
    val users = remember {
        listOf(
            User(
                uid = "1",
                email = "admin@school.com",
                fullName = "Admin User",
                phoneNumber = "1234567890",
                role = "admin",
                createdAt = Date(),
                lastLogin = Date()
            ),
            User(
                uid = "2",
                email = "teacher1@school.com",
                fullName = "John Teacher",
                phoneNumber = "2345678901",
                role = "teacher",
                createdAt = Date(),
                lastLogin = Date()
            ),
            User(
                uid = "3",
                email = "student1@school.com",
                fullName = "Jane Student",
                phoneNumber = "3456789012",
                role = "student",
                createdAt = Date(),
                lastLogin = Date()
            ),
            User(
                uid = "4",
                email = "parent1@gmail.com",
                fullName = "Bob Parent",
                phoneNumber = "4567890123",
                role = "parent",
                createdAt = Date(),
                lastLogin = Date()
            )
        )
    }
    
    // Filter users based on search query and role filter
    val filteredUsers = users.filter { user ->
        val matchesSearch = if (searchQuery.isNotEmpty()) {
            user.fullName.contains(searchQuery, ignoreCase = true) ||
            user.email.contains(searchQuery, ignoreCase = true)
        } else true
        
        val matchesRole = if (selectedRoleFilter != null) {
            user.role == selectedRoleFilter
        } else true
        
        matchesSearch && matchesRole
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Users") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddUserDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add User")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Search and filter bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Search field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search users") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Role filter chips
                    Text(
                        text = "Filter by role:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedRoleFilter == null,
                            onClick = { selectedRoleFilter = null },
                            label = { Text("All") }
                        )
                        
                        FilterChip(
                            selected = selectedRoleFilter == "admin",
                            onClick = { selectedRoleFilter = "admin" },
                            label = { Text("Admin") }
                        )
                        
                        FilterChip(
                            selected = selectedRoleFilter == "teacher",
                            onClick = { selectedRoleFilter = "teacher" },
                            label = { Text("Teacher") }
                        )
                        
                        FilterChip(
                            selected = selectedRoleFilter == "student",
                            onClick = { selectedRoleFilter = "student" },
                            label = { Text("Student") }
                        )
                        
                        FilterChip(
                            selected = selectedRoleFilter == "parent",
                            onClick = { selectedRoleFilter = "parent" },
                            label = { Text("Parent") }
                        )
                    }
                }
            }
            
            // Users list
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(filteredUsers) { user ->
                    UserListItem(
                        user = user,
                        onEdit = {
                            selectedUser = user
                            // Open edit user dialog here
                        },
                        onDelete = {
                            selectedUser = user
                            showDeleteConfirmDialog = true
                        }
                    )
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteConfirmDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete User") },
            text = { 
                Text("Are you sure you want to delete ${selectedUser?.fullName}? This action cannot be undone.")
            },
            confirmButton = { 
                TextButton(
                    onClick = {
                        // Delete user logic here
                        showDeleteConfirmDialog = false
                        selectedUser = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDeleteConfirmDialog = false
                        selectedUser = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Add User Dialog - simplified, would need more fields in a real app
    if (showAddUserDialog) {
        var email by remember { mutableStateOf("") }
        var fullName by remember { mutableStateOf("") }
        var role by remember { mutableStateOf(UserRole.Student.value) }
        
        AlertDialog(
            onDismissRequest = { showAddUserDialog = false },
            title = { Text("Add New User") },
            text = {
                Column {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    
                    Text(
                        text = "Role:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        getAllUserRoles().forEach { userRole ->
                            FilterChip(
                                selected = role == userRole.value,
                                onClick = { role = userRole.value },
                                label = { Text(userRole.value.capitalize()) }
                            )
                        }
                    }
                }
            },
            confirmButton = { 
                TextButton(
                    onClick = {
                        // Add user logic here
                        showAddUserDialog = false
                    },
                    enabled = email.isNotEmpty() && fullName.isNotEmpty()
                ) {
                    Text("Add User")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddUserDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * User list item component
 */
@Composable
fun UserListItem(
    user: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User icon with role-based color
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = getRoleColor(user.role).copy(alpha = 0.2f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = getRoleColor(user.role)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // User details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoleChip(role = user.role)
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "Last login: ${dateFormatter.format(user.lastLogin)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            // Action buttons
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * Role chip component
 */
@Composable
fun RoleChip(role: String) {
    Surface(
        color = getRoleColor(role).copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = role.capitalize(),
            style = MaterialTheme.typography.labelSmall,
            color = getRoleColor(role),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Helper function to get color based on user role
 */
@Composable
fun getRoleColor(role: String): Color {
    return when (role) {
        "admin" -> MaterialTheme.colorScheme.primary
        "teacher" -> Color(0xFF4CAF50) // Green
        "student" -> Color(0xFF2196F3) // Blue
        "parent" -> Color(0xFFFF9800) // Orange
        "staff" -> Color(0xFF9C27B0) // Purple
        else -> MaterialTheme.colorScheme.onSurface
    }
}

// Extension function to capitalize first letter
private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun getAllUserRoles() = listOf(UserRole.Admin, UserRole.Teacher, UserRole.Staff, UserRole.Student, UserRole.Parent)