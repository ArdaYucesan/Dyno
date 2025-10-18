package com.ardayucesan.dyno.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast

/**
 * Handles launching the Dyno debug interface from various entry points.
 */
object DynoDebugLauncher {
    
    private var isReceiverRegistered = false
    
    /**
     * Initialize the debug launcher with context.
     * This sets up broadcast receivers for notification taps.
     */
    fun initialize(context: Context) {
        if (!isReceiverRegistered) {
            val receiver = DynoDebugReceiver()
            val filter = IntentFilter("com.ardayucesan.dyno.OPEN_DEBUG_INTERFACE")
            
            // Register receiver with appropriate flag for Android 14+
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(receiver, filter, android.content.Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(receiver, filter)
            }
            isReceiverRegistered = true
        }
    }
    
    /**
     * Launch the debug interface directly.
     */
    fun launch(context: Context) {
        try {
            val intent = Intent(context, DynoDebugActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Error launching Dyno debug interface", Toast.LENGTH_SHORT).show()
            android.util.Log.e("DynoDebugLauncher", "Error launching debug interface", e)
        }
    }
    
    /**
     * Cleanup resources.
     */
    fun cleanup(context: Context) {
        // Receiver cleanup will be handled automatically when app is destroyed
        isReceiverRegistered = false
    }
}

/**
 * Broadcast receiver for handling debug interface launch requests.
 */
private class DynoDebugReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        android.util.Log.d("DynoDebugReceiver", "Received broadcast: ${intent?.action}")
        if (context != null && intent?.action == "com.ardayucesan.dyno.OPEN_DEBUG_INTERFACE") {
            android.util.Log.d("DynoDebugReceiver", "Launching debug interface")
            DynoDebugLauncher.launch(context)
        }
    }
}