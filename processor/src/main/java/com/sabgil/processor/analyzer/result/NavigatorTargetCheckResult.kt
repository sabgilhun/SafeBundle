package com.sabgil.processor.analyzer.result

import com.sabgil.processor.common.Parameterizable
import com.sabgil.processor.common.model.DelegateElement
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

data class NavigatorTargetCheckResult(
    val argumentsMap: Map<String, DelegateElement>,
    val targetElement: Element
) : Parameterizable