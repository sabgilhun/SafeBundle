package com.sabgil.kapt.analyzer

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class AnnotationAnalyzer(
    private val navigatorElements: Set<Element>,
    private val factoryElements: Set<Element>,
    private val environment: ProcessingEnvironment
) {

    fun analyze(): Boolean {
        return AnnotationOwnerClassChecker(
            AnnotationOwnerClassChecker.Type.NAVIGATOR,
            navigatorElements.first(),
            environment
        ).check()
    }
}