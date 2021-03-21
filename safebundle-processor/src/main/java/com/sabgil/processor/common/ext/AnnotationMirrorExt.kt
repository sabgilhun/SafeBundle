package com.sabgil.processor.common.ext

import javax.lang.model.element.AnnotationMirror

fun AnnotationMirror.extractAnnotationValue(): String {
    val key = elementValues.keys.first {
        it.simpleName.contentEquals("clazz")
    }
    return requireNotNull(elementValues[key]?.value?.toString())
}
