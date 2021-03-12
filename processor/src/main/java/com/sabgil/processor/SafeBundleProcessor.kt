package com.sabgil.processor

import com.google.auto.service.AutoService
import com.sabgil.annotation.SafeBundle
import com.sabgil.processor.analyzer.AnnotatedClassAnalyzer
import com.sabgil.processor.analyzer.MatchingChecker
import com.sabgil.processor.analyzer.TargetClassAnalyzer
import com.sabgil.processor.generator.CodeGenerator
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@KotlinPoetMetadataPreview
@AutoService(Processor::class)
class SafeBundleProcessor : AbstractProcessor() {

    override fun getSupportedAnnotationTypes(): Set<String> = hashSetOf(
        SafeBundle::class.java.canonicalName
    )

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        val annotatedElement = roundEnvironment.getElementsAnnotatedWith(SafeBundle::class.java)

        if (annotatedElement.isNotEmpty()) {
            val annotatedClassAnalyzer = AnnotatedClassAnalyzer(processingEnv)
            val targetClassAnalyzer = TargetClassAnalyzer(processingEnv)
            annotatedElement
                .filterIsInstance<TypeElement>()
                .forEach {
                    val annotatedClassAnalyzeResult = annotatedClassAnalyzer.analyze(it)
                    val targetClassAnalyzeResult = targetClassAnalyzer.analyze(it)
                    MatchingChecker(annotatedClassAnalyzeResult, targetClassAnalyzeResult)

                    val fileSpec = CodeGenerator(
                        annotatedClassAnalyzeResult, targetClassAnalyzeResult
                    ).generate()

                    fileSpec.writeTo(createKotlinGeneratedDir())
                }
        }

        return true
    }

    private fun createKotlinGeneratedDir() =
        File(processingEnv.options["kapt.kotlin.generated"], "")
}