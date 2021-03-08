package com.sabgil.processor.analyzer.step

import com.sabgil.processor.analyzer.result.ArgumentsCheckResult
import com.sabgil.processor.analyzer.result.NavigatorTargetCheckResult
import com.sabgil.processor.common.Step
import com.sabgil.processor.common.ext.isAbstract
import com.sabgil.processor.common.ext.isAssignable
import com.sabgil.processor.common.ext.typeElement
import com.sabgil.processor.common.types.contextBasedNavigatorMarkPackageName
import com.sabgil.processor.common.types.navigatorPackageName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element


class NavigatorTargetCheckStep : Step<ArgumentsCheckResult, NavigatorTargetCheckResult>() {

    override fun process(
        rootElement: Element,
        env: ProcessingEnvironment,
        input: ArgumentsCheckResult
    ): NavigatorTargetCheckResult {
        val annotationMirror = rootElement.annotationMirrors.first {
            it.annotationType == env.typeElement(navigatorPackageName).asType()
        }

        val targetElement = env.typeElement(extractAnnotationValue(annotationMirror))

        if (!targetElement.isAbstract || !env.isImplementMark(targetElement)) {
            TODO("NavigatorTargetCheckStep, error report")
        }

        return NavigatorTargetCheckResult(input.argumentsMap, targetElement)
    }

    private fun extractAnnotationValue(annotationMirror: AnnotationMirror): String {
        val key = annotationMirror.elementValues.keys.first {
            it.simpleName.contentEquals(ANNOTATION_PROP_NAME)
        }
        return requireNotNull(annotationMirror.elementValues[key]?.value?.toString())
    }

    private fun ProcessingEnvironment.isImplementMark(element: Element) =
        isAssignable(element.asType(), typeElement(contextBasedNavigatorMarkPackageName).asType())

    companion object {
        private const val ANNOTATION_PROP_NAME = "clazz"
    }
}