package com.dj.ksp.processor

import com.dj.ksp.extensions.createFileWithText
import com.dj.ksp.extensions.newLine
import com.dj.testannotation.ViewModelAnnotation
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter

internal class Processor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {

    companion object {
        const val GENERATED_PACKAGE = "generated.file"
        const val GENERATED_FILE_NAME = "GeneratedFile"
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {

        //step1 - Find interfaces annotated with @CustomAnnotation
        val viewModelAnnotatedClasses = getAnnotatedClasses(
            resolver, ViewModelAnnotation::class.java.canonicalName
        )

        //step 2 - For each interface, find Implementation classes
        val files = resolver.getAllFiles().toList()

        val declarations = mutableListOf<KSClassDeclaration>()

        files.map {
            it.declarations
        }.map {
            it.toList().filterIsInstance<KSClassDeclaration>()
        }.forEach {
            declarations.addAll(it)
        }

        val implementationClasses = declarations.filter {
            it.superTypes.map {
                it.toString()
            }.toList().intersect(viewModelAnnotatedClasses.map {
                it.toString()
            }.toSet()).isNotEmpty()
        }

        // step 3 & 4 - For each Implementation class, access it’s constructor parameters
        //For each parameter/class, get it’s annotation.
        val classParams = implementationClasses.map {
            getConstructorParameters(it).map {
                val parameterClass = getClassFromParameter(it)
                parameterClass?.annotations?.map {
                    it.shortName.asString()
                }?.toList()
            }
        }


        val fileText = buildString {
            append("package $GENERATED_PACKAGE")
            newLine(2)
            append("fun printHackFunction() = \"\"\"")
            newLine()
            append(
                "all viewmodel classes/interfaces $viewModelAnnotatedClasses"
            )
            newLine()
            append(
                "viewmodel params $classParams"
            )
            newLine()
            append("\"\"\"")
            newLine()
        }

        environment.logger.warn("PRINTED: \n\n$fileText")
        try {
            environment.createFileWithText(fileText)
        } catch (e: Exception) {
            environment.logger.warn("Exception")
        }
        return emptyList()
    }

    private fun getClassFromParameter(parameter: KSValueParameter): KSClassDeclaration? {
        val parameterType = parameter.type.resolve()
        return parameterType.declaration as? KSClassDeclaration
    }

    private fun getAnnotatedClasses(
        resolver: Resolver, annotationName: String
    ): MutableList<KSClassDeclaration> {
        val annotatedClasses = mutableListOf<KSClassDeclaration>()

        val annotatedSymbols = resolver.getSymbolsWithAnnotation(annotationName)
        for (symbol in annotatedSymbols) {
            if (symbol is KSClassDeclaration) {
                annotatedClasses.add(symbol)
            }
        }

        return annotatedClasses
    }

    private fun getConstructorParameters(classDeclaration: KSClassDeclaration): List<KSValueParameter> {
        val constructor = classDeclaration.primaryConstructor ?: return emptyList()
        return constructor.parameters
    }

}