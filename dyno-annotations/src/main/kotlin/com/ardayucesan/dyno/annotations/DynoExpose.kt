package com.ardayucesan.dyno.annotations

/**
 * Marks a field or property to be exposed in Dyno debug interface for runtime modification.
 * 
 * @param name Display name in the debug UI. If empty, uses the field name.
 * @param group Group name to organize related parameters together.
 * @param description Optional description shown in the debug UI.
 * @param min Minimum value for numeric types.
 * @param max Maximum value for numeric types.
 * @param step Step size for numeric sliders.
 * @param enumMapping Map of int values to enum-like names. For example: ["0:None", "1:Active", "2:Paused"]
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class DynoExpose(
    val name: String = "",
    val group: String = "Default",
    val description: String = "",
    val min: Double = Double.NEGATIVE_INFINITY,
    val max: Double = Double.POSITIVE_INFINITY,
    val step: Double = 1.0,
    val enumMapping: Array<String> = []
)