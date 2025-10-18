package com.ardayucesan.dyno

import android.app.Application

/**
 * Optional base Application class that automatically initializes Dyno.
 * Developers can extend this class instead of manually calling Dyno.initialize().
 * 
 * Usage:
 * class MyApplication : DynoApplication() {
 *     override fun onCreate() {
 *         super.onCreate() // This will initialize Dyno
 *         // Your app initialization code
 *     }
 * }
 */
open class DynoApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Automatically initialize Dyno
        // Only enable in debug builds by checking BuildConfig.DEBUG
        val isDebug = try {
            val buildConfigClass = Class.forName("${packageName}.BuildConfig")
            val debugField = buildConfigClass.getField("DEBUG")
            debugField.getBoolean(null)
        } catch (e: Exception) {
            // Fallback: assume debug if we can't determine
            true
        }
        
        Dyno.initialize(
            context = this,
            enableInDebug = isDebug,
            autoShowNotification = isDebug
        )
    }
}