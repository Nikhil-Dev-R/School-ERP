package com.titanflaws.erp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titanflaws.erp.presentation.screens.common.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Data class representing the UI state for notifications
 */
data class NotificationUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val notifications: List<Notification> = emptyList(),
    val hasUnreadNotifications: Boolean = false
)

/**
 * ViewModel for managing notifications
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    // TODO: Inject NotificationRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()
    
    private val allNotifications = mutableListOf<Notification>()
    private var currentFilter: String? = null
    
    /**
     * Load notifications for the current user
     */
    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // TODO: Replace with actual repository call
                delay(1000) // Simulate network delay
                
                // Sample notifications for demo
                allNotifications.clear()
                allNotifications.addAll(generateSampleNotifications())
                
                applyFilter(currentFilter)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        hasUnreadNotifications = allNotifications.any { notification -> !notification.read }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Failed to load notifications: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Filter notifications based on type
     */
    fun filterNotifications(filter: String?) {
        currentFilter = filter
        applyFilter(filter)
    }
    
    /**
     * Mark a notification as read
     */
    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            // Find and update the notification in allNotifications
            val index = allNotifications.indexOfFirst { it.id == notificationId }
            if (index != -1) {
                val updatedNotification = allNotifications[index].copy(read = true)
                allNotifications[index] = updatedNotification
                
                // TODO: Call repository to update in backend
                
                // Update UI state
                applyFilter(currentFilter)
                _uiState.update { 
                    it.copy(
                        hasUnreadNotifications = allNotifications.any { notification -> !notification.read }
                    )
                }
            }
        }
    }
    
    /**
     * Mark all notifications as read
     */
    fun markAllAsRead() {
        viewModelScope.launch {
            // Update all notifications
            allNotifications.forEachIndexed { index, notification ->
                if (!notification.read) {
                    allNotifications[index] = notification.copy(read = true)
                }
            }
            
            // TODO: Call repository to update in backend
            
            // Update UI state
            applyFilter(currentFilter)
            _uiState.update { it.copy(hasUnreadNotifications = false) }
        }
    }
    
    /**
     * Dismiss a notification
     */
    fun dismissNotification(notificationId: String) {
        viewModelScope.launch {
            // Remove from allNotifications
            val removed = allNotifications.removeIf { it.id == notificationId }
            
            if (removed) {
                // TODO: Call repository to delete in backend
                
                // Update UI state
                applyFilter(currentFilter)
                _uiState.update { 
                    it.copy(
                        hasUnreadNotifications = allNotifications.any { notification -> !notification.read }
                    )
                }
            }
        }
    }
    
    /**
     * Apply filter to notifications
     */
    private fun applyFilter(filter: String?) {
        val filteredList = when (filter) {
            "unread" -> allNotifications.filter { !it.read }
            "announcement" -> allNotifications.filter { it.type == "announcement" }
            "assignment" -> allNotifications.filter { it.type == "assignment" }
            "exam" -> allNotifications.filter { it.type == "exam" }
            "attendance" -> allNotifications.filter { it.type == "attendance" }
            else -> allNotifications.toList()
        }
        
        _uiState.update { it.copy(notifications = filteredList) }
    }
    
    /**
     * Generate sample notifications for demo
     */
    private fun generateSampleNotifications(): List<Notification> {
        val calendar = Calendar.getInstance()
        val now = calendar.time
        
        val notifications = mutableListOf<Notification>()
        
        // Add some sample notifications with different timestamps
        
        // Just now
        notifications.add(
            Notification(
                id = "1",
                title = "New Announcement",
                message = "Principal's address scheduled for tomorrow. Attendance is mandatory for all students.",
                type = "announcement",
                timestamp = now,
                read = false,
                targetId = "ann123",
                sender = "Principal"
            )
        )
        
        // 5 minutes ago
        calendar.add(Calendar.MINUTE, -5)
        notifications.add(
            Notification(
                id = "2",
                title = "Assignment Deadline Extended",
                message = "The deadline for Mathematics Assignment has been extended by 2 days.",
                type = "assignment",
                timestamp = calendar.time,
                read = false,
                targetId = "ass456",
                sender = "Math Teacher"
            )
        )
        
        // 2 hours ago
        calendar.add(Calendar.HOUR, -2)
        notifications.add(
            Notification(
                id = "3",
                title = "Exam Results Published",
                message = "Mid-term examination results have been published. Check your dashboard.",
                type = "exam",
                timestamp = calendar.time,
                read = true,
                targetId = "exam789",
                sender = "Examination Department"
            )
        )
        
        // 1 day ago
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        notifications.add(
            Notification(
                id = "4",
                title = "Attendance Warning",
                message = "Your attendance is below 75% in Science class. Please improve attendance.",
                type = "attendance",
                timestamp = calendar.time,
                read = false,
                targetId = "att321",
                sender = "Class Teacher"
            )
        )
        
        // 3 days ago
        calendar.add(Calendar.DAY_OF_MONTH, -2)
        notifications.add(
            Notification(
                id = "5",
                title = "Library Book Due",
                message = "The book 'Physics for Beginners' is due for return tomorrow.",
                type = "message",
                timestamp = calendar.time,
                read = true,
                targetId = "lib654",
                sender = "Librarian"
            )
        )
        
        // 1 week ago
        calendar.add(Calendar.DAY_OF_MONTH, -4)
        notifications.add(
            Notification(
                id = "6",
                title = "Fee Payment Reminder",
                message = "This is a reminder to pay the second term fees by the end of this month.",
                type = "payment",
                timestamp = calendar.time,
                read = true,
                targetId = "pay987",
                sender = "Finance Department"
            )
        )
        
        return notifications
    }
} 