package com.ardayucesan.dyno.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.ardayucesan.dyno.annotations.*

class DynoSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("DynoSymbolProcessor: Starting processing")

        val exposedSymbols = resolver.getSymbolsWithAnnotation("com.ardayucesan.dyno.annotations.DynoExpose")
        val triggerSymbols = resolver.getSymbolsWithAnnotation("com.ardayucesan.dyno.annotations.DynoTrigger")
        val groupSymbols = resolver.getSymbolsWithAnnotation("com.ardayucesan.dyno.annotations.DynoGroup")
        val functionSymbols = resolver.getSymbolsWithAnnotation("com.ardayucesan.dyno.annotations.DynoFunction")

        val allSymbols = (exposedSymbols + triggerSymbols + groupSymbols + functionSymbols).toList()
        
        if (allSymbols.isEmpty()) {
            logger.info("DynoSymbolProcessor: No Dyno annotations found")
            return emptyList()
        }

        val invalidSymbols = allSymbols.filter { !it.validate() }
        if (invalidSymbols.isNotEmpty()) {
            logger.info("DynoSymbolProcessor: Found invalid symbols, deferring processing")
            return invalidSymbols
        }

        try {
            generateDynoRegistry(exposedSymbols, triggerSymbols, groupSymbols, functionSymbols)
            logger.info("DynoSymbolProcessor: Registry generated successfully")
        } catch (e: Exception) {
            logger.error("DynoSymbolProcessor: Error generating registry: ${e.message}")
        }

        return emptyList()
    }

    private fun generateDynoRegistry(
        exposedSymbols: Sequence<KSAnnotated>,
        triggerSymbols: Sequence<KSAnnotated>,
        groupSymbols: Sequence<KSAnnotated>,
        functionSymbols: Sequence<KSAnnotated>
    ) {
        val file = codeGenerator.createNewFile(
            dependencies = Dependencies(false),
            packageName = "com.ardayucesan.dyno.generated",
            fileName = "DynoRegistry"
        )

        file.bufferedWriter().use { writer ->
            writer.write(generateRegistryCode(exposedSymbols, triggerSymbols, groupSymbols, functionSymbols))
        }
    }

    private fun generateRegistryCode(
        exposedSymbols: Sequence<KSAnnotated>,
        triggerSymbols: Sequence<KSAnnotated>,
        groupSymbols: Sequence<KSAnnotated>,
        functionSymbols: Sequence<KSAnnotated>
    ): String {
        val stringBuilder = StringBuilder()

        stringBuilder.appendLine("package com.ardayucesan.dyno.generated")
        stringBuilder.appendLine()
        stringBuilder.appendLine("import com.ardayucesan.dyno.annotations.*")
        stringBuilder.appendLine("import com.ardayucesan.dyno.runtime.DynoParameterRegistry")
        stringBuilder.appendLine("import kotlin.reflect.KClass")
        stringBuilder.appendLine()
        stringBuilder.appendLine("object DynoRegistry {")
        stringBuilder.appendLine()
        stringBuilder.appendLine("    fun registerAll() {")

        // Process groups and their members
        val groupMap = mutableMapOf<String, MutableList<String>>()
        
        groupSymbols.forEach { symbol ->
            if (symbol is KSClassDeclaration) {
                val annotation = symbol.annotations.find { 
                    it.shortName.asString() == "DynoGroup" 
                }?.also { groupAnnotation ->
                    val className = symbol.qualifiedName?.asString() ?: return@forEach
                    val groupName = groupAnnotation.arguments.find { it.name?.asString() == "name" }?.value as? String ?: ""
                    val description = groupAnnotation.arguments.find { it.name?.asString() == "description" }?.value as? String ?: ""
                    val enabled = groupAnnotation.arguments.find { it.name?.asString() == "enabled" }?.value as? Boolean ?: true
                    
                    stringBuilder.appendLine("        // Register group: $className")
                    stringBuilder.appendLine("        DynoParameterRegistry.registerGroup(")
                    stringBuilder.appendLine("            className = \"$className\",")
                    stringBuilder.appendLine("            groupName = \"${groupName.ifEmpty { symbol.simpleName.asString() }}\",")
                    stringBuilder.appendLine("            description = \"$description\",")
                    stringBuilder.appendLine("            enabled = $enabled")
                    stringBuilder.appendLine("        )")
                    stringBuilder.appendLine()
                }
            }
        }

        // Process exposed parameters
        exposedSymbols.forEach { symbol ->
            when (symbol) {
                is KSPropertyDeclaration -> {
                    val annotation = symbol.annotations.find { 
                        it.shortName.asString() == "DynoExpose" 
                    }?.also { exposeAnnotation ->
                        val className = symbol.parentDeclaration?.qualifiedName?.asString() ?: return@forEach
                        val fieldName = symbol.simpleName.asString()

                        val name = exposeAnnotation.arguments.find { it.name?.asString() == "name" }?.value as? String ?: ""
                        val group = exposeAnnotation.arguments.find { it.name?.asString() == "group" }?.value as? String ?: "Default"
                        val description = exposeAnnotation.arguments.find { it.name?.asString() == "description" }?.value as? String ?: ""
                        
                        stringBuilder.appendLine("        // Register exposed parameter: $className.$fieldName")
                        stringBuilder.appendLine("        DynoParameterRegistry.registerParameter(")
                        stringBuilder.appendLine("            className = \"$className\",")
                        stringBuilder.appendLine("            fieldName = \"$fieldName\",")
                        stringBuilder.appendLine("            displayName = \"${name.ifEmpty { fieldName }}\",")
                        stringBuilder.appendLine("            group = \"$group\",")
                        stringBuilder.appendLine("            description = \"$description\"")
                        stringBuilder.appendLine("        )")
                        stringBuilder.appendLine()
                    }
                }
            }
        }

        // Process trigger methods
        triggerSymbols.forEach { symbol ->
            when (symbol) {
                is KSFunctionDeclaration -> {
                    val annotation = symbol.annotations.find { 
                        it.shortName.asString() == "DynoTrigger" 
                    }?.also { triggerAnnotation ->
                        val className = symbol.parentDeclaration?.qualifiedName?.asString() ?: return@forEach
                        val methodName = symbol.simpleName.asString()
                        val name = triggerAnnotation.arguments.find { it.name?.asString() == "name" }?.value as? String ?: ""
                        val group = triggerAnnotation.arguments.find { it.name?.asString() == "group" }?.value as? String ?: "Default"
                        val description = triggerAnnotation.arguments.find { it.name?.asString() == "description" }?.value as? String ?: ""
                        
                        stringBuilder.appendLine("        // Register trigger method: $className.$methodName")
                        stringBuilder.appendLine("        DynoParameterRegistry.registerTrigger(")
                        stringBuilder.appendLine("            className = \"$className\",")
                        stringBuilder.appendLine("            methodName = \"$methodName\",")
                        stringBuilder.appendLine("            displayName = \"${name.ifEmpty { methodName }}\",")
                        stringBuilder.appendLine("            group = \"$group\",")
                        stringBuilder.appendLine("            description = \"$description\"")
                        stringBuilder.appendLine("        )")
                        stringBuilder.appendLine()
                    }
                }
            }
        }

        // Process debug functions
        functionSymbols.forEach { symbol ->
            when (symbol) {
                is KSFunctionDeclaration -> {
                    val annotation = symbol.annotations.find { 
                        it.shortName.asString() == "DynoFunction" 
                    }?.also { functionAnnotation ->
                        val className = symbol.parentDeclaration?.qualifiedName?.asString() ?: return@forEach
                        val methodName = symbol.simpleName.asString()
                        val name = functionAnnotation.arguments.find { it.name?.asString() == "name" }?.value as? String ?: ""
                        val group = functionAnnotation.arguments.find { it.name?.asString() == "group" }?.value as? String ?: "Default"
                        val description = functionAnnotation.arguments.find { it.name?.asString() == "description" }?.value as? String ?: ""
                        val exposeParameters = functionAnnotation.arguments.find { it.name?.asString() == "exposeParameters" }?.value as? Boolean ?: true
                        
                        stringBuilder.appendLine("        // Register debug function: $className.$methodName")
                        stringBuilder.appendLine("        DynoParameterRegistry.registerDebugFunction(")
                        stringBuilder.appendLine("            className = \"$className\",")
                        stringBuilder.appendLine("            methodName = \"$methodName\",")
                        stringBuilder.appendLine("            displayName = \"${name.ifEmpty { methodName }}\",")
                        stringBuilder.appendLine("            group = \"$group\",")
                        stringBuilder.appendLine("            description = \"$description\",")
                        stringBuilder.appendLine("            exposeParameters = $exposeParameters")
                        stringBuilder.appendLine("        )")
                        stringBuilder.appendLine()
                    }
                }
            }
        }

        stringBuilder.appendLine("    }")
        stringBuilder.appendLine("}")

        return stringBuilder.toString()
    }
}

class DynoSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return DynoSymbolProcessor(environment.codeGenerator, environment.logger)
    }
}