package com.sabgil.processor.common.model.result

import com.sabgil.processor.common.model.element.KotlinFunElement
import javax.lang.model.element.TypeElement

data class TargetClassAnalyzeResult(
    val targetClassElement: TypeElement,
    val targetClassFunElements: List<KotlinFunElement>,
    val useForResultMap: Map<KotlinFunElement, Boolean>,
    val isIncludeForResult: Boolean
)