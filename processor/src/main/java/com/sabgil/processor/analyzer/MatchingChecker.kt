package com.sabgil.processor.analyzer

import com.sabgil.processor.common.ext.error
import com.sabgil.processor.common.model.result.AnnotatedClassAnalyzeResult
import com.sabgil.processor.common.model.result.TargetClassAnalyzeResult
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import javax.annotation.processing.ProcessingEnvironment

class MatchingChecker(
    annotatedClassAnalyzeResult: AnnotatedClassAnalyzeResult,
    targetClassAnalyzeResult: TargetClassAnalyzeResult,
    private val env: ProcessingEnvironment
) {

    private val annotatedClassElement = annotatedClassAnalyzeResult.annotatedClassElement

    private val targetClassElement = targetClassAnalyzeResult.targetClassElement

    private val targetClassFunElements = targetClassAnalyzeResult.targetClassFunElements

    private val propertiesMap = annotatedClassAnalyzeResult.propertiesMap

    private val propertyNames = propertiesMap.keys

    private val requestCodeMap = targetClassAnalyzeResult.requestCodeMap

    private val isIncludeForResult = targetClassAnalyzeResult.isIncludeForResult

    private val numOfRequiredProperties = propertiesMap.values.filter {
        !it.kotlinProperty.type.isNullable
    }.size


    fun check() {
        targetClassFunElements.forEach { func ->
            val usedPropertyNameSet = mutableSetOf<String>()

            val params = if (isIncludeForResult) {
                val requestCodeParam = requestCodeMap[func]
                func.kotlinFun.parameters.filter { it != requestCodeParam }
            } else {
                func.kotlinFun.parameters
            }

            params.forEach { kotlinParam ->
                checkNameIncludeInProperties(kotlinParam)
                checkMatchingType(kotlinParam)
                checkNullability(kotlinParam)
                usedPropertyNameSet.add(kotlinParam.name)
            }
            checkRemainProperty(usedPropertyNameSet)
        }
    }

    private fun checkNameIncludeInProperties(parameterSpec: ParameterSpec) {
        if (!propertyNames.contains(parameterSpec.name)) {
            env.error(
                "Property names of SafeBundle annotated class must equals to function parameter names of target class",
                annotatedClassElement
            )
        }
    }

    private fun checkMatchingType(parameterSpec: ParameterSpec) {
        val property = requireNotNull(propertiesMap[parameterSpec.name])
        if (parameterSpec.type.regardlessOfNull != property.kotlinProperty.type.regardlessOfNull) {
            env.error(
                "Property types of SafeBundle annotated class equals to function parameter types of target class",
                targetClassElement
            )
        }
    }

    private fun checkNullability(parameterSpec: ParameterSpec) {
        val property = requireNotNull(propertiesMap[parameterSpec.name])
        if (parameterSpec.type.isNullable && !property.kotlinProperty.type.isNullable) {
            env.error(
                "Check nullability of properties and parameter",
                annotatedClassElement
            )
        }
    }

    private fun checkRemainProperty(usedPropertyNameSet: Set<String>) {
        if (usedPropertyNameSet.size < numOfRequiredProperties) {
            env.error(
                "Check number of property and parameter",
                annotatedClassElement
            )
        }
    }

    private val TypeName.regardlessOfNull: String
        get() = toString().removeSuffix("?")
}