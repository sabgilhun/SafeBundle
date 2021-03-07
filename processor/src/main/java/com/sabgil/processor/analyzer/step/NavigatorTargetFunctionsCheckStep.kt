package com.sabgil.processor.analyzer.step

import com.sabgil.processor.analyzer.model.NavigatorTargetCheckResult
import com.sabgil.processor.analyzer.model.NavigatorTargetFunctionsCheckResult
import com.sabgil.processor.ext.typeElement
import com.sabgil.processor.types.unitPackageName
import com.sabgil.processor.utils.isAbstract
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.TypeKind


class NavigatorTargetFunctionsCheckStep :
    Step<NavigatorTargetCheckResult, NavigatorTargetFunctionsCheckResult>() {

    override fun process(
        rootElement: Element,
        env: ProcessingEnvironment,
        input: NavigatorTargetCheckResult
    ): NavigatorTargetFunctionsCheckResult {
        val functions = input.targetElement.enclosedElements
            .filterIsInstance<ExecutableElement>()
            .filter { it.isAbstract }

        if (!functions.all { it.returnType.kind == TypeKind.VOID }) {
            TODO("NavigatorTargetFunctionsCheckStep, error report")
        }

        return NavigatorTargetFunctionsCheckResult(
            input.argumentsMap,
            input.targetElement,
            functions
        )
    }
}