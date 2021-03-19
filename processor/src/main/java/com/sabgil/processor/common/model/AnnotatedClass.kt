package com.sabgil.processor.common.model

import com.sabgil.processor.common.ext.toClassName
import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

data class AnnotatedClass(
    val element: TypeElement,
    val inheritanceType: InheritanceType
) {
    val className : ClassName = element.toClassName()

    val packageName: String = className.packageName

    val simpleName: String = className.simpleName

    val elementType: TypeMirror = element.asType()
}