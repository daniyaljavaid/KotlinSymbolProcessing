package com.dj.ksp.processor

import com.dj.ksp.extensions.createFileWithText
import com.dj.ksp.extensions.getAnnotatedClasses
import com.dj.ksp.extensions.getClassFromParameter
import com.dj.ksp.extensions.getConstructorParameters
import com.dj.ksp.extensions.newLine
import com.dj.testannotation.RepositoryAnnotation
import com.dj.testannotation.UseCaseAnnotation
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
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
        val annotatedClasses = resolver.getAnnotatedClasses(
            UseCaseAnnotation::class.java.canonicalName
        )

        if (annotatedClasses.any { it.classKind == ClassKind.INTERFACE }) {
            validate(annotatedClasses)
        } else {

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
                }.toList().intersect(annotatedClasses.map {
                    it.toString()
                }.toSet()).isNotEmpty()
            }

            validate(implementationClasses)
        }

        val fileText = buildString {
            append("package $GENERATED_PACKAGE")
            newLine(2)
            append("fun printHackFunction() = \"\"\"")
            newLine()
            append(
                "all classes/interfaces $annotatedClasses"
            )
            newLine()
//            append(
////                "all viewmodel implementations $implementationClasses"
//            )
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


    private fun validate(implementationClasses: List<KSClassDeclaration>) {
        // step 3 - For each Implementation class, access it’s constructor parameters
        implementationClasses.forEach { implClass ->
            implClass.getConstructorParameters().forEach {

                // step 4 - For each parameter/class, get it’s annotation
                val parameterClass = it.getClassFromParameter()

                // step 5 - Validate according to respective layer
                parameterClass?.annotations?.forEach {
                    if (it.shortName.asString() != RepositoryAnnotation::class.simpleName) {
                        throw java.lang.Exception("Verify $implClass parameters")
                    }
                }
            }
        }
    }

}