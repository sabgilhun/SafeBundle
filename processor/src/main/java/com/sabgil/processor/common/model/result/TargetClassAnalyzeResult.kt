package com.sabgil.processor.common.model.result

import com.sabgil.processor.common.model.Function
import com.squareup.kotlinpoet.ParameterSpec
import javax.lang.model.element.TypeElement

data class TargetClassAnalyzeResult(
    val targetClassElement: TypeElement,
    val functions: List<Function>
)