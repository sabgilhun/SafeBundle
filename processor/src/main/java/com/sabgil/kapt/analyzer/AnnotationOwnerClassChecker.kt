package com.sabgil.kapt.analyzer

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class AnnotationOwnerClassChecker(
    private val type: Type,
    private val element: Element,
    private val environment: ProcessingEnvironment
) {

    fun check(): Boolean {
        return if (type == Type.NAVIGATOR) {
            val elementTypeMirror =
                element.asType()
            val activityTypeMirror =
                environment.elementUtils.getTypeElement("android.app.Activity").asType()

            environment.typeUtils.isAssignable(elementTypeMirror, activityTypeMirror)
        } else {
            val elementTypeMirror =
                element.asType()
            val fragmentTypeMirror =
                environment.elementUtils.getTypeElement("androidx.fragment.app.Fragment").asType()

            environment.typeUtils.isAssignable(elementTypeMirror, fragmentTypeMirror)
        }
    }

    enum class Type {
        NAVIGATOR, FACTORY
    }
}