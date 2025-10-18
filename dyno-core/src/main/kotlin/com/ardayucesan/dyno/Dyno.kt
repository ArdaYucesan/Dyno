package com.ardayucesan.dyno

import android.content.Context
import com.ardayucesan.dyno.runtime.DynoManager
import com.ardayucesan.dyno.ui.DynoDebugLauncher

/**
 * Main entry point for Dyno debugging library.
 * This class provides a simplified API for initialization and basic operations.
 */
object Dyno {
    
    private var isInitialized = false
    
    /**
     * Initialize Dyno with the given context.
     * Call this in your Application.onCreate() or MainActivity.onCreate().
     * 
     * @param context Application context
     * @param enableInDebug Whether to enable Dyno only in debug builds (default: true)
     * @param autoShowNotification Whether to automatically show the debug notification (default: true)
     */
    @JvmStatic
    @JvmOverloads
    fun initialize(
        context: Context,
        enableInDebug: Boolean = true,
        autoShowNotification: Boolean = true
    ) {
        if (isInitialized) {
            android.util.Log.w("Dyno", "Dyno is already initialized")
            return
        }
        
        // Initialize the runtime manager
        DynoManager.getInstance().initialize(context, enableInDebug)
        
        // Initialize debug launcher
        if (enableInDebug) {
            DynoDebugLauncher.initialize(context)
            
            // Show notification if requested
            if (autoShowNotification) {
                showDebugNotification(context)
            }
        }
        
        isInitialized = true
        android.util.Log.d("Dyno", "Dyno initialized successfully")
    }
    
    /**
     * Register an object instance with Dyno for parameter monitoring.
     * Call this for each instance that has @DynoGroup annotation.
     * 
     * @param instance The instance to register
     */
    @JvmStatic
    fun register(instance: Any) {
        DynoManager.getInstance().register(instance)
    }
    
    /**
     * Unregister an object instance from Dyno.
     * Call this in onDestroy() or when the instance is no longer needed.
     * 
     * @param instance The instance to unregister
     */
    @JvmStatic
    fun unregister(instance: Any) {
        DynoManager.getInstance().unregister(instance)
    }
    
    /**
     * Show the debug notification.
     * The notification provides quick access to the debug interface.
     * 
     * @param context Application context
     */
    @JvmStatic
    fun showDebugNotification(context: Context) {
        DynoManager.getInstance().showDebugInterface()
    }
    
    /**
     * Hide the debug notification.
     * 
     * @param context Application context
     */
    @JvmStatic
    fun hideDebugNotification(context: Context) {
        DynoManager.getInstance().hideDebugInterface()
    }
    
    /**
     * Launch the debug interface directly.
     * 
     * @param context Application context
     */
    @JvmStatic
    fun launchDebugInterface(context: Context) {
        if (DynoManager.getInstance().isDebugMode()) {
            DynoDebugLauncher.launch(context)
        }
    }
    
    /**
     * Check if Dyno is initialized.
     * 
     * @return true if initialized, false otherwise
     */
    @JvmStatic
    fun isInitialized(): Boolean = isInitialized
    
    /**
     * Check if Dyno is in debug mode.
     * 
     * @return true if in debug mode, false otherwise
     */
    @JvmStatic
    fun isDebugMode(): Boolean = DynoManager.getInstance().isDebugMode()
}

/**
 * Extension functions for easier integration.
 */

/**
 * Extension function to register an object with Dyno.
 * Usage: myObject.dynoRegister()
 */
fun Any.dynoRegister() {
    Dyno.register(this)
}

/**
 * Extension function to unregister an object from Dyno.
 * Usage: myObject.dynoUnregister()
 */
fun Any.dynoUnregister() {
    Dyno.unregister(this)
}