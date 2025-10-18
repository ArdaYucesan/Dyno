package com.ardayucesan.dyno.annotations

/**
 * Annotation for marking StateFlow/MutableStateFlow fields that contain data classes
 * which should be manipulatable in debug interface.
 * 
 * This allows direct manipulation of data class fields within StateFlow without
 * needing separate override variables.
 * 
 * Example:
 * ```
 * @DynoFlow(
 *     name = "Passenger Info",
 *     group = "Trip States", 
 *     fields = ["bookingStatus", "tripStatus", "isSearchingAgain"]
 * )
 * private val _passengerInfoFlow = MutableStateFlow<PassengerInfoModel?>(null)
 * ```
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class DynoFlow(
    val name: String = "",
    val group: String = "Default",
    val description: String = "",
    val fields: Array<String> = []
)