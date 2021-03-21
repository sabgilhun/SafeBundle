package com.sabgil.processor.common.model.result

import com.sabgil.processor.common.model.AnnotatedClass
import com.sabgil.processor.common.model.Property

data class AnnotatedClassAnalyzeResult(
    val annotatedClass: AnnotatedClass,
    val properties: Map<String, Property>
)