package com.sabgil.processor

import com.google.auto.service.AutoService
import com.sabgil.annotation.Factory
import com.sabgil.annotation.Navigator
import com.sabgil.processor.analyzer.step.*
import com.sabgil.processor.common.SequentialStep
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.toTypeSpec
import com.squareup.kotlinpoet.metadata.toKotlinClassMetadata
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import kotlinx.metadata.jvm.isRaw
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

@KotlinPoetMetadataPreview
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
            navigatorElements.forEach {
                val result = SequentialStep(it, processingEnv)
                    .chain(NavigatorOwnerCheckStep())
                    .chain(ArgumentsCheckStep())
                    .chain(NavigatorTargetCheckStep())
                    .chain(NavigatorTargetFunctionsCheckStep())
                    .chain(TargetFunctionArgumentsCheckStep())
                    .result()

                println(result)
            }
        }

        return true
    }

}