package com.sabgil.processor.ext

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

fun ProcessingEnvironment.typeElement(
    name: CharSequence
): TypeElement = elementUtils.getTypeElement(name)

fun ProcessingEnvironment.isAssignable(
    t1: TypeMirror,
    t2: TypeMirror
) = typeUtils.isAssignable(t1, t2)
