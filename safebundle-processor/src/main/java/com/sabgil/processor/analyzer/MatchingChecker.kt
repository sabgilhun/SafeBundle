package com.sabgil.processor.analyzer

import com.sabgil.processor.common.ext.error
import com.sabgil.processor.common.ext.isAssignable
import com.sabgil.processor.common.ext.name
import com.sabgil.processor.common.model.Parameter
import com.sabgil.processor.common.model.result.AnnotatedClassAnalyzeResult
import com.sabgil.processor.common.model.result.TargetClassAnalyzeResult
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class MatchingChecker(
    annotatedClassAnalyzeResult: AnnotatedClassAnalyzeResult,
    targetClassAnalyzeResult: TargetClassAnalyzeResult,
    private val env: ProcessingEnvironment
) {

    private val annotatedClass = annotatedClassAnalyzeResult.annotatedClass

    private val targetClassFunElements = targetClassAnalyzeResult.functions

    private val properties = annotatedClassAnalyzeResult.properties

    private val propertyNames = properties.keys

    fun check() {
        targetClassFunElements.forEach { func ->
            val usedPropertyNameSet = mutableSetOf<String>()

            val parameters = if (func.isForResult) {
                val filtered = func.parameters.filter { !it.isRequestCodeParam }
                if (filtered.size == func.parameters.size) {
                    reportMissingParamNameError(func.element)
                }
                filtered
            } else {
                func.parameters
            }

            parameters.forEach { parameter ->
                if (!propertyNames.contains(parameter.variableElement.name)) {
                    reportNotIncludeNameError(parameter.variableElement)
                }
                checkNameIncludeInProperties(parameter)
                checkMatchingType(parameter)
                checkNullability(parameter)
                usedPropertyNameSet.add(parameter.name)
            }
            checkRemainProperty(usedPropertyNameSet)
        }
    }

    private fun checkNameIncludeInProperties(parameter: Parameter) {
        if (!propertyNames.contains(parameter.variableElement.name)) {
            reportNotIncludeNameError(parameter.variableElement)
        }
    }


    private fun checkMatchingType(parameter: Parameter) {
        val property = requireNotNull(properties[parameter.name])
        if (!env.isAssignable(property.type, parameter.type)) {
            reportNotMatchingTypeError(parameter.variableElement)
        }
    }

    private fun checkNullability(parameter: Parameter) {
        val property = requireNotNull(properties[parameter.name])
        if (parameter.isNullable && !property.isNullable) {
            reportNotMatchingNullabilityError(parameter.variableElement)
        }
    }

    private fun checkRemainProperty(usedPropertyNameSet: Set<String>) {
        val numOfRequiredProp = properties.values.count { !it.isNullable }
        if (usedPropertyNameSet.size < numOfRequiredProp) {
            reportNotMatchingPropertiesError(annotatedClass.element)
        }
    }

    private fun reportMissingParamNameError(element: Element) {
        env.error("Require 'requestCode' parameter if use @ForResult", element)
    }

    private fun reportNotIncludeNameError(element: Element) {
        env.error(
            "Property names of SafeBundle annotated class must equals to function parameter names of target class",
            element
        )
    }

    private fun reportNotMatchingTypeError(element: Element) {
        env.error(
            "Property types of SafeBundle annotated class equals to function parameter types of target class",
            element
        )
    }

    private fun reportNotMatchingNullabilityError(element: Element) {
        env.error("Check nullability of properties and parameter", element)
    }

    private fun reportNotMatchingPropertiesError(element: Element) {
        env.error("Check number of property and parameter", element)
    }
}