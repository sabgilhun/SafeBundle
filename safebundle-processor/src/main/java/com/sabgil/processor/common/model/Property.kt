package com.sabgil.processor.common.model

import com.squareup.kotlinpoet.PropertySpec
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

data class Property(
    val kotlinProperty: PropertySpec,
    val jvmField: VariableElement,
    val jvmGetter: ExecutableElement
) {
    val isNullable: Boolean = kotlinProperty.type.isNullable

    val type: TypeMirror = jvmGetter.returnType
}