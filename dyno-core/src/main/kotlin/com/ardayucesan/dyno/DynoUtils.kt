package com.ardayucesan.dyno

import android.content.Context
import com.ardayucesan.dyno.runtime.DynoParameterRegistry

/**
 * Utility functions for Dyno debugging library.
 */
object DynoUtils {
    
    /**
     * Get all currently registered parameters.
     * Useful for debugging and introspection.
     */
    fun getAllParameters() = DynoParameterRegistry.getAllParameters()
    
    /**
     * Get all currently registered triggers.
     * Useful for debugging and introspection.
     */
    fun getAllTriggers() = DynoParameterRegistry.getAllTriggers()
    
    /**
     * Get all currently registered groups.
     * Useful for debugging and introspection.
     */
    fun getAllGroups() = DynoParameterRegistry.getAllGroups()
    
    /**
     * Get current value of a specific parameter.
     * 
     * @param className Fully qualified class name
     * @param fieldName Field name
     * @return Current value or null if not found
     */
    fun getParameterValue(className: String, fieldName: String): Any? {
        return DynoParameterRegistry.getParameterValue(className, fieldName)
    }
    
    /**
     * Set value of a specific parameter.
     * 
     * @param className Fully qualified class name
     * @param fieldName Field name
     * @param value New value
     * @return true if successful, false otherwise
     */
    fun setParameterValue(className: String, fieldName: String, value: Any): Boolean {
        return DynoParameterRegistry.setParameterValue(className, fieldName, value)
    }
    
    /**
     * Trigger a specific method.
     * 
     * @param className Fully qualified class name
     * @param methodName Method name
     * @return true if successful, false otherwise
     */
    fun triggerMethod(className: String, methodName: String): Boolean {
        return DynoParameterRegistry.triggerMethod(className, methodName)
    }
    
    /**
     * Check if device is running in debug mode.
     * This is a helper function that checks various debug indicators.
     */
    fun isDeviceInDebugMode(context: Context): Boolean {
        return try {
            // Check if debugger is connected
            android.os.Debug.isDebuggerConnected() ||
            // Check if application is debuggable
            (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Clear all registrations.
     * Useful for testing or when reinitializing.
     */
    fun clearAll() {
        DynoParameterRegistry.clear()
    }
}