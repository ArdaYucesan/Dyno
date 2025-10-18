package com.ardayucesan.dyno.runtime

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/**
 * Main manager class for Dyno debugging library.
 * Handles initialization, instance registration, and lifecycle management.
 */
class DynoManager private constructor() {
    
    private var isInitialized = false
    private var isDebugMode = false
    private lateinit var applicationContext: Context
    
    companion object {
        @Volatile
        private var INSTANCE: DynoManager? = null
        
        fun getInstance(): DynoManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DynoManager().also { INSTANCE = it }
            }
        }
    }
    
    /**
     * Initialize Dyno with application context.
     * Should be called in Application.onCreate() or MainActivity.onCreate().
     * 
     * @param context Application context
     * @param debugMode Whether to enable debug features (usually BuildConfig.DEBUG)
     */
    fun initialize(context: Context, debugMode: Boolean = true) {
        if (isInitialized) {
            android.util.Log.w("DynoManager", "Dyno is already initialized")
            return
        }
        
        applicationContext = context.applicationContext
        isDebugMode = debugMode
        isInitialized = true
        
        if (debugMode) {
            // Initialize generated registry
            try {
                val registryClass = Class.forName("com.ardayucesan.dyno.generated.DynoRegistry")
                val registerAllMethod = registryClass.getDeclaredMethod("registerAll")
                registerAllMethod.isAccessible = true
                val result = registerAllMethod.invoke(null)
                android.util.Log.d("DynoManager", "Dyno initialized successfully with generated registry, result: $result")
            } catch (e: ClassNotFoundException) {
                android.util.Log.w("DynoManager", "DynoRegistry not found - using manual registration", e)
                // Manual registration as fallback
                initializeManualRegistry()
            } catch (e: NoSuchMethodException) {
                android.util.Log.w("DynoManager", "registerAll method not found - using manual registration", e)
                // Manual registration as fallback
                initializeManualRegistry()
            } catch (e: Exception) {
                android.util.Log.e("DynoManager", "Error initializing Dyno registry, falling back to manual", e)
                // Manual registration as fallback
                initializeManualRegistry()
            }
        }
    }
    
    /**
     * Manual registry initialization when KSP generated registry is not available.
     */
    private fun initializeManualRegistry() {
        android.util.Log.d("DynoManager", "Manual registry initialized - parameters will be detected at runtime")
    }
    
    /**
     * Register an object instance with Dyno for parameter monitoring.
     * Call this for each instance that has @DynoGroup annotation.
     */
    fun register(instance: Any) {
        if (!isInitialized || !isDebugMode) return
        
        DynoParameterRegistry.registerInstance(instance)
    }
    
    /**
     * Unregister an object instance from Dyno.
     * Call this in onDestroy() or when the instance is no longer needed.
     */
    fun unregister(instance: Any) {
        if (!isInitialized || !isDebugMode) return
        
        DynoParameterRegistry.unregisterInstance(instance)
    }
    
    /**
     * Show the Dyno debug interface.
     * This will show a notification that opens the debug UI when tapped.
     */
    fun showDebugInterface() {
        if (!isInitialized || !isDebugMode) return
        
        DynoNotificationHelper.showNotification(applicationContext)
        android.util.Log.d("DynoManager", "Debug interface notification shown")
    }
    
    /**
     * Hide the Dyno debug interface.
     */
    fun hideDebugInterface() {
        if (!isInitialized || !isDebugMode) return
        
        DynoNotificationHelper.hideNotification(applicationContext)
        android.util.Log.d("DynoManager", "Debug interface notification hidden")
    }
    
    /**
     * Check if Dyno is in debug mode.
     */
    fun isDebugMode(): Boolean = isDebugMode
    
    /**
     * Check if Dyno is initialized.
     */
    fun isInitialized(): Boolean = isInitialized
    
    /**
     * Get application context.
     */
    fun getContext(): Context {
        if (!::applicationContext.isInitialized) {
            throw IllegalStateException("Dyno not initialized. Call initialize() first.")
        }
        return applicationContext
    }
}

/**
 * Extension function to easily register an object with Dyno.
 * Usage: myObject.registerWithDyno()
 */
fun Any.registerWithDyno() {
    DynoManager.getInstance().register(this)
}

/**
 * Extension function to easily unregister an object from Dyno.
 * Usage: myObject.unregisterFromDyno()
 */
fun Any.unregisterFromDyno() {
    DynoManager.getInstance().unregister(this)
}