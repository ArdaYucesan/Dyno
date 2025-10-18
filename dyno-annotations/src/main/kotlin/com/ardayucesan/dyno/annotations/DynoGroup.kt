package com.ardayucesan.dyno.annotations

/**
 * Marks a class to be monitored by Dyno and groups all its exposed parameters.
 * 
 * @param name Display name for the group in debug UI. If empty, uses the class name.
 * @param description Optional description shown in the debug UI.
 * @param enabled Whether this group should be enabled by default.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DynoGroup(
    val name: String = "",
    val description: String = "",
    val enabled: Boolean = true
)