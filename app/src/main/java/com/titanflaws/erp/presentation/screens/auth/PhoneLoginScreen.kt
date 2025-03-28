package com.titanflaws.erp.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.components.common.ErrorMessage
import com.titanflaws.erp.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

/**
 * Screen for phone number authentication
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneLoginScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    
    // Form state
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var isPhoneValid by remember { mutableStateOf(true) }
    var isOtpValid by remember { mutableStateOf(true) }
    var resendEnabled by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(60) }
    
    // Check if user is logged in
    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            onNavigateToHome()
        }
    }
    
    // Handle OTP sent message
    LaunchedEffect(authState.otpSent) {
        if (authState.otpSent) {
            // Start countdown for resend button
            resendEnabled = false
            countdown = 60
            
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            
            resendEnabled = true
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phone Login") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (authState.otpSent) "Verify OTP" else "Login with Phone",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = if (authState.otpSent) 
                    "Enter the verification code sent to ${authState.phoneNumber}" 
                else 
                    "Enter your phone number to receive a verification code",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )
            
            // Error message
            if (authState.error != null) {
                ErrorMessage(
                    message = authState.error!!,
                    onDismiss = { authViewModel.clearError() }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Success message
            if (authState.message != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = authState.message!!,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (!authState.otpSent) {
                // Phone number input field
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { 
                        phoneNumber = it
                        isPhoneValid = it.isEmpty() || it.length >= 10
                    },
                    label = { Text("Phone Number") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = "Phone")
                    },
                    placeholder = { Text("+91 9999999999") },
                    isError = !isPhoneValid,
                    supportingText = {
                        if (!isPhoneValid) {
                            Text("Please enter a valid phone number")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (phoneNumber.isNotBlank() && isPhoneValid) {
                                val formattedPhone = formatPhoneNumber(phoneNumber)
                                authViewModel.startPhoneAuth(formattedPhone)
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Send OTP button
                Button(
                    onClick = {
                        val formattedPhone = formatPhoneNumber(phoneNumber)
                        authViewModel.startPhoneAuth(formattedPhone)
                    },
                    enabled = phoneNumber.isNotBlank() && isPhoneValid && !authState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (authState.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Send Verification Code")
                    }
                }
            } else {
                // OTP verification fields
                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { 
                        if (it.length <= 6) {
                            otpCode = it
                            isOtpValid = it.isEmpty() || it.length == 6
                        }
                    },
                    label = { Text("Verification Code") },
                    leadingIcon = {
                        Icon(Icons.Default.Password, contentDescription = "OTP")
                    },
                    isError = !isOtpValid,
                    supportingText = {
                        if (!isOtpValid) {
                            Text("Please enter a valid 6-digit code")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (otpCode.length == 6) {
                                authViewModel.verifyOtp(otpCode)
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Resend timer
                if (!resendEnabled) {
                    Text(
                        text = "Resend code in $countdown seconds",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.End)
                    )
                } else {
                    TextButton(
                        onClick = {
                            val formattedPhone = formatPhoneNumber(phoneNumber)
                            authViewModel.startPhoneAuth(formattedPhone)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Resend Code")
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Verify button
                Button(
                    onClick = { authViewModel.verifyOtp(otpCode) },
                    enabled = otpCode.length == 6 && !authState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (authState.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Verify")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Change phone number button
                OutlinedButton(
                    onClick = { authViewModel.resetAuthState() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change Phone Number")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Format phone number to E.164 format
 */
private fun formatPhoneNumber(phoneNumber: String): String {
    var formattedNumber = phoneNumber.trim()
    
    // Remove any non-digit characters
    formattedNumber = formattedNumber.replace(Regex("[^0-9]"), "")
    
    // Add country code if missing
    if (!formattedNumber.startsWith("+")) {
        if (formattedNumber.startsWith("0")) {
            formattedNumber = "+91" + formattedNumber.substring(1)
        } else {
            formattedNumber = "+91" + formattedNumber
        }
    }
    
    return formattedNumber
} 