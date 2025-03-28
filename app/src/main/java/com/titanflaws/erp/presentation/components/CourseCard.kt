package com.titanflaws.erp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.titanflaws.erp.presentation.viewmodel.CourseWithDetails

@Composable
fun CourseCard(
    courseWithDetails: CourseWithDetails,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = courseWithDetails.courseName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Course"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Course"
                    )
                }
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Text(text = "Code: ${courseWithDetails.courseCode}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Description: ${courseWithDetails.description}", style = MaterialTheme.typography.bodyMedium)
            if (courseWithDetails.department != null) {
                Text(text = "Department: ${courseWithDetails.department}", style = MaterialTheme.typography.bodyMedium)
            }
            if (courseWithDetails.credits != null) {
                Text(text = "Credits: ${courseWithDetails.credits}", style = MaterialTheme.typography.bodyMedium)
            }
            Text(text = "Status: ${courseWithDetails.status}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}