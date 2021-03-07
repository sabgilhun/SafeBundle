package com.sabgil.processor.analyzer.step

import com.sabgil.processor.analyzer.model.ArgumentsCheckResult
import com.sabgil.processor.analyzer.model.NavigatorTargetCheckResult
import com.sabgil.processor.ext.isAssignable
import com.sabgil.processor.ext.typeElement
import com.sabgil.processor.types.contextBasedNavigatorMarkPackageName
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
            it.annotationType == env.typeElement(navigatorPackageName).asType()
        }

        val targetElement = env.typeElement(extractAnnotationValue(annotationMirror))

        println(isAbstract(targetElement))
        println(env.isImplementMark(targetElement))
        if (!isAbstract(targetElement) || !env.isImplementMark(targetElement)) {
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

    private fun isAbstract(element: Element) = element.modifiers.contains(Modifier.ABSTRACT)

    private fun ProcessingEnvironment.isImplementMark(element: Element) =
        isAssignable(element.asType(), typeElement(contextBasedNavigatorMarkPackageName).asType())

    companion object {
        private const val ANNOTATION_PROP_NAME = "clazz"
    }
}