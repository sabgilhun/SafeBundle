package com.sabgil.processor.analyzer.result

import com.sabgil.processor.common.Parameterizable
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

data class NavigatorTargetFunctionsCheckResult(
    val argumentsMap: Map<String, Pair<VariableElement, ExecutableElement>>,
    val targetElement: Element,
    val targetFunctionElements: List<ExecutableElement>
) : Parameterizable