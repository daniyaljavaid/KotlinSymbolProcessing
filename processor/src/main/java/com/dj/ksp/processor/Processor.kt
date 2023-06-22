package com.dj.ksp.processor

import com.dj.testannotation.RepositoryAnnotation
import com.dj.testannotation.TestAnnotation
import com.dj.testannotation.ViewModelAnnotation
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.LazyDeclarationResolver
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyTypeParameterDescriptor
import kotlin.reflect.KClass

internal class Processor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {

    companion object {
        const val GENERATED_PACKAGE = "generated.file"
        const val GENERATED_FILE_NAME = "GeneratedFile"
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val hackedFunctions: Sequence<KSFunctionDeclaration> =
            resolver.findAnnotations(TestAnnotation::class)

        val viewModels: Sequence<KSClassDeclaration> =
            resolver.findClassAnnotations(ViewModelAnnotation::class)

        val repositories: Sequence<KSClassDeclaration> =
            resolver.findClassAnnotations(RepositoryAnnotation::class)
        val viewModel = viewModels.iterator().next()
        val vmParams =
            viewModel.primaryConstructor?.parameters?.get(0)!!.type.resolve().declaration.annotations.iterator()
                .next().annotationType
        val declaration =
            viewModel.primaryConstructor?.parameters?.get(0)!!.type.resolve().declaration

        val classDec = convertKtClassToKSDeclaration(RepositoryAnnotation::class)

        if (declaration!!.packageName == classDec!!.packageName) {
            throw IllegalStateException("Repository cannot be injected")
        }

        if (!hackedFunctions.iterator().hasNext()) return emptyList()

        val sourceFiles = hackedFunctions.mapNotNull { it.containingFile }
        val fileText = buildString {
            append("package $GENERATED_PACKAGE")
            newLine(2)

            append("fun printHackFunction() = \"\"\"")
            newLine()
            hackedFunctions
                .mapNotNull {
                    it.qualifiedName?.asString()
                }.forEach {
                    append(it)
                    newLine()
                }
            append("vm count " + viewModels.count())
            newLine()
            append("repositories count " + repositories.count())
            newLine()
            append("vmParams $vmParams")
            newLine()
            append("ktClass $ktClass")
            append("\"\"\"")
            newLine()
        }

        environment.logger.warn("PRINTED: \n\n$fileText")

        createFileWithText(sourceFiles, fileText)
        return (hackedFunctions).filterNot { it.validate() }.toList()
    }

    fun convertKtClassToKSDeclaration(ktClass: KtClass): KSClassDeclaration? {
        val resolver: Resolver = environment.resolver
        val declaration = resolver.getDeclaration(ktClass) ?: return null
        return declaration as? KSClassDeclaration
    }

    private fun Resolver.findAnnotations(
        kClass: KClass<*>,
    ) = getSymbolsWithAnnotation(
        kClass.qualifiedName.toString()
    )
        .filterIsInstance<KSFunctionDeclaration>()

    private fun Resolver.findClassAnnotations(
        kClass: KClass<*>,
    ) = getSymbolsWithAnnotation(
        kClass.qualifiedName.toString()
    )
        .filterIsInstance<KSClassDeclaration>()

    private fun createFileWithText(
        sourceFiles: Sequence<KSFile>,
        fileText: String,
    ) {
        val file = environment.codeGenerator.createNewFile(
            Dependencies(
                false,
                *sourceFiles.toList().toTypedArray(),
            ),
            GENERATED_PACKAGE,
            GENERATED_FILE_NAME
        )

        file.write(fileText.toByteArray())
    }

    private fun StringBuilder.newLine(count: Int = 1) {
        repeat(count) {
            append("\n")
        }
    }
}