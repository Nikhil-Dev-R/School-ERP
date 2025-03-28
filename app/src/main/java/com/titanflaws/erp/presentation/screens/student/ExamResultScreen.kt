package com.titanflaws.erp.presentation.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.titanflaws.erp.data.model.ExamResult
import com.titanflaws.erp.presentation.components.common.ErrorMessage
import com.titanflaws.erp.presentation.components.common.LoadingIndicator
import com.titanflaws.erp.presentation.viewmodel.ExamViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen for students to view their exam results
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamResultScreen(
    studentId: String,
    examId: String,
    onNavigateBack: () -> Unit,
    examViewModel: ExamViewModel = hiltViewModel()
) {
    val uiState by examViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    // Effect to load the specified exam result when the screen is first displayed
    LaunchedEffect(key1 = examId, key2 = studentId) {
        examViewModel.loadExam(examId)
        examViewModel.loadStudentResultForExam(examId, studentId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exam Result") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                .padding(16.dp)
        ) {
            // Error message
            uiState.errorMessage?.let { message ->
                ErrorMessage(
                    message = message,
                    onDismiss = {
                        examViewModel.clearError()
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (uiState.isLoading) {
                LoadingIndicator("Loading result...")
            } else if (uiState.selectedExam != null) {
                // Exam header
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = uiState.selectedExam?.title ?: "Exam",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Type: ${uiState.selectedExam?.examType}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        uiState.selectedExam?.courseName?.let { courseName ->
                            Text(
                                text = "Subject: $courseName",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        Text(
                            text = "Date: ${formatDate(uiState.selectedExam?.startDate ?: Date())}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "Total Marks: ${uiState.selectedExam?.totalMarks}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Result section
                if (uiState.selectedResult != null) {
                    ResultCard(uiState.selectedResult!!)
                } else {
                    // No result available
                    NoResultAvailable()
                }
            } else {
                // No exam found
                Text(
                    text = "Exam not found or not available",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                )
            }
        }
    }
}

/**
 * Displays a card with the exam result details
 */
@Composable
fun ResultCard(result: ExamResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Result header with status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Result",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                StatusChip(status = result.status)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Percentage circle
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(getStatusColor(result.status).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${result.percentage.toInt()}%",
                        style = MaterialTheme.typography.headlineLarge,
                        color = getStatusColor(result.status),
                        fontWeight = FontWeight.Bold
                    )
                    
                    result.grade?.let { grade ->
                        Text(
                            text = "Grade: $grade",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Marks details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MarksDetail(label = "Marks Obtained", value = "${result.marksObtained}")
                
                // We don't have total marks in the result, so use a placeholder or calculate from percentage
                val totalMarks = if (result.percentage > 0) {
                    (result.marksObtained * 100 / result.percentage).toInt()
                } else {
                    100 // Default
                }
                
                MarksDetail(label = "Total Marks", value = "$totalMarks")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Divider()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Additional details
            result.courseName?.let { courseName ->
                DetailRow(label = "Subject", value = courseName)
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            result.remarks?.let { remarks ->
                DetailRow(label = "Remarks", value = remarks)
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            result.evaluatedAt?.let { date ->
                DetailRow(label = "Evaluated On", value = formatDate(date))
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (result.status == "PASS") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Gold star for passing
                    for (i in 1..3) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(32.dp)
                        )
                        
                        if (i < 3) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Congratulations!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Displays a status chip for the exam result status
 */
@Composable
fun StatusChip(status: String) {
    Surface(
        color = getStatusColor(status).copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelMedium,
            color = getStatusColor(status),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

/**
 * Displays a detail with label and value
 */
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Displays a marks detail with label and value
 */
@Composable
fun MarksDetail(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

/**
 * Displays a message when the result is not available
 */
@Composable
fun NoResultAvailable() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Result Not Available",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "The result for this exam has not been published yet or is being processed.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Please check back later.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Helper function to format a date
 */
@Composable
fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return formatter.format(date)
}

/**
 * Helper function to get a color based on status
 */
@Composable
fun getStatusColor(status: String): Color {
    return when (status) {
        "PASS" -> Color(0xFF4CAF50)  // Green
        "FAIL" -> Color(0xFFF44336)  // Red
        "ABSENT" -> Color(0xFF9E9E9E) // Gray
        "INCOMPLETE" -> Color(0xFFFF9800) // Orange
        else -> MaterialTheme.colorScheme.primary
    }
} 