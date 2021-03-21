package com.sabgil.processor.common.model.result

import com.sabgil.processor.common.model.Function
import com.sabgil.processor.common.model.TargetClass
import com.squareup.kotlinpoet.ParameterSpec
import javax.lang.model.element.TypeElement

data class TargetClassAnalyzeResult(
    val targetClass: TargetClass,
    val functions: List<Function>
)