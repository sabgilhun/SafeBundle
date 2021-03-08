package com.sabgil.processor.analyzer.step

import com.sabgil.processor.analyzer.result.NavigatorTargetFunctionsCheckResult
import com.sabgil.processor.common.Step
import com.sabgil.processor.common.ext.isAssignable
import com.sabgil.processor.common.ext.isNullable
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
                val delegateElement = requireNotNull(input.argumentsMap[paramName])

                if (!bundleParamNames.remove(paramName) ||
                    !env.isAssignable(param.asType(), delegateElement.getter.returnType) ||
                    (param.isNullable && !delegateElement.isNullable)
                ) {
                    TODO("TargetFunctionArgumentsCheckStep, error report")
                }
            }

            if (!bundleParamNames.none { !requireNotNull(input.argumentsMap[it]).isNullable }) {
                TODO("TargetFunctionArgumentsCheckStep, error report")
            }
        }

        return input
    }
}