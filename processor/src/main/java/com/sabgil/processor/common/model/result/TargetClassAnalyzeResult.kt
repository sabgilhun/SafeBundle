package com.sabgil.processor.common.model.result

import com.sabgil.processor.common.model.element.KotlinFunElement
import com.squareup.kotlinpoet.ParameterSpec
import javax.lang.model.element.TypeElement

data class TargetClassAnalyzeResult(
    val targetClassElement: TypeElement,
    val targetClassFunElements: List<KotlinFunElement>,
    val requestCodeMap: Map<KotlinFunElement, ParameterSpec>,
    val isIncludeForResult: Boolean
)