package com.sabgil.processor.common.model

import com.sabgil.processor.common.model.kelement.KotlinDelegateElement
import com.sabgil.processor.common.model.kelement.KotlinFunElement
import javax.lang.model.element.TypeElement

data class AnalyzedResult(
    val rootElement: TypeElement,
    val argumentsMap: Map<String, KotlinDelegateElement>,
    val targetElement: TypeElement,
    val targetFunctionElements: List<KotlinFunElement>
)