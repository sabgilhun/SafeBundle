package com.sabgil.processor.analyzer

import com.sabgil.processor.common.ext.*
import com.sabgil.processor.common.model.*
import com.sabgil.processor.common.model.Function
import com.sabgil.processor.common.model.result.TargetClassAnalyzeResult
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeKind

class TargetClassAnalyzer(private val env: ProcessingEnvironment) {

    private val forResultAnnotationType =
        env.parseToTypeElement(forResultAnnotationClassName).asType()

    private val requestCodeAnnotationType =
        env.parseToTypeElement(requestCodeAnnotationClassName).asType()

    private val nullableAnnotationType =
        env.parseToTypeElement(nullableAnnotationClassName).asType()

    fun analyze(
        annotatedElement: TypeElement,
        annotatedClassInheritanceType: InheritanceType
    ): TargetClassAnalyzeResult {
        val targetClassElement = findTargetClassElement(annotatedElement)
        checkTargetClassDetails(targetClassElement)

        val functions = extractKotlinFunElements(targetClassElement)
        checkFunctionDetails(targetClassElement, annotatedClassInheritanceType, functions)
        checkInterface(targetClassElement, annotatedClassInheritanceType, functions)

        return TargetClassAnalyzeResult(
            targetClassElement,
            functions
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
        .map { func ->
            Function(
                isForResult = checkForResultAnnotation(func),
                jvmMethod = func,
                parameters = func.parameters.map { it.toParameter() }
            )
        }

    private fun checkFunctionDetails(
        targetClassElement: TypeElement,
        inheritanceType: InheritanceType,
        functions: List<Function>
    ) {
        when (inheritanceType) {
            InheritanceType.ACTIVITY -> {
                if (functions.any { it.returnType.kind != TypeKind.VOID }) {
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

    private fun checkInterface(
        targetClassElement: TypeElement,
        annotatedClassInheritanceType: InheritanceType,
        functions: List<Function>
    ) {
        val targetType = targetClassElement.asType()
        val isIncludeForResult = functions.any { it.isForResult }
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

    private fun VariableElement.toParameter(): Parameter {
        return Parameter(
            checkRequestCodeParam(this),
            checkNullability(this),
            this
        )
    }

    private fun checkForResultAnnotation(
        executableElement: ExecutableElement
    ) = executableElement.annotationMirrors.any { it.annotationType == forResultAnnotationType }

    private fun checkNullability(
        variableElement: VariableElement
    ) = variableElement.annotationMirrors.any { it.annotationType == nullableAnnotationType }

    private fun checkRequestCodeParam(variableElement: VariableElement): Boolean {
        val isAnnotated = variableElement.annotationMirrors.any {
            it.annotationType == requestCodeAnnotationType
        }

        if (isAnnotated && variableElement.asType().kind != TypeKind.INT) {
            env.error("RequestCode parameter must be Int", variableElement)
        }

        return isAnnotated
    }
}