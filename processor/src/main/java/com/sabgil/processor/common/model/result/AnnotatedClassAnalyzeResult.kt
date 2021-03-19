package com.sabgil.processor.common.model.result

import com.sabgil.processor.common.model.AnnotatedClass
import com.sabgil.processor.common.model.element.KotlinDelegateElement

data class AnnotatedClassAnalyzeResult(
    val annotatedClass: AnnotatedClass,
    val propertiesMap: Map<String, KotlinDelegateElement>
)