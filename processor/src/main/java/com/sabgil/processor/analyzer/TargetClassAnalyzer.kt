package com.sabgil.processor.analyzer

import com.sabgil.processor.common.ext.*
import com.sabgil.processor.common.model.*
import com.sabgil.processor.common.model.element.KotlinFunElement
import com.sabgil.processor.common.model.result.TargetClassAnalyzeResult
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.metadata.specs.toTypeSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
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
        checkFunctionDetails(targetClassElement, annotatedClassInheritanceType, kotlinFunElements)

        val useForResultMap = checkForResult(kotlinFunElements)
        val isIncludeForResult = useForResultMap.isNotEmpty()
        checkInterface(targetClassElement, annotatedClassInheritanceType, isIncludeForResult)

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
            env.error(
                "Target class of SafeBundle must be abstract class",
                targetClassElement
            )
        }
    }

    private fun extractKotlinFunElements(targetClassElement: TypeElement): List<KotlinFunElement> {
        val kotlinFunctions = targetClassElement
            .toTypeSpec()
            .funSpecs
            .filter { it.modifiers.contains(KModifier.ABSTRACT) }

        val jvmMethods = targetClassElement.enclosedElements
            .filterIsInstance<ExecutableElement>()
            .filter { it.isAbstract && it.kind == ElementKind.METHOD }

        return kotlinFunctions.mapIndexed { i, f -> KotlinFunElement(f, jvmMethods[i]) }
    }

    private fun checkFunctionDetails(
        targetClassElement: TypeElement,
        annotatedClassInheritanceType: InheritanceType,
        kotlinFunElements: List<KotlinFunElement>
    ) {
        when (annotatedClassInheritanceType) {
            InheritanceType.ACTIVITY -> {
                val kotlinFunctions = kotlinFunElements.map { it.kotlinFun }
                if (!kotlinFunctions.all { it.returnType == UNIT || it.returnType == null }
                ) {
                    env.error(
                        "Return type of the target class functions must be a Unit when annotated class inherits Activity",
                        targetClassElement
                    )
                }
            }
            InheritanceType.FRAGMENT -> {
                val k = kotlinFunElements.map { it.kotlinFun }
                k.any { !env.isAssignable(ClassName.bestGuess(it.returnType.toString()), fragmentClassName)}
                val jvmMethods = kotlinFunElements.map { it.jvmMethod }
                if (jvmMethods.any { !env.isAssignable(it.returnType, fragmentClassName) }) {
                    env.error(
                        "Return type of the target class functions must be a Fragment when annotated class inherits Fragment",
                        targetClassElement
                    )
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

                val jvmParam = kotlinFunElement.jvmMethod.parameters.firstOrNull { param ->
                    param.annotationMirrors.any { it.annotationType == requestCode }
                } ?: env.error("Missing request code parameter", kotlinFunElement.jvmMethod)

                val kotlinParam = kotlinFunElement.kotlinFun.parameters.firstOrNull {
                    it.name == jvmParam.name
                } ?: env.error("Missing request code parameter2", kotlinFunElement.jvmMethod)

                if (kotlinParam.type == intClassName) {
                    map[kotlinFunElement] = kotlinParam
                } else {
                    env.error("RequestCode parameter must be Int", jvmParam)
                }
            }
        }
        return map
    }

    private fun checkInterface(
        targetClassElement: TypeElement,
        annotatedClassInheritanceType: InheritanceType,
        isIncludeForResult: Boolean
    ) {
        val targetType = targetClassElement.asType()
        if (annotatedClassInheritanceType == InheritanceType.ACTIVITY) {
            if (isIncludeForResult) {
                if (!env.isAssignable(targetType, activityBasedCreatableClassName)) {
                    env.error(
                        "Target class must implements ActivityBasedCreatable if annotated class inherits Activity and use @ForResult",
                        targetClassElement
                    )
                }
            } else {
                if (!env.isAssignable(targetType, contextBasedCreatableClassName)) {
                    env.error(
                        "Target class must implements ContextBasedCreatable if annotated class inherits Activity",
                        targetClassElement
                    )
                }
            }
        } else {
            if (!env.isAssignable(targetType, creatableClassName)) {
                env.error(
                    "Target class must implements Creatable",
                    targetClassElement
                )
            }
        }
    }
}