package com.sabgil.processor.analyzer

import com.sabgil.processor.common.ext.error
import com.sabgil.processor.common.ext.isAssignable
import com.sabgil.processor.common.ext.name
import com.sabgil.processor.common.model.Parameter
import com.sabgil.processor.common.model.result.AnnotatedClassAnalyzeResult
import com.sabgil.processor.common.model.result.TargetClassAnalyzeResult
import com.squareup.kotlinpoet.ParameterSpec
import javax.annotation.processing.ProcessingEnvironment

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
                if(filtered.size == func.parameters.size) {
                    env.error("Require 'requestCode' parameter if use @ForResult", func.jvmMethod)
                }
                filtered
            } else {
                func.parameters
            }

            parameters.forEach { parameter ->
                checkNameIncludeInProperties(parameter) //
                checkMatchingType(parameter)
                checkNullability(parameter)
                usedPropertyNameSet.add(parameter.name)
            }
            checkRemainProperty(usedPropertyNameSet)
        }
    }

    private fun checkNameIncludeInProperties(parameter: Parameter) {
        if (!propertyNames.contains(parameter.variableElement.name)) {
            env.error(
                "Property names of SafeBundle annotated class must equals to function parameter names of target class",
                annotatedClass.element
            )
        }
    }

    private fun checkMatchingType(parameter: Parameter) {
        val property = requireNotNull(properties[parameter.name])
        if (!env.isAssignable(property.type, parameter.type)) {
            env.error(
                "Property types of SafeBundle annotated class equals to function parameter types of target class",
                parameter.variableElement
            )
        }
    }

    private fun checkNullability(parameter: Parameter) {
        val property = requireNotNull(properties[parameter.name])
        if (parameter.isNullable && !property.isNullable) {
            env.error(
                "Check nullability of properties and parameter",
                annotatedClass.element
            )
        }
    }

    private fun checkRemainProperty(usedPropertyNameSet: Set<String>) {
        val numOfRequiredProp = properties.values.count { !it.isNullable }
        if (usedPropertyNameSet.size < numOfRequiredProp) {
            env.error(
                "Check number of property and parameter",
                annotatedClass.element
            )
        }
    }
}