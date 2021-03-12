package com.sabgil.processor.analyzer

import com.sabgil.processor.common.ext.*
import com.sabgil.processor.common.model.TargetClassAnalyzeResult
import com.sabgil.processor.common.model.element.KotlinFunElement
import com.sabgil.processor.common.types.safeBundleAnnotationClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.toTypeSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

class TargetClassAnalyzer(
    private val env: ProcessingEnvironment
) {

    fun analyze(annotatedElement: TypeElement): TargetClassAnalyzeResult {
        val targetClassElement = findTargetClassElement(annotatedElement)
        checkTargetClassDetails(targetClassElement)

        val kotlinFunElements = extractKotlinFunElements(targetClassElement)
        checkFunctionDetails(targetClassElement, kotlinFunElements)

        return TargetClassAnalyzeResult(
            targetClassElement,
            kotlinFunElements
        )
    }

    private fun findTargetClassElement(annotatedElement: TypeElement): TypeElement {
        val annotationType = env.parseToTypeElement(safeBundleAnnotationClassName).asType()

        val annotationMirror = annotatedElement.annotationMirrors.first {
            it.annotationType == annotationType
        }

        return env.typeElement(annotationMirror.extractAnnotationValue())
    }

    private fun checkTargetClassDetails(targetClassElement: TypeElement) {
        if (!targetClassElement.isAbstract) {
            TODO("TargetClassCheckStep, error report")
        }
    }

    private fun extractKotlinFunElements(targetClassElement: TypeElement): List<KotlinFunElement> {
        val funSpecs = targetClassElement
            .toTypeSpec()
            .funSpecs
            .filter { it.modifiers.contains(KModifier.ABSTRACT) }

        val executableElements = targetClassElement.enclosedElements
            .filterIsInstance<ExecutableElement>()

        return funSpecs.map { funSpec ->
            val jvmMethod = requireNotNull(executableElements.find { it.name == funSpec.name })
            KotlinFunElement(funSpec, jvmMethod)
        }
    }

    private fun checkFunctionDetails(
        targetClassElement: TypeElement,
        kotlinFunElements: List<KotlinFunElement>
    ) {
        if (!kotlinFunElements.map { it.kotlinFun }.all { it.returnType != UNIT }) {
            TODO("TargetClassCheckStep, error report")
        }
    }
}