package com.sabgil.processor.common.model

import com.sabgil.processor.common.ext.toTypeName
import javax.lang.model.element.Modifier
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

data class Parameter(
    val isRequestCodeParam: Boolean,
    val isNullable: Boolean,
    val variableElement: VariableElement
) {
    val name = variableElement.simpleName.toString()

    val type: TypeMirror = variableElement.asType()

    val modifiers: Set<Modifier> = variableElement.modifiers

    val typeName = variableElement.toTypeName(isNullable)
}