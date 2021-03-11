package com.sabgil.processor.analyzer.result

import com.sabgil.processor.common.Parameterizable
import com.sabgil.processor.common.model.kelement.KotlinDelegateElement

data class ArgumentsCheckResult(
    val argumentsMap: Map<String, KotlinDelegateElement>
) : Parameterizable