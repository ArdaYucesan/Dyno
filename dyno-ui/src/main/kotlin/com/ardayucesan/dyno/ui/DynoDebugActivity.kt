package com.ardayucesan.dyno.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ardayucesan.dyno.annotations.*
import com.ardayucesan.dyno.runtime.DynoParameterRegistry
import com.ardayucesan.dyno.ui.theme.DynoTheme

/**
 * Debug activity that shows all exposed parameters and triggers.
 * Provides UI for real-time parameter modification.
 */
class DynoDebugActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            DynoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DynoDebugScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynoDebugScreen() {
    val parameters by DynoParameterRegistry.parametersLiveData.observeAsState(emptyMap())
    val groups by DynoParameterRegistry.groupsLiveData.observeAsState(emptyMap())
    val triggers = DynoParameterRegistry.getAllTriggers()
    val debugFunctions = DynoParameterRegistry.getAllDebugFunctions()
    val flowManipulations = DynoParameterRegistry.getAllFlowManipulations()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Professional Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dyno Logo/Icon
                Surface(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "D",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Dyno",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Debug Interface",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Status indicator
                Surface(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "ACTIVE",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (parameters.isEmpty() && triggers.isEmpty() && debugFunctions.isEmpty() && flowManipulations.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No parameters found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Make sure to register your instances with DynoManager.register()",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            // Content
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Group parameters by group name
                val parametersByGroup = parameters.values.groupBy { it.group }
                val triggersByGroup = triggers.values.groupBy { it.group }
                val debugFunctionsByGroup = debugFunctions.values.groupBy { it.group }
                val flowManipulationsByGroup = flowManipulations.values.groupBy { it.group }
                val allGroups = (parametersByGroup.keys + triggersByGroup.keys + debugFunctionsByGroup.keys + flowManipulationsByGroup.keys).distinct()
                
                items(allGroups) { groupName ->
                    DynoGroupCard(
                        groupName = groupName,
                        parameters = parametersByGroup[groupName] ?: emptyList(),
                        triggers = triggersByGroup[groupName] ?: emptyList(),
                        debugFunctions = debugFunctionsByGroup[groupName] ?: emptyList(),
                        flowManipulations = flowManipulationsByGroup[groupName] ?: emptyList()
                    )
                }
            }
        }
    }
}

@Composable
fun DynoGroupCard(
    groupName: String,
    parameters: List<DynoExposedParameter>,
    triggers: List<DynoTriggerMethod>,
    debugFunctions: List<DynoDebugFunction>,
    flowManipulations: List<DynoFlowManipulation>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Professional Group Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.size(8.dp)
                ) {}
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = groupName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Parameter, trigger, function and flow count
                if (parameters.isNotEmpty() || triggers.isNotEmpty() || debugFunctions.isNotEmpty() || flowManipulations.isNotEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${parameters.size + triggers.size + debugFunctions.size + flowManipulations.size}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            if (parameters.isNotEmpty() || triggers.isNotEmpty() || debugFunctions.isNotEmpty() || flowManipulations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Parameters Section
            parameters.forEach { parameter ->
                DynoParameterRow(parameter = parameter)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Triggers Section
            if (triggers.isNotEmpty() && parameters.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }
            
            triggers.forEach { trigger ->
                DynoTriggerRow(trigger = trigger)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Debug Functions Section
            if (debugFunctions.isNotEmpty() && (parameters.isNotEmpty() || triggers.isNotEmpty())) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }
            
            debugFunctions.forEach { debugFunction ->
                DynoDebugFunctionRow(debugFunction = debugFunction)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Flow Manipulations Section
            if (flowManipulations.isNotEmpty() && (parameters.isNotEmpty() || triggers.isNotEmpty() || debugFunctions.isNotEmpty())) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }
            
            flowManipulations.forEach { flowManipulation ->
                DynoFlowManipulationRow(flowManipulation = flowManipulation)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun DynoParameterRow(parameter: DynoExposedParameter) {
    Column {
        Text(
            text = parameter.displayName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        
        if (parameter.description.isNotEmpty()) {
            Text(
                text = parameter.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        when (parameter.type) {
            DynoParameterType.BOOLEAN -> {
                DynoBooleanParameter(parameter)
            }
            DynoParameterType.INT -> {
                DynoIntParameter(parameter)
            }
            DynoParameterType.LONG -> {
                DynoLongParameter(parameter)
            }
            DynoParameterType.FLOAT -> {
                DynoFloatParameter(parameter)
            }
            DynoParameterType.DOUBLE -> {
                DynoDoubleParameter(parameter)
            }
            DynoParameterType.STRING -> {
                DynoStringParameter(parameter)
            }
            DynoParameterType.ENUM -> {
                DynoEnumParameter(parameter)
            }
        }
    }
}

@Composable
fun DynoBooleanParameter(parameter: DynoExposedParameter) {
    // Observe parameters LiveData for reactive updates
    val parameters by DynoParameterRegistry.parametersLiveData.observeAsState(emptyMap())
    
    // Get current value from registry
    val registryValue = DynoParameterRegistry.getParameterValue(parameter.className, parameter.name) as? Boolean ?: false
    
    // Use local state for immediate UI feedback
    var localValue by remember(registryValue) { mutableStateOf(registryValue) }
    
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = localValue,
            onCheckedChange = { newValue ->
                localValue = newValue // Immediate UI update
                // Update value in registry - this will trigger LiveData update
                DynoParameterRegistry.setParameterValue(parameter.className, parameter.name, newValue)
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = localValue.toString())
    }
}

@Composable
fun DynoIntParameter(parameter: DynoExposedParameter) {
    // Observe parameters LiveData for reactive updates
    val parameters by DynoParameterRegistry.parametersLiveData.observeAsState(emptyMap())
    
    // Get current value from registry
    val registryValue = remember(parameters) {
        DynoParameterRegistry.getParameterValue(parameter.className, parameter.name) as? Int ?: 0
    }
    
    // If parameter has enum mapping, show dropdown, otherwise show text field
    val enumMapping = parameter.enumMapping
    if (enumMapping != null && enumMapping.isNotEmpty()) {
        DynoIntEnumParameter(parameter, registryValue)
    } else {
        var currentValue by remember(registryValue) { mutableStateOf(registryValue.toString()) }
        
        OutlinedTextField(
            value = currentValue,
            onValueChange = { newValue ->
                if (newValue.toIntOrNull() != null || newValue.isEmpty()) {
                    currentValue = newValue
                    // Update value in registry
                    newValue.toIntOrNull()?.let { intValue ->
                        DynoParameterRegistry.setParameterValue(parameter.className, parameter.name, intValue)
                    }
                }
            },
            label = { Text("Value") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DynoLongParameter(parameter: DynoExposedParameter) {
    var currentValue by remember { mutableStateOf("0") }
    
    OutlinedTextField(
        value = currentValue,
        onValueChange = { newValue ->
            if (newValue.toLongOrNull() != null || newValue.isEmpty()) {
                currentValue = newValue
                // TODO: Update value in registry
            }
        },
        label = { Text("Value") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DynoFloatParameter(parameter: DynoExposedParameter) {
    var currentValue by remember { mutableStateOf("0.0") }
    
    OutlinedTextField(
        value = currentValue,
        onValueChange = { newValue ->
            if (newValue.toFloatOrNull() != null || newValue.isEmpty()) {
                currentValue = newValue
                // TODO: Update value in registry
            }
        },
        label = { Text("Value") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DynoDoubleParameter(parameter: DynoExposedParameter) {
    var currentValue by remember { mutableStateOf("0.0") }
    
    OutlinedTextField(
        value = currentValue,
        onValueChange = { newValue ->
            if (newValue.toDoubleOrNull() != null || newValue.isEmpty()) {
                currentValue = newValue
                // TODO: Update value in registry
            }
        },
        label = { Text("Value") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DynoStringParameter(parameter: DynoExposedParameter) {
    var currentValue by remember { mutableStateOf("") }
    
    OutlinedTextField(
        value = currentValue,
        onValueChange = { newValue ->
            currentValue = newValue
            // TODO: Update value in registry
        },
        label = { Text("Value") },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynoEnumParameter(parameter: DynoExposedParameter) {
    val enumValues = parameter.enumValues ?: emptyList()
    
    // Observe parameters LiveData for reactive updates
    val parameters by DynoParameterRegistry.parametersLiveData.observeAsState(emptyMap())
    
    // Get current value from registry - no remember needed, will recompose when LiveData changes
    val selectedValue = DynoParameterRegistry.getParameterValue(parameter.className, parameter.name)?.toString() ?: enumValues.firstOrNull() ?: ""
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text("Value") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            enumValues.forEach { value ->
                DropdownMenuItem(
                    text = { Text(value) },
                    onClick = {
                        expanded = false
                        // Update value in registry - this will trigger LiveData update
                        DynoParameterRegistry.setParameterValue(parameter.className, parameter.name, value)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynoIntEnumParameter(parameter: DynoExposedParameter, initialIntValue: Int) {
    val enumMapping = parameter.enumMapping ?: emptyMap()
    var expanded by remember { mutableStateOf(false) }
    
    // Observe parameters LiveData for reactive updates
    val parameters by DynoParameterRegistry.parametersLiveData.observeAsState(emptyMap())
    
    // Get current value from registry - no remember needed, will recompose when LiveData changes
    val currentIntValue = DynoParameterRegistry.getParameterValue(parameter.className, parameter.name) as? Int ?: initialIntValue
    
    // Get current enum name from int value, with fallback
    val currentEnumName = enumMapping[currentIntValue] ?: "Unknown"
    val displayValue = "$currentEnumName ($currentIntValue)"
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = displayValue,
            onValueChange = {},
            readOnly = true,
            label = { Text("Value") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            enumMapping.entries.sortedBy { it.key }.forEach { (intValue, enumName) ->
                DropdownMenuItem(
                    text = { Text("$enumName ($intValue)") },
                    onClick = {
                        expanded = false
                        // Update value in registry
                        DynoParameterRegistry.setParameterValue(parameter.className, parameter.name, intValue)
                    }
                )
            }
        }
    }
}

@Composable
fun DynoTriggerRow(trigger: DynoTriggerMethod) {
    Column {
        Text(
            text = trigger.displayName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        
        if (trigger.description.isNotEmpty()) {
            Text(
                text = trigger.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Button(
            onClick = {
                android.util.Log.d("DynoDebugActivity", "Trigger button clicked: ${trigger.displayName}")
                val success = DynoParameterRegistry.triggerMethod(trigger.className, trigger.name)
                android.util.Log.d("DynoDebugActivity", "Trigger result: $success")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 2.dp
            )
        ) {
            Text(
                text = trigger.displayName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun DynoDebugFunctionRow(debugFunction: DynoDebugFunction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Function header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🚀",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = debugFunction.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                if (debugFunction.parameters.isNotEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${debugFunction.parameters.size} params",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            if (debugFunction.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = debugFunction.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
            
            // Function parameters (if exposeParameters is true)
            if (debugFunction.exposeParameters && debugFunction.parameters.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Parameters:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                debugFunction.parameters.forEach { param ->
                    DynoFunctionParameterRow(
                        debugFunction = debugFunction,
                        parameter = param
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // Trigger button
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = {
                    android.util.Log.d("DynoDebugActivity", "Debug function button clicked: ${debugFunction.displayName}")
                    val success = DynoParameterRegistry.triggerDebugFunction(debugFunction.className, debugFunction.name)
                    android.util.Log.d("DynoDebugActivity", "Debug function result: $success")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text(
                    text = "Trigger ${debugFunction.displayName}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun DynoFunctionParameterRow(
    debugFunction: DynoDebugFunction,
    parameter: DynoFunctionParameter
) {
    Column {
        Text(
            text = parameter.displayName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        when (parameter.type) {
            DynoParameterType.BOOLEAN -> {
                DynoFunctionBooleanParameter(debugFunction, parameter)
            }
            DynoParameterType.INT -> {
                DynoFunctionIntParameter(debugFunction, parameter)
            }
            DynoParameterType.STRING -> {
                DynoFunctionStringParameter(debugFunction, parameter)
            }
            else -> {
                // For other types, fall back to string input
                DynoFunctionStringParameter(debugFunction, parameter)
            }
        }
    }
}

@Composable
fun DynoFunctionBooleanParameter(
    debugFunction: DynoDebugFunction,
    parameter: DynoFunctionParameter
) {
    val currentValue = DynoParameterRegistry.getFunctionParameterValue(
        debugFunction.className, 
        debugFunction.name, 
        parameter.name
    ) as? Boolean ?: (parameter.defaultValue as? Boolean ?: false)
    
    var localValue by remember(currentValue) { mutableStateOf(currentValue) }
    
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = localValue,
            onCheckedChange = { newValue ->
                localValue = newValue
                DynoParameterRegistry.setFunctionParameterValue(
                    debugFunction.className, 
                    debugFunction.name, 
                    parameter.name, 
                    newValue
                )
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = localValue.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun DynoFunctionIntParameter(
    debugFunction: DynoDebugFunction,
    parameter: DynoFunctionParameter
) {
    val currentValue = DynoParameterRegistry.getFunctionParameterValue(
        debugFunction.className, 
        debugFunction.name, 
        parameter.name
    ) as? Int ?: (parameter.defaultValue as? Int ?: 0)
    
    var stringValue by remember(currentValue) { mutableStateOf(currentValue.toString()) }
    
    OutlinedTextField(
        value = stringValue,
        onValueChange = { newValue ->
            if (newValue.toIntOrNull() != null || newValue.isEmpty()) {
                stringValue = newValue
                newValue.toIntOrNull()?.let { intValue ->
                    DynoParameterRegistry.setFunctionParameterValue(
                        debugFunction.className, 
                        debugFunction.name, 
                        parameter.name, 
                        intValue
                    )
                }
            }
        },
        label = { Text("Value") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun DynoFunctionStringParameter(
    debugFunction: DynoDebugFunction,
    parameter: DynoFunctionParameter
) {
    val currentValue = DynoParameterRegistry.getFunctionParameterValue(
        debugFunction.className, 
        debugFunction.name, 
        parameter.name
    ) as? String ?: (parameter.defaultValue as? String ?: "")
    
    var stringValue by remember(currentValue) { mutableStateOf(currentValue) }
    
    OutlinedTextField(
        value = stringValue,
        onValueChange = { newValue ->
            stringValue = newValue
            DynoParameterRegistry.setFunctionParameterValue(
                debugFunction.className, 
                debugFunction.name, 
                parameter.name, 
                newValue
            )
        },
        label = { Text("Value") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun DynoFlowManipulationRow(flowManipulation: DynoFlowManipulation) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Flow header
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon indicator
                    Surface(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🔄",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = flowManipulation.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (flowManipulation.description.isNotEmpty()) {
                            Text(
                                text = flowManipulation.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${flowManipulation.manipulableFields.size} fields",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    
                    // Expand/collapse icon with modern styling
                    Surface(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (expanded) "▼" else "▶",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Flow fields
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                if (flowManipulation.manipulableFields.isEmpty()) {
                    Text(
                        text = "No fields available for manipulation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    flowManipulation.manipulableFields.forEach { field ->
                        DynoFlowFieldRow(
                            flowManipulation = flowManipulation,
                            field = field
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DynoFlowFieldRow(
    flowManipulation: DynoFlowManipulation,
    field: DynoFlowField
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = field.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "Override: ${DynoParameterRegistry.getFlowFieldOverride(flowManipulation.className, flowManipulation.fieldName, field.name) ?: "None"} | Original: ${field.originalValue ?: "N/A"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Input field based on type
        when (field.type) {
            DynoParameterType.BOOLEAN -> {
                val currentOverride = DynoParameterRegistry.getFlowFieldOverride(
                    flowManipulation.className,
                    flowManipulation.fieldName,
                    field.name
                )
                val originalValue = (field.currentValue as? Boolean) ?: false
                val currentValue = (currentOverride as? Boolean) ?: originalValue
                
                var switchState by remember(currentValue) { mutableStateOf(currentValue) }
                
                Switch(
                    checked = switchState,
                    onCheckedChange = { newValue ->
                        switchState = newValue
                        val success = DynoParameterRegistry.setFlowFieldOverride(
                            flowManipulation.className,
                            flowManipulation.fieldName,
                            field.name,
                            newValue
                        )
                        android.util.Log.d("DynoDebugActivity", "Boolean switch changed: ${field.name} = $newValue, success = $success")
                    }
                )
            }
            DynoParameterType.INT -> {
                val currentOverride = DynoParameterRegistry.getFlowFieldOverride(
                    flowManipulation.className,
                    flowManipulation.fieldName,
                    field.name
                )
                val originalValue = (field.currentValue as? Int) ?: 0
                val currentValue = (currentOverride as? Int) ?: originalValue
                var textValue by remember(currentValue) { mutableStateOf(currentValue.toString()) }
                
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { newValue ->
                        textValue = newValue
                        newValue.toIntOrNull()?.let { intValue ->
                            val success = DynoParameterRegistry.setFlowFieldOverride(
                                flowManipulation.className,
                                flowManipulation.fieldName,
                                field.name,
                                intValue
                            )
                            android.util.Log.d("DynoDebugActivity", "Int field changed: ${field.name} = $intValue, success = $success")
                        }
                    },
                    modifier = Modifier.width(120.dp),
                    singleLine = true,
                    label = { Text("Value") }
                )
            }
            DynoParameterType.STRING -> {
                val currentOverride = DynoParameterRegistry.getFlowFieldOverride(
                    flowManipulation.className,
                    flowManipulation.fieldName,
                    field.name
                )
                val currentValue = (currentOverride as? String) ?: (field.currentValue as? String) ?: ""
                var textValue by remember(currentValue) { mutableStateOf(currentValue) }
                
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { newValue ->
                        textValue = newValue
                        DynoParameterRegistry.setFlowFieldOverride(
                            flowManipulation.className,
                            flowManipulation.fieldName,
                            field.name,
                            newValue
                        )
                    },
                    modifier = Modifier.width(150.dp),
                    singleLine = true,
                    label = { Text("Value") }
                )
            }
            DynoParameterType.ENUM -> {
                val currentOverride = DynoParameterRegistry.getFlowFieldOverride(
                    flowManipulation.className,
                    flowManipulation.fieldName,
                    field.name
                )
                val originalValue = (field.currentValue as? Int) ?: 0
                val currentValue = (currentOverride as? Int) ?: originalValue
                val enumMapping = field.enumMapping ?: emptyMap()
                
                var expanded by remember { mutableStateOf(false) }
                var selectedValue by remember(currentValue) { mutableStateOf(currentValue) }
                val selectedLabel = enumMapping[selectedValue] ?: "Unknown ($selectedValue)"
                
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.width(180.dp),
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text("▼", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        enumMapping.forEach { (value, label) ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "$label ($value)",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    selectedValue = value
                                    val success = DynoParameterRegistry.setFlowFieldOverride(
                                        flowManipulation.className,
                                        flowManipulation.fieldName,
                                        field.name,
                                        value
                                    )
                                    android.util.Log.d("DynoDebugActivity", "Enum field changed: ${field.name} = $value (${enumMapping[value]}), success = $success")
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            else -> {
                Text(
                    text = "Unsupported type",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}