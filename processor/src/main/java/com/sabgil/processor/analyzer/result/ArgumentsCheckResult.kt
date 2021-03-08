package com.sabgil.processor.analyzer.result

import com.sabgil.processor.common.Parameterizable
import com.sabgil.processor.common.model.DelegateElement
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

data class ArgumentsCheckResult(
    val argumentsMap: Map<String, DelegateElement>
) : Parameterizable