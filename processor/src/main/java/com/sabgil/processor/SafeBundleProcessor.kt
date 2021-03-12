package com.sabgil.processor

import com.google.auto.service.AutoService
import com.sabgil.annotation.SafeBundle
import com.sabgil.processor.analyzer.AnnotatedClassAnalyzer
import com.sabgil.processor.analyzer.MatchingChecker
import com.sabgil.processor.analyzer.TargetClassAnalyzer
import com.sabgil.processor.generator.CodeGenerator
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

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

            val results = annotatedElement
                .filterIsInstance<TypeElement>()
                .map {
                    val annotatedClassResult =
                        annotatedClassAnalyzer.analyze(it)
                    val targetClassResult =
                        targetClassAnalyzer.analyze(it, annotatedClassResult.inheritanceType)

                    MatchingChecker(annotatedClassResult, targetClassResult).check()
                    annotatedClassResult to targetClassResult
                }


            results.forEach { (annotatedClassAnalyzeResult, targetClassAnalyzeResult) ->
                val fileSpec = CodeGenerator(
                    annotatedClassAnalyzeResult,
                    targetClassAnalyzeResult
                ).generate()

                fileSpec.writeTo(createKotlinGeneratedDir())
            }
        }

        return true
    }

    private fun createKotlinGeneratedDir() =
        File(processingEnv.options["kapt.kotlin.generated"], "")
}