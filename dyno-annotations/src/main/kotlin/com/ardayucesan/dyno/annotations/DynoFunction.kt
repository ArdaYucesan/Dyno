package com.ardayucesan.dyno.annotations

/**
 * Marks a function to be debuggable in Dyno interface.
 * This annotation automatically exposes all function parameters for modification
 * and creates a trigger button to execute the function with custom parameter values.
 * 
 * @param name Display name in the debug UI. If empty, uses the function name.
 * @param group Group name to organize related functions together.
 * @param description Optional description shown in the debug UI.
 * @param exposeParameters Whether to automatically expose function parameters for modification.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DynoFunction(
    val name: String = "",
    val group: String = "Default",
    val description: String = "",
    val exposeParameters: Boolean = true
)