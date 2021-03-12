package com.sabgil.processor.common.model

import com.sabgil.processor.common.model.element.KotlinDelegateElement
import javax.lang.model.element.TypeElement

data class AnnotatedClassAnalyzeResult(
    val annotatedClassElement: TypeElement,
    val inheritanceType: InheritanceType,
    val propertiesMap: Map<String, KotlinDelegateElement>
)