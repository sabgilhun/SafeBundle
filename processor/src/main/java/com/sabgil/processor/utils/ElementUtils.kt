package com.sabgil.processor.utils

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement

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