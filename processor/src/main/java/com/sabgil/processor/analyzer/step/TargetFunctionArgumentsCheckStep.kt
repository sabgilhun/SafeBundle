package com.sabgil.processor.analyzer.step

import com.sabgil.processor.analyzer.result.NavigatorTargetFunctionsCheckResult
import com.sabgil.processor.common.Step
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element


class TargetFunctionArgumentsCheckStep :
    Step<NavigatorTargetFunctionsCheckResult, NavigatorTargetFunctionsCheckResult>() {

    override fun process(
        rootElement: Element,
        env: ProcessingEnvironment,
        input: NavigatorTargetFunctionsCheckResult
    ): NavigatorTargetFunctionsCheckResult {
        input.targetFunctionElements.forEach { func ->
            val bundleParamNames = input.argumentsMap.keys.toMutableList()
            func.parameters.forEach { param ->
                val paramName = param.simpleName.toString()
                if (!bundleParamNames.remove(paramName) ||
                    param.asType() != input.argumentsMap[paramName]?.second?.returnType
                ) {
                    TODO("TargetFunctionArgumentsCheckStep, error report")
                }
            }

            if (bundleParamNames.isNotEmpty()) {
                TODO("TargetFunctionArgumentsCheckStep, error report")
            }
        }

        return input
    }
}