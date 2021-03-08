package com.sabgil.processor.common.ext

import com.squareup.kotlinpoet.ClassName
import org.jetbrains.annotations.Nullable
import javax.lang.model.element.*

fun Element.packageElement(): PackageElement {
    var element = this

    while (element !is PackageElement) {
        element = element.enclosingElement
    }

    return element
}


fun Element.packageName(): String = packageElement().qualifiedName.toString()

fun Element.outerClassElement(): TypeElement {
    var element = this

    while (element !is TypeElement) {
        element = element.enclosingElement
    }

    return element
}

fun Element.outerClassSimpleName(): String = outerClassElement().simpleName.toString()

fun Element.toClassName(): ClassName = ClassName(packageName(), outerClassSimpleName())

val Element.isAbstract
    get() = modifiers.contains(Modifier.ABSTRACT)

val VariableElement.isNullable
    get(): Boolean {
        val nullableAnnotation = annotationMirrors.find {
            it.annotationType.asElement().toString() == Nullable::class.java.canonicalName
        }
        val isPrimitive = asType().kind.isPrimitive

        return if (isPrimitive) {
            false
        } else {
            nullableAnnotation != null
        }
    }