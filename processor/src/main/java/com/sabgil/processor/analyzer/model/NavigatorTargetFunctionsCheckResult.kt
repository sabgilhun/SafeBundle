package com.sabgil.processor.analyzer.model

import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

data class NavigatorTargetFunctionsCheckResult(
    val argumentsMap: Map<VariableElement, ExecutableElement>,
    val targetElement: Element,
    val targetFunctionElements: List<ExecutableElement>
) : Parameterizable