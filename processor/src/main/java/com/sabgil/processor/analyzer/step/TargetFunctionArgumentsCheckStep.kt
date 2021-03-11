package com.sabgil.processor.analyzer.step

import com.sabgil.processor.analyzer.result.NavigatorTargetFunctionsCheckResult
import com.sabgil.processor.common.Step
import com.squareup.kotlinpoet.TypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element


class TargetFunctionArgumentsCheckStep :
    Step<NavigatorTargetFunctionsCheckResult, NavigatorTargetFunctionsCheckResult>() {

    override fun process(
        rootElement: Element,
        env: ProcessingEnvironment,
        input: NavigatorTargetFunctionsCheckResult
    ): NavigatorTargetFunctionsCheckResult {
        val propertyNames = input.argumentsMap.keys
        val requiredProperties = input.argumentsMap.values.filter {
            !it.kotlinProperty.type.isNullable
        }
        input.targetFunctionElements.forEach { func ->
            val usedPropertyNameSet = mutableSetOf<String>()
            func.kotlinFun.parameters.zip(func.jvmMethod.parameters) { kotlinParam, _ ->
                if (!propertyNames.contains(kotlinParam.name)) {
                    TODO("TargetFunctionArgumentsCheckStep, error report")
                }

                val property = requireNotNull(input.argumentsMap[kotlinParam.name])
                if (kotlinParam.type.regardlessOfNull != property.kotlinProperty.type.regardlessOfNull) {
                    TODO("TargetFunctionArgumentsCheckStep, error report")
                }

                if (kotlinParam.type.isNullable && !property.kotlinProperty.type.isNullable) {
                    TODO("TargetFunctionArgumentsCheckStep, error report")
                }
                usedPropertyNameSet.add(kotlinParam.name)
            }

            if (usedPropertyNameSet.size < requiredProperties.size) {
                TODO("TargetFunctionArgumentsCheckStep, error report")
            }
        }

        return input
    }

    private val TypeName.regardlessOfNull: String
        get() = toString().removeSuffix("?")
}