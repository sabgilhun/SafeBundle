package com.sabgil.processor.common.model

import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.TypeMirror

data class Function(
    val isForResult: Boolean,
    val element: ExecutableElement,
    val parameters: List<Parameter>
) {
    val returnType: TypeMirror = element.returnType

    val name = element.simpleName.toString()
}