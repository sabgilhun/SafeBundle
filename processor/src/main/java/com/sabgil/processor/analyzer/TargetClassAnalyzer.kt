package com.sabgil.processor.analyzer

import com.sabgil.processor.common.ext.*
import com.sabgil.processor.common.model.*
import com.sabgil.processor.common.model.element.KotlinFunElement
import com.sabgil.processor.common.model.result.TargetClassAnalyzeResult
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.metadata.specs.toTypeSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

class TargetClassAnalyzer(private val env: ProcessingEnvironment) {

    fun analyze(
        annotatedElement: TypeElement,
        annotatedClassInheritanceType: InheritanceType
    ): TargetClassAnalyzeResult {
        val targetClassElement = findTargetClassElement(annotatedElement)
        checkTargetClassDetails(targetClassElement)

        val kotlinFunElements = extractKotlinFunElements(targetClassElement)
        checkFunctionDetails(annotatedClassInheritanceType, kotlinFunElements)

        val useForResultMap = checkForResult(kotlinFunElements)
        val isIncludeForResult = useForResultMap.isNotEmpty()

        return TargetClassAnalyzeResult(
            targetClassElement,
            kotlinFunElements,
            useForResultMap,
            isIncludeForResult
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
        val kotlinFunctions = targetClassElement
            .toTypeSpec()
            .funSpecs
            .filter { it.modifiers.contains(KModifier.ABSTRACT) }

        val jvmMethods = targetClassElement.enclosedElements
            .filterIsInstance<ExecutableElement>()

        return kotlinFunctions.map { funSpec ->
            val jvmMethod = requireNotNull(jvmMethods.find { it.name == funSpec.name })
            KotlinFunElement(funSpec, jvmMethod)
        }
    }

    private fun checkFunctionDetails(
        annotatedClassInheritanceType: InheritanceType,
        kotlinFunElements: List<KotlinFunElement>
    ) {
        when (annotatedClassInheritanceType) {
            InheritanceType.ACTIVITY -> {
                if (!kotlinFunElements.map { it.kotlinFun }.all { it.returnType != UNIT }) {
                    TODO("TargetClassCheckStep, error report")
                }
            }
            InheritanceType.FRAGMENT -> {
                val jvmMethods = kotlinFunElements.map { it.jvmMethod }
                if (!jvmMethods.all { env.isAssignable(it.returnType, fragmentClassName) }) {
                    TODO("TargetClassCheckStep, error report")
                }
            }
        }
    }

    private fun checkForResult(kotlinFunElements: List<KotlinFunElement>): Map<KotlinFunElement, Boolean> {
        val map = mutableMapOf<KotlinFunElement, Boolean>()
        val annotationType = env.parseToTypeElement(forResultAnnotationClassName).asType()
        kotlinFunElements.forEach { kotlinFunElement ->
            val isForResultAnnotated = kotlinFunElement.jvmMethod.annotationMirrors.any {
                it.annotationType == annotationType
            }

            if (isForResultAnnotated) {
                val firstParam = kotlinFunElement.kotlinFun.parameters.first()
                if (firstParam.name == "requestCode" && firstParam.type == intClassName)
                    map[kotlinFunElement] = isForResultAnnotated
            }
        }
        return map
    }
}