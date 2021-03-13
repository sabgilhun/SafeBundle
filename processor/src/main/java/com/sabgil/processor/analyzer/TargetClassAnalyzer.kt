package com.sabgil.processor.analyzer

import com.sabgil.processor.common.ext.*
import com.sabgil.processor.common.model.*
import com.sabgil.processor.common.model.element.KotlinFunElement
import com.sabgil.processor.common.model.result.TargetClassAnalyzeResult
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
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
            TODO("TargetClassAnalyzer, error report")
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
                    TODO("TargetClassAnalyzer, error report")
                }
            }
            InheritanceType.FRAGMENT -> {
                val jvmMethods = kotlinFunElements.map { it.jvmMethod }
                if (!jvmMethods.all { env.isAssignable(it.returnType, fragmentClassName) }) {
                    TODO("TargetClassAnalyzer, error report")
                }
            }
        }
    }

    private fun checkForResult(
        kotlinFunElements: List<KotlinFunElement>
    ): Map<KotlinFunElement, ParameterSpec> {
        val map = mutableMapOf<KotlinFunElement, ParameterSpec>()
        val annotationType = env.parseToTypeElement(forResultAnnotationClassName).asType()

        kotlinFunElements.forEach { kotlinFunElement ->
            val isForResultAnnotated = kotlinFunElement.jvmMethod.annotationMirrors.any {
                it.annotationType == annotationType
            }

            if (isForResultAnnotated) {
                val requestCode = env.parseToTypeElement(requestCodeAnnotationClassName).asType()

                val jvmParam = kotlinFunElement.jvmMethod.parameters.first { param ->
                    param.annotationMirrors.any { it.annotationType == requestCode }
                }

                val kotlinParam = kotlinFunElement.kotlinFun.parameters.first {
                    it.name == jvmParam.name
                }

                if (kotlinParam.type == intClassName) {
                    map[kotlinFunElement] = kotlinParam
                } else {
                    TODO("TargetClassCheckStep, error report")
                }
            }
        }
        return map
    }
}