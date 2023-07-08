package com.dj.ksp.extensions

import com.dj.ksp.processor.Processor
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter

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

fun KSClassDeclaration.getConstructorParameters(): List<KSValueParameter> {
    val constructor = primaryConstructor ?: return emptyList()
    return constructor.parameters
}

fun KSValueParameter.getClassFromParameter(): KSClassDeclaration? {
    val parameterType = type.resolve()
    return parameterType.declaration as? KSClassDeclaration
}

fun Resolver.getAnnotatedClasses(
    annotationName: String
): MutableList<KSClassDeclaration> {
    val annotatedClasses = mutableListOf<KSClassDeclaration>()

    val annotatedSymbols = getSymbolsWithAnnotation(annotationName)
    for (symbol in annotatedSymbols) {
        if (symbol is KSClassDeclaration) {
            annotatedClasses.add(symbol)
        }
    }

    return annotatedClasses
}