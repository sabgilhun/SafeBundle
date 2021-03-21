package com.sabgil.processor.generator

import com.sabgil.processor.common.model.result.AnnotatedClassAnalyzeResult
import com.sabgil.processor.common.model.InheritanceType
import com.sabgil.processor.common.model.result.TargetClassAnalyzeResult

class CodeGenerator(
    private val annotatedClassAnalyzeResult: AnnotatedClassAnalyzeResult,
    private val targetClassAnalyzeResult: TargetClassAnalyzeResult
) {

    fun generate() =
        when (annotatedClassAnalyzeResult.annotatedClass.inheritanceType) {
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