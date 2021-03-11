package com.sabgil.processor.analyzer.step

import com.sabgil.processor.analyzer.result.NavigatorTargetCheckResult
import com.sabgil.processor.analyzer.result.NavigatorTargetFunctionsCheckResult
import com.sabgil.processor.common.Step
import com.sabgil.processor.common.ext.name
import com.sabgil.processor.common.model.kelement.KotlinFunElement
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.toTypeSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement


class NavigatorTargetFunctionsCheckStep :
    Step<NavigatorTargetCheckResult, NavigatorTargetFunctionsCheckResult>() {

    @KotlinPoetMetadataPreview
    override fun process(
        rootElement: Element,
        env: ProcessingEnvironment,
        input: NavigatorTargetCheckResult
    ): NavigatorTargetFunctionsCheckResult {
        val funSpecs = (input.targetElement as TypeElement)
            .toTypeSpec()
            .funSpecs
            .filter { it.modifiers.contains(KModifier.ABSTRACT) }

        if (!funSpecs.all { it.returnType != UNIT }) {
            TODO("NavigatorTargetFunctionsCheckStep, error report")
        }

        val executableElements = input.targetElement.enclosedElements
            .filterIsInstance<ExecutableElement>()

        val kotlinFunElements = funSpecs.map { funSpec ->
            KotlinFunElement(
                funSpec,
                requireNotNull(executableElements.find { it.name == funSpec.name })
            )
        }

        return NavigatorTargetFunctionsCheckResult(
            input.argumentsMap,
            input.targetElement,
            kotlinFunElements
        )
    }
}