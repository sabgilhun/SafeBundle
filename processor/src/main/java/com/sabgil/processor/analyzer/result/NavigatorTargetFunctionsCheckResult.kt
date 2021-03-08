package com.sabgil.processor.analyzer.result

import com.sabgil.processor.common.Parameterizable
import com.sabgil.processor.common.model.DelegateElement
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

data class NavigatorTargetFunctionsCheckResult(
    val argumentsMap: Map<String, DelegateElement>,
    val targetElement: Element,
    val targetFunctionElements: List<ExecutableElement>
) : Parameterizable