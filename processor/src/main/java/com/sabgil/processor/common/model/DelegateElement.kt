package com.sabgil.processor.common.model

import com.squareup.kotlinpoet.TypeName
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

data class DelegateElement(
    val variable: VariableElement,
    val getter: ExecutableElement,
    val type: TypeName,
    val isNullable: Boolean
)