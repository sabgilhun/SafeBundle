package com.sabgil.processor.analyzer.result

import com.sabgil.processor.common.Parameterizable
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

data class ArgumentsCheckResult(
    val argumentsMap: Map<VariableElement, ExecutableElement>
) : Parameterizable