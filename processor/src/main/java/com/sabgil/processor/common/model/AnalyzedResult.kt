package com.sabgil.processor.common.model

import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

data class AnalyzedResult(
    val rootElement: Element,
    val argumentsMap: Map<String, DelegateElement>,
    val targetElement: Element,
    val targetFunctionElements: List<ExecutableElement>
)