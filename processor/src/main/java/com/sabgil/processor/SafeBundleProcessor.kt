package com.sabgil.processor

import com.google.auto.service.AutoService
import com.sabgil.annotation.Factory
import com.sabgil.annotation.Navigator
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
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

//        if (navigatorElements.isNotEmpty()) {
//            val e = navigatorElements.first()!!
//            val type = processingEnv
//                .elementUtils.getTypeElement(Navigator::class.java.canonicalName).asType()
//            val annotation = e.annotationMirrors.first { it.annotationType == type }
//            val key = annotation.elementValues.keys.first { it.simpleName.contentEquals("clazz") }
//            val value = annotation.elementValues[key]!!
//            val classValue = value.value.toString()
//            val eElement = processingEnv
//                .elementUtils.getTypeElement(classValue)
//
//            println(eElement.modifiers.contains(Modifier.ABSTRACT))
//        }

        return true
    }
}