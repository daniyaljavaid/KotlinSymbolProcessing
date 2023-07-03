package com.dj.ksp.processor

import com.dj.ksp.extensions.createFileWithText
import com.dj.ksp.extensions.findClassAnnotations
import com.dj.ksp.extensions.newLine
import com.dj.testannotation.RepositoryAnnotation
import com.dj.testannotation.ViewModelAnnotation
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
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

        val viewModels: Sequence<KSClassDeclaration> =
            resolver.findClassAnnotations(ViewModelAnnotation::class)

        val repositories: Sequence<KSClassDeclaration> =
            resolver.findClassAnnotations(RepositoryAnnotation::class)

        if (!viewModels.iterator().hasNext()) return emptyList()
        val viewModel = viewModels.iterator().next()
        val vmParams =
            viewModel.primaryConstructor?.parameters?.get(0)!!.type.resolve().declaration.annotations.iterator()
                .next().annotationType
        val declaration =
            viewModel.primaryConstructor?.parameters?.get(0)!!.type.resolve().declaration


//        if (declaration!!.packageName == classDec!!.packageName) {
//            throw IllegalStateException("Repository cannot be injected")
//        }

        val fileText = buildString {
            append("package $GENERATED_PACKAGE")
            newLine(2)
            append("fun printHackFunction() = \"\"\"")
            newLine()
            append(
                "all repositories " + getAnnotatedClasses(
                    resolver,
                    RepositoryAnnotation::class.java.canonicalName
                )
            )
            newLine()
            append(
                "all viewmodels " + getAnnotatedClasses(
                    resolver,
                    ViewModelAnnotation::class.java.canonicalName
                )
            )
            newLine()
            append("\"\"\"")
            newLine()
        }

        environment.logger.warn("PRINTED: \n\n$fileText")
        environment.createFileWithText(fileText)
        return emptyList()
    }

    private fun getAnnotatedClasses(
        resolver: Resolver,
        annotationName: String
    ): MutableList<KSAnnotated> {
        val annotatedClasses = mutableListOf<KSAnnotated>()

        val annotatedSymbols =
            resolver.getSymbolsWithAnnotation(annotationName)
        for (symbol in annotatedSymbols) {
            if (symbol is KSClassDeclaration) {
                annotatedClasses.add(symbol)
            }
        }

        return annotatedClasses
    }

}