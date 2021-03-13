package com.sabgil.processor.analyzer

import com.sabgil.processor.common.model.result.AnnotatedClassAnalyzeResult
import com.sabgil.processor.common.model.result.TargetClassAnalyzeResult
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName

class MatchingChecker(
    annotatedClassAnalyzeResult: AnnotatedClassAnalyzeResult,
    targetClassAnalyzeResult: TargetClassAnalyzeResult
) {

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
            TODO("TargetFunctionArgumentsCheckStep, error report")
        }
    }

    private fun checkMatchingType(parameterSpec: ParameterSpec) {
        val property = requireNotNull(propertiesMap[parameterSpec.name])
        if (parameterSpec.type.regardlessOfNull != property.kotlinProperty.type.regardlessOfNull) {
            TODO("TargetFunctionArgumentsCheckStep, error report")
        }

    }

    private fun checkNullability(parameterSpec: ParameterSpec) {
        val property = requireNotNull(propertiesMap[parameterSpec.name])
        if (parameterSpec.type.isNullable && !property.kotlinProperty.type.isNullable) {
            TODO("TargetFunctionArgumentsCheckStep, error report")
        }
    }

    private fun checkRemainProperty(usedPropertyNameSet: Set<String>) {
        if (usedPropertyNameSet.size < numOfRequiredProperties) {
            TODO("TargetFunctionArgumentsCheckStep, error report")
        }
    }

    private val TypeName.regardlessOfNull: String
        get() = toString().removeSuffix("?")
}