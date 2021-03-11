package com.sabgil.processor.common.ext

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
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

fun TypeElement.toClassName(): ClassName {
    val packageNames = packageElement().qualifiedName.split(".")
    val fullNames = qualifiedName.split(".").toMutableList().apply { removeAll(packageNames) }
    return ClassName(packageNames.joinToString("."), fullNames.joinToString("."))
}

val Element.name: String get() = this.simpleName.toString()

val Element.isAbstract
    get() = modifiers.contains(Modifier.ABSTRACT)