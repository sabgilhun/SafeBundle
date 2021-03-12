package com.sabgil.processor.common.model

import com.sabgil.processor.common.model.element.KotlinFunElement
import javax.lang.model.element.TypeElement


data class TargetClassAnalyzeResult(
    val targetClassElement: TypeElement,
    val targetClassFunElements: List<KotlinFunElement>
)