package com.sabgil.processor.analyzer

import com.sabgil.processor.common.ext.*
import com.sabgil.processor.common.model.*
import com.sabgil.processor.common.model.Function
import com.sabgil.processor.common.model.result.TargetClassAnalyzeResult
import com.squareup.kotlinpoet.ParameterSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind

class TargetClassAnalyzer(private val env: ProcessingEnvironment) {

    private val annotationType = env.parseToTypeElement(forResultAnnotationClassName).asType()

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

    private fun extractKotlinFunElements(
        targetClassElement: TypeElement
    ): List<Function> = targetClassElement.enclosedElements
        .filterIsInstance<ExecutableElement>()
        .filter { it.isAbstract && it.kind == ElementKind.METHOD }
        .map {
            Function(checkForResultAnnotation(it), it)
        }

    private fun checkFunctionDetails(
        targetClassElement: TypeElement,
        inheritanceType: InheritanceType,
        functions: List<Function>
    ) {
        when (inheritanceType) {
            InheritanceType.ACTIVITY -> {
                if (functions.any { it.returnType.kind != TypeKind.VOID }
                ) {
                    env.error(
                        "Return type of the target class functions must be a Unit when annotated class inherits Activity",
                        targetClassElement
                    )
                }
            }
            InheritanceType.FRAGMENT -> {
                if (functions.any { !env.isAssignable(it.returnType, fragmentClassName) }) {
                    env.error(
                        "Return type of the target class functions must be a Fragment when annotated class inherits Fragment",
                        targetClassElement
                    )
                }
            }
        }
    }

    private fun checkForResult(
        functions: List<Function>
    ): Map<Function, ParameterSpec> {
        val map = mutableMapOf<Function, ParameterSpec>()
        val annotationType = env.parseToTypeElement(forResultAnnotationClassName).asType()

        functions.forEach { function ->
            val isForResultAnnotated = function.annotations.any {
                it.annotationType == annotationType
            }

            if (isForResultAnnotated) {
                val requestCodeAnnotationType =
                    env.parseToTypeElement(requestCodeAnnotationClassName).asType()

                // TODO : param
//                val jvmParam = function.jvmMethod.parameters.firstOrNull { param ->
//                    param.annotationMirrors.any { it.annotationType == requestCodeAnnotationType }
//
//                } ?: env.error("Missing request code parameter", function.jvmMethod)
//
//                val kotlinParam = function.kotlinFun.parameters.firstOrNull {
//                    it.name == jvmParam.name
//                } ?: env.error("Missing request code parameter2", function.jvmMethod)
//
//                if (kotlinParam.type == intClassName) {
//                    map[function] = kotlinParam
//                } else {
//                    env.error("RequestCode parameter must be Int", jvmParam)
//                }
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

    private fun checkForResultAnnotation(
        executableElement: ExecutableElement
    ) = executableElement.annotationMirrors.any { it.annotationType == annotationType }
}