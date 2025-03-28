package com.titanflaws.erp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.titanflaws.erp.utils.DataSyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ERPApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        scheduleDataSync()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // General notifications channel
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General notifications for school updates"
            }
            
            // Academic notifications channel
            val academicChannel = NotificationChannel(
                CHANNEL_ACADEMIC,
                "Academic Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Academic notifications for exams, results, and assignments"
            }
            
            // Fee notifications channel
            val feeChannel = NotificationChannel(
                CHANNEL_FEE,
                "Fee Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Fee due date notifications and payment confirmations"
            }
            
            // Attendance notifications channel
            val attendanceChannel = NotificationChannel(
                CHANNEL_ATTENDANCE,
                "Attendance Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Attendance updates and absence notifications"
            }
            
            // Get the notification manager and create channels
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(
                listOf(generalChannel, academicChannel, feeChannel, attendanceChannel)
            )
        }
    }
    
    private fun scheduleDataSync() {
        // Schedule periodic data synchronization
        DataSyncWorker.schedule(this)
    }
    
    // For Hilt WorkManager integration
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    
    companion object {
        const val CHANNEL_GENERAL = "channel_general"
        const val CHANNEL_ACADEMIC = "channel_academic"
        const val CHANNEL_FEE = "channel_fee"
        const val CHANNEL_ATTENDANCE = "channel_attendance"
    }
} 