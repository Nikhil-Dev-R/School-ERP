package com.titanflaws.erp.presentation.screens.parent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.presentation.components.EmptyStateMessage
import com.titanflaws.erp.presentation.components.LoadingIndicator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeesPaymentScreen(
    onNavigateBack: () -> Unit,
    studentId: String,
//    feesViewModel: FeesViewModel = hiltViewModel()
) {
    /*val uiState by feesViewModel.uiState.collectAsState()
    var showPaymentDialog by remember { mutableStateOf(false) }
    var selectedFeeId by remember { mutableStateOf("") }
    var paymentAmount by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Online") }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(key1 = studentId) {
        feesViewModel.loadStudentFees(studentId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fees & Payments") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LoadingIndicator()
            } else if (uiState.feeItems.isEmpty()) {
                EmptyStateMessage(
                    message = "No fees information available",
                    icon = Icons.Default.Receipt
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Summary Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Fee Summary",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Fees:")
                                Text(uiState.totalFees.formatToCurrency())
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Amount Paid:")
                                Text(uiState.amountPaid.formatToCurrency())
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Due Amount:",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = uiState.dueAmount.formatToCurrency(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (uiState.dueAmount > 0) Color.Red else Color.Green
                                )
                            }
                        }
                    }
                    
                    // Fee Items List
                    Text(
                        text = "Fee Details",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    LazyColumn {
                        items(uiState.feeItems) { feeItem ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = feeItem.feeName,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        
                                        val statusColor = when(feeItem.status) {
                                            "paid" -> Color.Green
                                            "partially_paid" -> Color.Yellow
                                            "overdue" -> Color.Red
                                            else -> Color(0xFF2196F3) // Blue for pending
                                        }
                                        
                                        Text(
                                            text = feeItem.status.replace("_", " ").capitalize(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = statusColor
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Amount:")
                                        Text(feeItem.amount.formatToCurrency())
                                    }
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Paid:")
                                        Text(feeItem.amountPaid.formatToCurrency())
                                    }
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Due:")
                                        Text(
                                            text = (feeItem.amount - feeItem.amountPaid).formatToCurrency(),
                                            color = if (feeItem.amount - feeItem.amountPaid > 0) Color.Red else Color.Green
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Text(
                                        text = "Due Date: ${feeItem.dueDate}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    
                                    // Show pay button if any amount is due
                                    if (feeItem.amount - feeItem.amountPaid > 0) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = {
                                                selectedFeeId = feeItem.id
                                                paymentAmount = (feeItem.amount - feeItem.amountPaid).toString()
                                                showPaymentDialog = true
                                            },
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Payment,
                                                contentDescription = "Pay"
                                            )
                                            Spacer(width = 4.dp)
                                            Text("Pay")
                                        }
                                    }
                                }
                            }
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Payment History",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        if (uiState.paymentHistory.isEmpty()) {
                            item {
                                Text(
                                    text = "No payment history available",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                )
                            }
                        } else {
                            items(uiState.paymentHistory) { payment ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Receipt #${payment.receiptNumber}",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Text(
                                                text = payment.amount.formatToCurrency(),
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        Text(
                                            text = "Date: ${payment.date}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        
                                        Text(
                                            text = "Fee: ${payment.feeName}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        
                                        Text(
                                            text = "Method: ${payment.paymentMethod}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        
                                        if (payment.transactionId != null) {
                                            Text(
                                                text = "Transaction ID: ${payment.transactionId}",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Bottom space for better scrolling
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
            
            // Payment Dialog
            if (showPaymentDialog) {
                val selectedFee = uiState.feeItems.find { it.id == selectedFeeId }
                
                AlertDialog(
                    onDismissRequest = { showPaymentDialog = false },
                    title = { Text("Make Payment") },
                    text = {
                        Column {
                            Text("Fee: ${selectedFee?.feeName ?: ""}")
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            OutlinedTextField(
                                value = paymentAmount,
                                onValueChange = { paymentAmount = it },
                                label = { Text("Amount") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text("Payment Method:")
                            RadioButton(
                                selected = paymentMethod == "Online",
                                onClick = { paymentMethod = "Online" }
                            )
                            Text("Online Payment (Credit/Debit Card)")
                            
                            RadioButton(
                                selected = paymentMethod == "Bank Transfer",
                                onClick = { paymentMethod = "Bank Transfer" }
                            )
                            Text("Bank Transfer")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showPaymentDialog = false
                                showConfirmationDialog = true
                            }
                        ) {
                            Text("Proceed to Payment")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { showPaymentDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
            
            // Payment Confirmation Dialog
            if (showConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmationDialog = false },
                    title = { Text("Confirm Payment") },
                    text = {
                        Text("Are you sure you want to make a payment of ${paymentAmount.toDoubleOrNull()?.formatToCurrency() ?: "0.00"} via $paymentMethod?")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Process payment
                                val amountValue = paymentAmount.toDoubleOrNull() ?: 0.0
                                feesViewModel.makePayment(
                                    studentId = studentId,
                                    feeId = selectedFeeId,
                                    amount = amountValue,
                                    paymentMethod = paymentMethod
                                )
                                showConfirmationDialog = false
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { showConfirmationDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
            
            // Show error message if any
            if (uiState.errorMessage != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(uiState.errorMessage!!)
                }
            }
        }
    }*/
}

private fun String.capitalize(): String {
    return this.split(" ").joinToString(" ") { 
        it.replaceFirstChar { char -> 
            if (char.isLowerCase()) char.titlecase() else char.toString() 
        }
    }
} 