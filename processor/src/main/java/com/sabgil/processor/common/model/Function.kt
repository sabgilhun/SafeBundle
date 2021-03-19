package com.sabgil.processor.common.model

import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.TypeMirror

data class Function(
    val isForResult: Boolean,
    val jvmMethod: ExecutableElement,
    val parameters: List<Parameter>
) {
    val returnType: TypeMirror = jvmMethod.returnType

    val name = jvmMethod.simpleName.toString()
}