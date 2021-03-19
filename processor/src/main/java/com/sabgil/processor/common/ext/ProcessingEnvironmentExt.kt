package com.sabgil.processor.common.ext

import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic

fun ProcessingEnvironment.typeElement(
    name: CharSequence
): TypeElement = elementUtils.getTypeElement(name)

fun ProcessingEnvironment.parseToTypeElement(
    className: ClassName
): TypeElement = elementUtils.getTypeElement(className.canonicalName)

fun ProcessingEnvironment.isAssignable(
    t1: TypeMirror,
    t2: ClassName
) = typeUtils.isAssignable(t1, elementUtils.getTypeElement(t2.canonicalName).asType())

fun ProcessingEnvironment.isAssignable(
    t1: ClassName,
    t2: ClassName
) = typeUtils.isAssignable(
    elementUtils.getTypeElement(t1.canonicalName).asType(),
    elementUtils.getTypeElement(t2.canonicalName).asType()
)

fun ProcessingEnvironment.isSameType(
    t1: TypeMirror,
    t2: TypeMirror
) = typeUtils.isSameType(t1, t2)

fun ProcessingEnvironment.erasure(
    t1: TypeMirror
): TypeMirror = typeUtils.erasure(t1)

@Throws(IllegalStateException::class)
fun ProcessingEnvironment.error(
    message: String,
    element: Element
): Nothing {
    messager.printMessage(Diagnostic.Kind.ERROR, message, element)
    error(message)
}