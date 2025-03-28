package com.titanflaws.erp.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.titanflaws.erp.ERPApplication.Companion.CHANNEL_ACADEMIC
import com.titanflaws.erp.ERPApplication.Companion.CHANNEL_ATTENDANCE
import com.titanflaws.erp.ERPApplication.Companion.CHANNEL_FEE
import com.titanflaws.erp.ERPApplication.Companion.CHANNEL_GENERAL
import com.titanflaws.erp.MainActivity
import com.titanflaws.erp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ERPFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var tokenRepository: TokenRepository

    override fun onNewToken(token: String) {
        // Save the new token to Firestore
        CoroutineScope(Dispatchers.IO).launch {
            tokenRepository.updateToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle the incoming message
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "School ERP"
            val body = notification.body ?: "You have a new notification"
            
            // Get notification type from data payload
            val notificationType = remoteMessage.data["type"] ?: "general"
            val targetScreen = remoteMessage.data["targetScreen"]
            
            // Show notification based on type
            showNotification(title, body, notificationType, targetScreen)
        }
    }
    
    private fun showNotification(
        title: String,
        body: String,
        type: String,
        targetScreen: String?
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create intent for when notification is tapped
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            targetScreen?.let { putExtra("target_screen", it) }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        
        // Determine channel ID based on notification type
        val channelId = when (type) {
            "academic" -> CHANNEL_ACADEMIC
            "fee" -> CHANNEL_FEE
            "attendance" -> CHANNEL_ATTENDANCE
            else -> CHANNEL_GENERAL
        }
        
        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        
        // Show the notification
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}

// Interface for token repository
interface TokenRepository {
    suspend fun updateToken(token: String)
} 