package com.ardayucesan.dyno.annotations

/**
 * Supported parameter types for Dyno debug interface.
 */
enum class DynoParameterType {
    BOOLEAN,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    STRING,
    ENUM
}

/**
 * Metadata for an exposed parameter.
 */
data class DynoExposedParameter(
    val name: String,
    val displayName: String,
    val group: String,
    val description: String,
    val type: DynoParameterType,
    val min: Double?,
    val max: Double?,
    val step: Double?,
    val enumValues: List<String>? = null,
    val className: String = "",
    val enumMapping: Map<Int, String>? = null
)

/**
 * Metadata for a trigger method.
 */
data class DynoTriggerMethod(
    val name: String,
    val displayName: String,
    val group: String,
    val description: String,
    val className: String = ""
)

/**
 * Metadata for a monitored class group.
 */
data class DynoGroupInfo(
    val name: String,
    val displayName: String,
    val description: String,
    val enabled: Boolean,
    val className: String,
    val parameters: List<DynoExposedParameter>,
    val triggers: List<DynoTriggerMethod>
)

/**
 * Metadata for a function parameter exposed by @DynoFunction.
 */
data class DynoFunctionParameter(
    val name: String,
    val displayName: String,
    val type: DynoParameterType,
    val defaultValue: Any?,
    val enumMapping: Map<Int, String>? = null,
    val min: Double? = null,
    val max: Double? = null,
    val step: Double? = null
)

/**
 * Metadata for a debuggable function marked with @DynoFunction.
 */
data class DynoDebugFunction(
    val name: String,
    val displayName: String,
    val group: String,
    val description: String,
    val className: String,
    val parameters: List<DynoFunctionParameter>,
    val exposeParameters: Boolean
)

/**
 * Metadata for a manipulatable field within a StateFlow data class.
 */
data class DynoFlowField(
    val name: String,
    val displayName: String,
    val type: DynoParameterType,
    val currentValue: Any?,
    val originalValue: Any?,
    val enumMapping: Map<Int, String>? = null
)

/**
 * Metadata for a StateFlow/MutableStateFlow marked with @DynoFlow.
 */
data class DynoFlowManipulation(
    val name: String,
    val displayName: String,
    val group: String,
    val description: String,
    val className: String,
    val fieldName: String,
    val dataClassName: String,
    val manipulableFields: List<DynoFlowField>
)