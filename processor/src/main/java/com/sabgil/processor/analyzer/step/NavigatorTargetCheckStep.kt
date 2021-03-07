package com.sabgil.processor.analyzer.step

import com.sabgil.processor.analyzer.model.ArgumentsCheckResult
import com.sabgil.processor.analyzer.model.NavigatorTargetCheckResult
import com.sabgil.processor.ext.typeElement
import com.sabgil.processor.types.navigatorPackageName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier


class NavigatorTargetCheckStep : Step<ArgumentsCheckResult, NavigatorTargetCheckResult>() {

    override fun process(
        rootElement: Element,
        env: ProcessingEnvironment,
        input: ArgumentsCheckResult
    ): NavigatorTargetCheckResult {
        val annotationMirror = rootElement.annotationMirrors.first {
            it.annotationType == env.typeElement(navigatorPackageName)
        }

        val targetElement = env.typeElement(extractAnnotationValue(annotationMirror))

        if (!targetElement.modifiers.contains(Modifier.ABSTRACT)) {
            // TODO : error report
        }

        return NavigatorTargetCheckResult(input.argumentsMap, targetElement)
    }

    private fun extractAnnotationValue(annotationMirror: AnnotationMirror): String {
        val key = annotationMirror.elementValues.keys.first {
            it.simpleName.contentEquals(ANNOTATION_PROP_NAME)
        }
        return requireNotNull(annotationMirror.elementValues[key]?.value?.toString())
    }

    companion object {
        private const val ANNOTATION_PROP_NAME = "clazz"
    }
}