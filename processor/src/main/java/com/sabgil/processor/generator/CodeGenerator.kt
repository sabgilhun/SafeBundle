package com.sabgil.processor.generator

import com.sabgil.processor.common.model.AnnotatedClassAnalyzeResult
import com.sabgil.processor.common.model.InheritanceType
import com.sabgil.processor.common.model.TargetClassAnalyzeResult

class CodeGenerator(
    private val annotatedClassAnalyzeResult: AnnotatedClassAnalyzeResult,
    private val targetClassAnalyzeResult: TargetClassAnalyzeResult
) {

    fun generate() =
        when (annotatedClassAnalyzeResult.inheritanceType) {
            InheritanceType.ACTIVITY -> ActivityTypeGenerator(
                annotatedClassAnalyzeResult,
                targetClassAnalyzeResult
            ).generate()

            InheritanceType.FRAGMENT -> FragmentTypeGenerator(
                annotatedClassAnalyzeResult,
                targetClassAnalyzeResult
            ).generate()
        }
}