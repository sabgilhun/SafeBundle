package com.sabgil.kapt

import com.google.auto.service.AutoService
import com.sabgil.annotation.Factory
import com.sabgil.annotation.Navigator
import com.sabgil.kapt.analyzer.AnnotationAnalyzer
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class SafeBundleProcessor : AbstractProcessor() {

    private var round = 0

    override fun getSupportedAnnotationTypes(): Set<String> = hashSetOf(
        Navigator::class.java.canonicalName,
        Factory::class.java.canonicalName
    )

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        val navigatorElements = roundEnvironment.getElementsAnnotatedWith(Navigator::class.java)
        val factoryElements = roundEnvironment.getElementsAnnotatedWith(Factory::class.java)

        if (navigatorElements.isEmpty() && factoryElements.isEmpty()) {
            return false
        }

        AnnotationAnalyzer(navigatorElements, factoryElements, processingEnv).analyze().let {
            if (it) {
                println("success")
            }
        }
        return true
    }
}