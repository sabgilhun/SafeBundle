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

fun TypeElement.toClassName(): ClassName {
    val packageNames = packageElement().qualifiedName.split(".")
    val fullNames = qualifiedName.split(".").toMutableList().apply { removeAll(packageNames) }
    return ClassName(packageNames.joinToString("."), fullNames.joinToString("."))
}

val Element.name: String get() = this.simpleName.toString()

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