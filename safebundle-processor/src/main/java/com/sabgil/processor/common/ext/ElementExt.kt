package com.sabgil.processor.common.ext

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import javax.lang.model.element.*

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

fun VariableElement.toTypeName(isNullable: Boolean): TypeName {
    val packageNames = packageElement().qualifiedName.split(".")
    val fullNames = asType().toString().split(".").toMutableList().apply { removeAll(packageNames) }
    val className = ClassName(packageNames.joinToString("."), fullNames.joinToString("."))
    return className.copy(isNullable)
}

val Element.name: String get() = this.simpleName.toString()

val Element.isAbstract
    get() = modifiers.contains(Modifier.ABSTRACT)