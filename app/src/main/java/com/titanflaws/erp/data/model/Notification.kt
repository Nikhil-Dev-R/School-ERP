package com.titanflaws.erp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a notification in the system
 * @property notificationId Unique identifier for the notification
 * @property title Notification title
 * @property message Notification message body
 * @property type Notification type (GENERAL, ACADEMIC, FEE, ATTENDANCE, etc.)
 * @property priority Priority level (HIGH, MEDIUM, LOW)
 * @property recipientId ID of the recipient (null for broadcasts)
 * @property recipientRole Role of the recipient (ADMIN, TEACHER, STUDENT, PARENT, STAFF, ALL)
 * @property senderName Name of the sender
 * @property senderId ID of the sender
 * @property relatedEntityId ID of related entity (e.g., exam ID, fee ID)
 * @property relatedEntityType Type of related entity (EXAM, FEE, ATTENDANCE, etc.)
 * @property actionUrl Deep link URL for the notification
 * @property isRead Whether the notification has been read
 * @property createdAt Creation timestamp
 */
@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey
    val notificationId: String,
    val title: String,
    val message: String,
    val type: String,
    val priority: String,
    val recipientId: String?,
    val recipientRole: String?,
    val senderName: String,
    val senderId: String,
    val relatedEntityId: String?,
    val relatedEntityType: String?,
    val actionUrl: String?,
    val isRead: Boolean = false,
    val createdAt: Date = Date()
) 