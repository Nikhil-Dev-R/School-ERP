package com.titanflaws.erp.presentation.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Date

/**
 * A custom date picker dialog component
 * @param initialDate The initially selected date
 * @param onDateSelected Callback when a date is selected
 * @param onDismiss Callback when the dialog is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    initialDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        time = initialDate
    }
    
    var year by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var day by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Date") },
        text = {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Date picker
                DatePicker(
                    state = rememberDatePickerState(
                        initialSelectedDateMillis = calendar.timeInMillis
                    ),
                    modifier = Modifier.padding(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    calendar.set(year, month, day)
                    onDateSelected(calendar.time)
                }
            ) {
                Text("Confirm")
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
 * A simplified date picker for selecting just a date
 * @param initialDate The initially selected date
 * @param onDateSelected Callback when a date is selected
 * @param onDismiss Callback when the dialog is dismissed
 */
@Composable
fun SimpleDatePicker(
    initialDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        time = initialDate
    }
    
    var year by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var day by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Date") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Year picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Year:")
                    
                    NumberPicker(
                        value = year,
                        onValueChange = { year = it },
                        range = (year - 5)..(year + 5)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Month picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Month:")
                    
                    NumberPicker(
                        value = month + 1, // Month is 0-based in Calendar
                        onValueChange = { month = it - 1 },
                        range = 1..12
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Day picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Day:")
                    
                    // Calculate max days in the selected month
                    val maxDays = run {
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, month)
                        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    }
                    
                    NumberPicker(
                        value = day,
                        onValueChange = { day = it },
                        range = 1..maxDays
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    calendar.set(year, month, day)
                    onDateSelected(calendar.time)
                }
            ) {
                Text("Confirm")
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
 * A simple number picker component
 * @param value The current value
 * @param onValueChange Callback when the value changes
 * @param range The range of valid values
 */
@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = {
                if (value > range.first) {
                    onValueChange(value - 1)
                }
            },
            enabled = value > range.first
        ) {
            Text("-")
        }
        
        Text(value.toString())
        
        TextButton(
            onClick = {
                if (value < range.last) {
                    onValueChange(value + 1)
                }
            },
            enabled = value < range.last
        ) {
            Text("+")
        }
    }
} 