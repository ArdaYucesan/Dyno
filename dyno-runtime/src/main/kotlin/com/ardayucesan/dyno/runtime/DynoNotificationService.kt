package com.ardayucesan.dyno.runtime

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

/**
 * Background service for showing Dyno debug notification.
 * Provides quick access to debug interface from notification panel.
 */
class DynoNotificationService : Service() {
    
    companion object {
        private const val NOTIFICATION_ID = 12345
        private const val CHANNEL_ID = "dyno_debug_channel"
        private const val CHANNEL_NAME = "Dyno Debug"
        
        fun start(context: Context) {
            val intent = Intent(context, DynoNotificationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stop(context: Context) {
            val intent = Intent(context, DynoNotificationService::class.java)
            context.stopService(intent)
        }
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        return START_STICKY
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Dyno debug interface notifications"
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun showNotification() {
        val pendingIntent = createPendingIntent()
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🔧 Dyno Debug Active")
            .setContentText("Tap to open debug interface")
            .setSmallIcon(android.R.drawable.ic_menu_preferences)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
        
        startForeground(NOTIFICATION_ID, notification)
    }
    
    private fun createPendingIntent(): PendingIntent {
        // Create intent to open debug interface
        // For now, this will just log that the notification was tapped
        val intent = Intent().apply {
            action = "com.ardayucesan.dyno.OPEN_DEBUG_INTERFACE"
        }
        
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        
        return PendingIntent.getBroadcast(this, 0, intent, flags)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }
}