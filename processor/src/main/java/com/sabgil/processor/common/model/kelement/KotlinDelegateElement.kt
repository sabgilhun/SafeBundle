package com.sabgil.processor.common.model.kelement

import com.squareup.kotlinpoet.PropertySpec
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

data class KotlinDelegateElement(
    val kotlinProperty: PropertySpec,
    val jvmField: VariableElement,
    val jvmGetter: ExecutableElement
)