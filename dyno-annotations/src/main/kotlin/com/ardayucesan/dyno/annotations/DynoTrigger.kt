package com.ardayucesan.dyno.annotations

/**
 * Marks a method to be triggered from Dyno debug interface.
 * 
 * @param name Display name in the debug UI. If empty, uses the method name.
 * @param group Group name to organize related triggers together.
 * @param description Optional description shown in the debug UI.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DynoTrigger(
    val name: String = "",
    val group: String = "Default",
    val description: String = ""
)