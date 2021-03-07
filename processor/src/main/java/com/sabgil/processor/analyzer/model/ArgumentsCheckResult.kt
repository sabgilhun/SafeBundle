package com.sabgil.processor.analyzer.model

import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

data class ArgumentsCheckResult(
    val argumentsMap: Map<VariableElement, ExecutableElement>
) : Parameterizable