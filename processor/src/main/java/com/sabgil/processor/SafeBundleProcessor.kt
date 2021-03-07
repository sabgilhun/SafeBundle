package com.sabgil.processor

import com.google.auto.service.AutoService
import com.sabgil.annotation.Factory
import com.sabgil.annotation.Navigator
import com.sabgil.processor.analyzer.AnnotationAnalyzer
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class SafeBundleProcessor : AbstractProcessor() {

    override fun getSupportedAnnotationTypes(): Set<String> = hashSetOf(
        Navigator::class.java.canonicalName,
        Factory::class.java.canonicalName
    )

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        val navigatorElements = roundEnvironment.getElementsAnnotatedWith(Navigator::class.java)
        val factoryElements = roundEnvironment.getElementsAnnotatedWith(Factory::class.java)

        if (navigatorElements.isNotEmpty()) {
            navigatorElements.forEach { element ->
                AnnotationAnalyzer(element, processingEnv)
            }
        }

        return true
    }
}