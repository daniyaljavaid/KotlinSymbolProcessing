package com.dj.ksp.processor

import com.dj.ksp.extensions.createFileWithText
import com.dj.ksp.extensions.getAnnotatedClasses
import com.dj.ksp.extensions.getClassFromParameter
import com.dj.ksp.extensions.getAnnotatedClassVariables
import com.dj.ksp.extensions.getConstructorParameters
import com.dj.ksp.extensions.newLine
import com.dj.ksp.properties.AnnotationProperties
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

internal class Processor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {

    companion object {
        const val GENERATED_PACKAGE = "generated.file"
        const val GENERATED_FILE_NAME = "GeneratedFile"
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        AnnotationProperties.values().forEach { annotation ->
            findAndValidateAnnotations(
                resolver, annotation = annotation.annotationName, annotation.inclusions
            )
        }
        return emptyList()
    }

    private fun findAndValidateAnnotations(
        resolver: Resolver, annotation: String, inclusions: List<String>
    ) {

        //step1 - Find interfaces annotated with @CustomAnnotation
        val annotatedClasses = resolver.getAnnotatedClasses(
            annotation
        )

        // check if it was a class or interface
        if (annotatedClasses.any { it.classKind != ClassKind.INTERFACE }) {
            // for Class(Fragment/Activity/VM)

            validate(annotatedClasses, inclusions)

            generateFile(buildString {
                newLine()
                append(
                    "all classes $annotatedClasses"
                )
                newLine()
            })
        } else {
            // for Interface
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

            validate(implementationClasses, inclusions)

            generateFile(buildString {
                newLine()
                append(
                    "all interfaces $annotatedClasses"
                )
                newLine()
                append(
                    "all implementations $implementationClasses"
                )
                newLine()
            })
        }
    }

    private fun generateFile(
        body: String
    ) {
        val fileText = buildString {
            append("package $GENERATED_PACKAGE")
            newLine(2)
            append("fun printHackFunction() = \"\"\"")
            append(body)
            append("\"\"\"")
            newLine()
        }

        try {
            environment.createFileWithText(fileText)
        } catch (e: Exception) {
            environment.logger.warn("Exception")
        }
    }

    private fun validate(
        implementationClasses: List<KSClassDeclaration>, inclusions: List<String>
    ) {
        // step 3 - For each Implementation class, access it’s constructor parameters
        implementationClasses.forEach { implClass ->
            // get & validate each constructor parameter
            implClass.getConstructorParameters().forEach {

                // step 4 - For each class, get it’s annotation
                val parameterClass = it.getClassFromParameter()

                // step 5 - Validate according to respective layer
                val annotationsList =
                    parameterClass?.annotations?.toMutableList() ?: mutableListOf()

                annotationsList.addAll(it.annotations)

                val annotations = annotationsList.map { it.shortName.asString() }

                if (annotations.isEmpty() || annotations.intersect(inclusions).isEmpty()) {
                    throw java.lang.Exception(
                        "$implClass params are annotated with $annotations but should have annotated with $inclusions"
                    )
                }
            }

            // get & validate each class variable which is @Inject annotated
            implClass.getAnnotatedClassVariables().filter {
                it.annotations.toList().isNotEmpty()
                        && (!it.annotations.toList()
                    .map { it.shortName.toString() }.toList().contains("Inject"))
            }.map {
                it.type.resolve().declaration as KSClassDeclaration
            }.forEach {

                // step 4 - For each parameter/class, get it’s annotation
                // step 5 - Validate according to respective layer
                val annotations =
                    it.annotations.toList().map { it.shortName.asString() }

                if (annotations.isEmpty() || annotations.intersect(inclusions).isEmpty()) {
                    throw java.lang.Exception(
                        "$implClass params are annotated with $annotations but should have annotated with $inclusions"
                    )
                }
            }
        }
    }

}