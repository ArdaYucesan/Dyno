package com.ardayucesan.dyno.runtime

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

/**
 * Simple notification helper for Dyno debug interface.
 * Shows a persistent notification without using foreground service.
 */
object DynoNotificationHelper {
    
    private const val NOTIFICATION_ID = 12345
    private const val CHANNEL_ID = "dyno_debug_channel"
    private const val CHANNEL_NAME = "Dyno Debug"
    
    private var notificationManager: NotificationManager? = null
    
    /**
     * Show the debug notification.
     */
    fun showNotification(context: Context) {
        createNotificationChannel(context)
        
        val pendingIntent = createPendingIntent(context)
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("🔧 Dyno Debug Active")
            .setContentText("Tap to open debug interface")
            .setSmallIcon(android.R.drawable.ic_menu_preferences)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
        
        getNotificationManager(context).notify(NOTIFICATION_ID, notification)
        android.util.Log.d("DynoNotificationHelper", "Debug notification shown")
    }
    
    /**
     * Hide the debug notification.
     */
    fun hideNotification(context: Context) {
        getNotificationManager(context).cancel(NOTIFICATION_ID)
        android.util.Log.d("DynoNotificationHelper", "Debug notification hidden")
    }
    
    private fun createNotificationChannel(context: Context) {
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
            
            getNotificationManager(context).createNotificationChannel(channel)
        }
    }
    
    private fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent().apply {
            setClassName(context.packageName, "com.ardayucesan.dyno.ui.DynoDebugActivity")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        
        return PendingIntent.getActivity(context, 0, intent, flags)
    }
    
    private fun getNotificationManager(context: Context): NotificationManager {
        if (notificationManager == null) {
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager!!
    }
}