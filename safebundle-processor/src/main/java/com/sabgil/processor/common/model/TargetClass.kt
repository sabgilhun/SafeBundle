package com.sabgil.processor.common.model

import com.sabgil.processor.common.ext.toClassName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.metadata.specs.toTypeSpec
import javax.lang.model.element.TypeElement

data class TargetClass(
    val element: TypeElement
) {
    val className: ClassName = element.toClassName()

    val toBoOverridingFunSpecs =
        element.toTypeSpec().funSpecs.filter { it.modifiers.contains(KModifier.ABSTRACT) }
}