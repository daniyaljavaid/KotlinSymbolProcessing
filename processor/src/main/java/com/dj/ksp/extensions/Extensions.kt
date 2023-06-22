package com.dj.ksp.extensions

import com.dj.ksp.processor.Processor
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import kotlin.reflect.KClass

fun StringBuilder.newLine(count: Int = 1) {
    repeat(count) {
        append("\n")
    }
}

fun SymbolProcessorEnvironment.createFileWithText(
    text: String
) {
    val file = this.codeGenerator.createNewFile(
        Dependencies(
            false
        ),
        Processor.GENERATED_PACKAGE,
        Processor.GENERATED_FILE_NAME
    )

    file.write(text.toByteArray())
}


fun Resolver.findAnnotations(
    kClass: KClass<*>,
) = getSymbolsWithAnnotation(
    kClass.qualifiedName.toString()
)
    .filterIsInstance<KSFunctionDeclaration>()

fun Resolver.findClassAnnotations(
    kClass: KClass<*>,
) = getSymbolsWithAnnotation(
    kClass.qualifiedName.toString()
)
    .filterIsInstance<KSClassDeclaration>()
