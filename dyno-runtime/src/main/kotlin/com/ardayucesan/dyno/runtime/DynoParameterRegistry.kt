package com.ardayucesan.dyno.runtime

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ardayucesan.dyno.annotations.*
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.reflect.KClass

/**
 * Central registry for all Dyno exposed parameters and trigger methods.
 * This class manages runtime parameter modification and method triggering.
 */
object DynoParameterRegistry {
    
    private val groups = mutableMapOf<String, DynoGroupInfo>()
    private val parameters = mutableMapOf<String, DynoExposedParameter>()
    private val triggers = mutableMapOf<String, DynoTriggerMethod>()
    private val debugFunctions = mutableMapOf<String, DynoDebugFunction>()
    private val functionParameters = mutableMapOf<String, MutableMap<String, Any?>>()
    private val instances = mutableMapOf<String, Any>()
    
    private val _parametersLiveData = MutableLiveData<Map<String, DynoExposedParameter>>()
    val parametersLiveData: LiveData<Map<String, DynoExposedParameter>> = _parametersLiveData
    
    private val _groupsLiveData = MutableLiveData<Map<String, DynoGroupInfo>>()
    val groupsLiveData: LiveData<Map<String, DynoGroupInfo>> = _groupsLiveData
    
    /**
     * Register a class group.
     */
    fun registerGroup(
        className: String,
        groupName: String,
        description: String,
        enabled: Boolean
    ) {
        val groupInfo = DynoGroupInfo(
            name = groupName,
            displayName = groupName,
            description = description,
            enabled = enabled,
            className = className,
            parameters = emptyList(),
            triggers = emptyList()
        )
        groups[className] = groupInfo
        _groupsLiveData.postValue(groups.toMap())
    }
    
    /**
     * Register an exposed parameter.
     */
    fun registerParameter(
        className: String,
        fieldName: String,
        displayName: String,
        group: String,
        description: String
    ) {
        registerParameterWithMapping(className, fieldName, displayName, group, description, null)
    }
    
    /**
     * Register an exposed parameter with enum mapping.
     */
    fun registerParameterWithMapping(
        className: String,
        fieldName: String,
        displayName: String,
        group: String,
        description: String,
        enumMapping: Map<Int, String>?
    ) {
        val parameterKey = "$className.$fieldName"
        
        try {
            val clazz = Class.forName(className)
            val field = clazz.getDeclaredField(fieldName)
            field.isAccessible = true
            
            val parameterType = when (field.type) {
                Boolean::class.java -> DynoParameterType.BOOLEAN
                Int::class.java -> DynoParameterType.INT
                Long::class.java -> DynoParameterType.LONG
                Float::class.java -> DynoParameterType.FLOAT
                Double::class.java -> DynoParameterType.DOUBLE
                String::class.java -> DynoParameterType.STRING
                else -> {
                    if (field.type.isEnum) {
                        DynoParameterType.ENUM
                    } else {
                        DynoParameterType.STRING // fallback
                    }
                }
            }
            
            val enumValues = if (parameterType == DynoParameterType.ENUM) {
                field.type.enumConstants?.map { it.toString() }
            } else null
            
            val parameter = DynoExposedParameter(
                name = fieldName,
                displayName = displayName,
                group = group,
                description = description,
                type = parameterType,
                min = null, // Will be extracted from annotation later if needed
                max = null,
                step = null,
                enumValues = enumValues,
                className = className,
                enumMapping = enumMapping
            )
            
            parameters[parameterKey] = parameter
            _parametersLiveData.postValue(parameters.toMap())
            
        } catch (e: Exception) {
            // Log error but don't crash
            android.util.Log.e("DynoParameterRegistry", "Error registering parameter $parameterKey", e)
        }
    }
    
    /**
     * Register a trigger method.
     */
    fun registerTrigger(
        className: String,
        methodName: String,
        displayName: String,
        group: String,
        description: String
    ) {
        val triggerKey = "$className.$methodName"
        
        val trigger = DynoTriggerMethod(
            name = methodName,
            displayName = displayName,
            group = group,
            description = description,
            className = className
        )
        
        triggers[triggerKey] = trigger
    }
    
    /**
     * Register a debug function.
     */
    fun registerDebugFunction(
        className: String,
        methodName: String,
        displayName: String,
        group: String,
        description: String,
        exposeParameters: Boolean
    ) {
        val functionKey = "$className.$methodName"
        
        android.util.Log.d("DynoParameterRegistry", "🔍 Registering debug function: $functionKey")
        
        try {
            val clazz = Class.forName(className)
            android.util.Log.d("DynoParameterRegistry", "✅ Found class: $className")
            
            // Find method by name (since there might be overloads)
            val method = clazz.declaredMethods.find { it.name == methodName }
            if (method == null) {
                android.util.Log.e("DynoParameterRegistry", "❌ Method $methodName not found in class $className")
                android.util.Log.d("DynoParameterRegistry", "Available methods: ${clazz.declaredMethods.map { it.name }}")
                return
            }
            
            method.isAccessible = true
            android.util.Log.d("DynoParameterRegistry", "✅ Found method: ${method.name} with ${method.parameterCount} parameters")
            
            // Extract function parameters if exposeParameters is true
            val functionParameters = if (exposeParameters && android.os.Build.VERSION.SDK_INT >= 26) {
                android.util.Log.d("DynoParameterRegistry", "📋 API >= 26, using method.parameters for parameter extraction")
                try {
                    val params = method.parameters.mapIndexed { index, param ->
                        val paramType = when (param.type) {
                            Boolean::class.java -> DynoParameterType.BOOLEAN
                            Int::class.java -> DynoParameterType.INT
                            Long::class.java -> DynoParameterType.LONG
                            Float::class.java -> DynoParameterType.FLOAT
                            Double::class.java -> DynoParameterType.DOUBLE
                            String::class.java -> DynoParameterType.STRING
                            else -> {
                                if (param.type.isEnum) {
                                    DynoParameterType.ENUM
                                } else {
                                    DynoParameterType.STRING // fallback
                                }
                            }
                        }
                        
                        val paramName = param.name ?: "param$index"
                        android.util.Log.d("DynoParameterRegistry", "   Parameter $index: $paramName (${param.type.simpleName}) -> $paramType")
                        
                        DynoFunctionParameter(
                            name = paramName,
                            displayName = paramName,
                            type = paramType,
                            defaultValue = getDefaultValueForType(paramType)
                        )
                    }
                    android.util.Log.d("DynoParameterRegistry", "✅ Successfully extracted ${params.size} parameters using method.parameters")
                    params
                } catch (e: Exception) {
                    android.util.Log.w("DynoParameterRegistry", "⚠️ method.parameters failed, falling back to parameterTypes", e)
                    // Fallback for older API levels or reflection issues
                    val params = method.parameterTypes.mapIndexed { index, paramType ->
                        val dynoType = when (paramType) {
                            Boolean::class.java -> DynoParameterType.BOOLEAN
                            Int::class.java -> DynoParameterType.INT
                            Long::class.java -> DynoParameterType.LONG
                            Float::class.java -> DynoParameterType.FLOAT
                            Double::class.java -> DynoParameterType.DOUBLE
                            String::class.java -> DynoParameterType.STRING
                            else -> {
                                if (paramType.isEnum) {
                                    DynoParameterType.ENUM
                                } else {
                                    DynoParameterType.STRING // fallback
                                }
                            }
                        }
                        
                        val paramName = "param$index"
                        android.util.Log.d("DynoParameterRegistry", "   Parameter $index: $paramName (${paramType.simpleName}) -> $dynoType")
                        
                        DynoFunctionParameter(
                            name = paramName,
                            displayName = "Parameter $index",
                            type = dynoType,
                            defaultValue = getDefaultValueForType(dynoType)
                        )
                    }
                    android.util.Log.d("DynoParameterRegistry", "✅ Successfully extracted ${params.size} parameters using parameterTypes fallback")
                    params
                }
            } else if (exposeParameters) {
                // Fallback for API < 26
                android.util.Log.d("DynoParameterRegistry", "📋 API < 26, using parameterTypes for parameter extraction")
                val params = method.parameterTypes.mapIndexed { index, paramType ->
                    val dynoType = when (paramType) {
                        Boolean::class.java -> DynoParameterType.BOOLEAN
                        Int::class.java -> DynoParameterType.INT
                        Long::class.java -> DynoParameterType.LONG
                        Float::class.java -> DynoParameterType.FLOAT
                        Double::class.java -> DynoParameterType.DOUBLE
                        String::class.java -> DynoParameterType.STRING
                        else -> {
                            if (paramType.isEnum) {
                                DynoParameterType.ENUM
                            } else {
                                DynoParameterType.STRING // fallback
                            }
                        }
                    }
                    
                    val paramName = "param$index"
                    android.util.Log.d("DynoParameterRegistry", "   Parameter $index: $paramName (${paramType.simpleName}) -> $dynoType")
                    
                    DynoFunctionParameter(
                        name = paramName,
                        displayName = "Parameter $index",
                        type = dynoType,
                        defaultValue = getDefaultValueForType(dynoType)
                    )
                }
                android.util.Log.d("DynoParameterRegistry", "✅ Successfully extracted ${params.size} parameters using parameterTypes")
                params
            } else {
                android.util.Log.d("DynoParameterRegistry", "⚠️ exposeParameters=false, no parameters extracted")
                emptyList()
            }
            
            val debugFunction = DynoDebugFunction(
                name = methodName,
                displayName = displayName,
                group = group,
                description = description,
                className = className,
                parameters = functionParameters,
                exposeParameters = exposeParameters
            )
            
            debugFunctions[functionKey] = debugFunction
            android.util.Log.d("DynoParameterRegistry", "✅ Registered debug function $functionKey with ${functionParameters.size} parameters")
            
            // Initialize function parameter values
            if (exposeParameters && functionParameters.isNotEmpty()) {
                val paramMap = functionParameters.associate { it.name to it.defaultValue }.toMutableMap()
                this.functionParameters[functionKey] = paramMap
                android.util.Log.d("DynoParameterRegistry", "📋 Initialized parameter values: $paramMap")
            } else {
                android.util.Log.d("DynoParameterRegistry", "⚠️ No parameters to initialize (exposeParameters=$exposeParameters, paramCount=${functionParameters.size})")
            }
            
        } catch (e: Exception) {
            // Log error but don't crash
            android.util.Log.e("DynoParameterRegistry", "❌ Error registering debug function $functionKey", e)
        }
    }
    
    private fun getDefaultValueForType(type: DynoParameterType): Any? {
        return when (type) {
            DynoParameterType.BOOLEAN -> false
            DynoParameterType.INT -> 0
            DynoParameterType.LONG -> 0L
            DynoParameterType.FLOAT -> 0.0f
            DynoParameterType.DOUBLE -> 0.0
            DynoParameterType.STRING -> ""
            DynoParameterType.ENUM -> null
        }
    }
    
    /**
     * Register an instance of a class that has Dyno annotations.
     */
    fun registerInstance(instance: Any) {
        val className = instance::class.java.name
        instances[className] = instance
        
        // Runtime annotation scanning
        //TODO: gerek yok böyle bir fonksiyona, ksp zaten compile-time'da bunu yapıyor hepsi için DynoRegistry'e kayıt oluşturuyor.
        scanInstanceAnnotations(instance)
        
        android.util.Log.d("DynoParameterRegistry", "Registered instance of $className")
    }
    
    /**
     * Scan instance for Dyno annotations at runtime.
     */
    private fun scanInstanceAnnotations(instance: Any) {
        val clazz = instance::class.java
        val className = clazz.name
        
        android.util.Log.d("DynoParameterRegistry", "Scanning annotations for $className")
        
        // Check for @DynoGroup
        val dynoGroup = clazz.getAnnotation(com.ardayucesan.dyno.annotations.DynoGroup::class.java)
        if (dynoGroup != null) {
            android.util.Log.d("DynoParameterRegistry", "Found @DynoGroup: ${dynoGroup.name}")
            registerGroup(className, dynoGroup.name.ifEmpty { clazz.simpleName }, dynoGroup.description, dynoGroup.enabled)
        } else {
            android.util.Log.w("DynoParameterRegistry", "No @DynoGroup found on $className")
        }
        
        // Scan fields for @DynoExpose
        var foundFields = 0
        clazz.declaredFields.forEach { field ->
            val dynoExpose = field.getAnnotation(com.ardayucesan.dyno.annotations.DynoExpose::class.java)
            if (dynoExpose != null) {
                foundFields++
                android.util.Log.d("DynoParameterRegistry", "Found @DynoExpose on field: ${field.name}")
                
                // Parse enum mapping from annotation
                val enumMapping = if (dynoExpose.enumMapping.isNotEmpty()) {
                    dynoExpose.enumMapping.mapNotNull { mapping ->
                        val parts = mapping.split(":")
                        if (parts.size == 2) {
                            val intValue = parts[0].toIntOrNull()
                            val enumName = parts[1]
                            if (intValue != null) intValue to enumName else null
                        } else null
                    }.toMap()
                } else null
                
                registerParameterWithMapping(
                    className = className,
                    fieldName = field.name,
                    displayName = dynoExpose.name.ifEmpty { field.name },
                    group = dynoExpose.group,
                    description = dynoExpose.description,
                    enumMapping = enumMapping
                )
            }
        }
        android.util.Log.d("DynoParameterRegistry", "Total @DynoExpose fields found: $foundFields")
        
        // Scan methods for @DynoTrigger
        var foundMethods = 0
        clazz.declaredMethods.forEach { method ->
            val dynoTrigger = method.getAnnotation(com.ardayucesan.dyno.annotations.DynoTrigger::class.java)
            if (dynoTrigger != null) {
                foundMethods++
                android.util.Log.d("DynoParameterRegistry", "Found @DynoTrigger on method: ${method.name}")
                registerTrigger(
                    className = className,
                    methodName = method.name,
                    displayName = dynoTrigger.name.ifEmpty { method.name },
                    group = dynoTrigger.group,
                    description = dynoTrigger.description
                )
            }
        }
        android.util.Log.d("DynoParameterRegistry", "Total @DynoTrigger methods found: $foundMethods")
        
        // Scan methods for @DynoFunction
        var foundFunctions = 0
        clazz.declaredMethods.forEach { method ->
            val dynoFunction = method.getAnnotation(com.ardayucesan.dyno.annotations.DynoFunction::class.java)
            if (dynoFunction != null) {
                foundFunctions++
                android.util.Log.d("DynoParameterRegistry", "Found @DynoFunction on method: ${method.name}")
                registerDebugFunction(
                    className = className,
                    methodName = method.name,
                    displayName = dynoFunction.name.ifEmpty { method.name },
                    group = dynoFunction.group,
                    description = dynoFunction.description,
                    exposeParameters = dynoFunction.exposeParameters
                )
            }
        }
        android.util.Log.d("DynoParameterRegistry", "Total @DynoFunction methods found: $foundFunctions")
        android.util.Log.d("DynoParameterRegistry", "Annotation scanning completed for $className")
    }
    
    /**
     * Unregister an instance.
     */
    fun unregisterInstance(instance: Any) {
        val className = instance::class.java.name
        instances.remove(className)
        android.util.Log.d("DynoParameterRegistry", "Unregistered instance of $className")
    }
    
    /**
     * Get current value of a parameter.
     */
    fun getParameterValue(className: String, fieldName: String): Any? {
        val instance = instances[className] ?: return null
        val parameterKey = "$className.$fieldName"
        
        return try {
            val field = instance::class.java.getDeclaredField(fieldName)
            field.isAccessible = true
            field.get(instance)
        } catch (e: Exception) {
            android.util.Log.e("DynoParameterRegistry", "Error getting parameter value $parameterKey", e)
            null
        }
    }
    
    /**
     * Set value of a parameter.
     */
    fun setParameterValue(className: String, fieldName: String, value: Any): Boolean {
        val instance = instances[className] ?: return false
        val parameterKey = "$className.$fieldName"
        
        return try {
            val field = instance::class.java.getDeclaredField(fieldName)
            field.isAccessible = true

            // Type conversion
            val convertedValue = when (field.type) {
                Boolean::class.java -> value.toString().toBoolean()
                Int::class.java -> when (value) {
                    is Number -> value.toInt()
                    is String -> value.toInt()
                    else -> value
                }
                Long::class.java -> when (value) {
                    is Number -> value.toLong()
                    is String -> value.toLong()
                    else -> value
                }
                Float::class.java -> when (value) {
                    is Number -> value.toFloat()
                    is String -> value.toFloat()
                    else -> value
                }
                Double::class.java -> when (value) {
                    is Number -> value.toDouble()
                    is String -> value.toDouble()
                    else -> value
                }
                String::class.java -> value.toString()
                else -> {
                    if (field.type.isEnum) {
                        field.type.enumConstants.find { it.toString() == value.toString() } ?: value
                    } else value
                }
            }
            
            field.set(instance, convertedValue)
            android.util.Log.d("DynoParameterRegistry", "Set parameter $parameterKey = $convertedValue")
            
            // Notify LiveData observers that parameters have changed
            _parametersLiveData.postValue(parameters.toMap())
            
            true
        } catch (e: Exception) {
            android.util.Log.e("DynoParameterRegistry", "Error setting parameter value $parameterKey", e)
            false
        }
    }
    
    /**
     * Trigger a method.
     */
    fun triggerMethod(className: String, methodName: String): Boolean {
        android.util.Log.d("DynoParameterRegistry", "triggerMethod called: $className.$methodName")
        
        val instance = instances[className]
        if (instance == null) {
            android.util.Log.e("DynoParameterRegistry", "No instance found for class: $className")
            android.util.Log.d("DynoParameterRegistry", "Available instances: ${instances.keys}")
            return false
        }
        
        val triggerKey = "$className.$methodName"
        
        return try {
            android.util.Log.d("DynoParameterRegistry", "Attempting to invoke method: $methodName on $className")
            val method = instance::class.java.getDeclaredMethod(methodName)
            method.isAccessible = true
            method.invoke(instance)
            android.util.Log.d("DynoParameterRegistry", "✅ Successfully triggered method $triggerKey")
            true
        } catch (e: Exception) {
            android.util.Log.e("DynoParameterRegistry", "❌ Error triggering method $triggerKey", e)
            false
        }
    }
    
    /**
     * Get all registered parameters.
     */
    fun getAllParameters(): Map<String, DynoExposedParameter> = parameters.toMap()
    
    /**
     * Get all registered triggers.
     */
    fun getAllTriggers(): Map<String, DynoTriggerMethod> = triggers.toMap()
    
    /**
     * Get all registered debug functions.
     */
    fun getAllDebugFunctions(): Map<String, DynoDebugFunction> = debugFunctions.toMap()
    
    /**
     * Get function parameter value.
     */
    fun getFunctionParameterValue(className: String, methodName: String, parameterName: String): Any? {
        val functionKey = "$className.$methodName"
        return functionParameters[functionKey]?.get(parameterName)
    }
    
    /**
     * Set function parameter value.
     */
    fun setFunctionParameterValue(className: String, methodName: String, parameterName: String, value: Any): Boolean {
        val functionKey = "$className.$methodName"
        val paramMap = functionParameters[functionKey] ?: return false
        
        // Find the parameter metadata for type conversion
        val debugFunction = debugFunctions[functionKey] ?: return false
        val parameterMetadata = debugFunction.parameters.find { it.name == parameterName } ?: return false
        
        return try {
            // Type conversion
            val convertedValue = when (parameterMetadata.type) {
                DynoParameterType.BOOLEAN -> value.toString().toBoolean()
                DynoParameterType.INT -> when (value) {
                    is Number -> value.toInt()
                    is String -> value.toInt()
                    else -> value
                }
                DynoParameterType.LONG -> when (value) {
                    is Number -> value.toLong()
                    is String -> value.toLong()
                    else -> value
                }
                DynoParameterType.FLOAT -> when (value) {
                    is Number -> value.toFloat()
                    is String -> value.toFloat()
                    else -> value
                }
                DynoParameterType.DOUBLE -> when (value) {
                    is Number -> value.toDouble()
                    is String -> value.toDouble()
                    else -> value
                }
                DynoParameterType.STRING -> value.toString()
                DynoParameterType.ENUM -> value // TODO: Handle enum conversion
            }
            
            paramMap[parameterName] = convertedValue
            android.util.Log.d("DynoParameterRegistry", "Set function parameter $functionKey.$parameterName = $convertedValue")
            true
        } catch (e: Exception) {
            android.util.Log.e("DynoParameterRegistry", "Error setting function parameter $functionKey.$parameterName", e)
            false
        }
    }
    
    /**
     * Trigger a debug function with current parameter values.
     */
    fun triggerDebugFunction(className: String, methodName: String): Boolean {
        android.util.Log.d("DynoParameterRegistry", "triggerDebugFunction called: $className.$methodName")
        
        val instance = instances[className]
        if (instance == null) {
            android.util.Log.e("DynoParameterRegistry", "No instance found for class: $className")
            return false
        }
        
        val functionKey = "$className.$methodName"
        val debugFunction = debugFunctions[functionKey]
        if (debugFunction == null) {
            android.util.Log.e("DynoParameterRegistry", "No debug function found: $functionKey")
            return false
        }
        
        return try {
            // Find the method by scanning all methods with matching name
            val method = instance::class.java.declaredMethods.find { it.name == methodName }
            if (method == null) {
                android.util.Log.e("DynoParameterRegistry", "Method $methodName not found in class $className")
                return false
            }
            
            method.isAccessible = true
            
            if (debugFunction.exposeParameters && debugFunction.parameters.isNotEmpty()) {
                // Get parameter values and invoke with them
                val paramValues = debugFunction.parameters.map { param ->
                    functionParameters[functionKey]?.get(param.name) ?: param.defaultValue
                }.toTypedArray()
                
                android.util.Log.d("DynoParameterRegistry", "Invoking $functionKey with parameters: ${paramValues.contentToString()}")
                method.invoke(instance, *paramValues)
            } else {
                // Invoke without parameters
                method.invoke(instance)
            }
            
            android.util.Log.d("DynoParameterRegistry", "✅ Successfully triggered debug function $functionKey")
            true
        } catch (e: Exception) {
            android.util.Log.e("DynoParameterRegistry", "❌ Error triggering debug function $functionKey", e)
            false
        }
    }
    
    /**
     * Get all registered groups.
     */
    fun getAllGroups(): Map<String, DynoGroupInfo> = groups.toMap()
    
    /**
     * Clear all registrations.
     */
    fun clear() {
        groups.clear()
        parameters.clear()
        triggers.clear()
        debugFunctions.clear()
        functionParameters.clear()
        instances.clear()
        _parametersLiveData.postValue(emptyMap())
        _groupsLiveData.postValue(emptyMap())
    }
}