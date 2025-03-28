package com.titanflaws.erp.presentation.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A reusable component for displaying success messages
 * @param message The success message to display
 * @param onDismiss Callback when the message is dismissed
 * @param autoDismiss Whether to automatically dismiss the message after a delay
 * @param dismissDelay Time in milliseconds before auto-dismissing (default: 3000ms)
 */
@Composable
fun SuccessMessage(
    message: String,
    onDismiss: () -> Unit,
    autoDismiss: Boolean = true,
    dismissDelay: Long = 3000
) {
    // Auto-dismiss after delay if enabled
    if (autoDismiss) {
        LaunchedEffect(key1 = message) {
            kotlinx.coroutines.delay(dismissDelay)
            onDismiss()
        }
    }
    
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
} 