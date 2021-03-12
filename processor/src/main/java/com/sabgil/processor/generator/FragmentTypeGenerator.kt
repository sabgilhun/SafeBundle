package com.sabgil.processor.generator

import com.sabgil.processor.common.model.AnnotatedClassAnalyzeResult
import com.sabgil.processor.common.model.TargetClassAnalyzeResult
import com.squareup.kotlinpoet.FileSpec

class FragmentTypeGenerator(
    private val annotatedClassAnalyzeResult: AnnotatedClassAnalyzeResult,
    private val targetClassAnalyzeResult: TargetClassAnalyzeResult
) {

    fun generate() = FileSpec.builder("", "")
        .build()
}