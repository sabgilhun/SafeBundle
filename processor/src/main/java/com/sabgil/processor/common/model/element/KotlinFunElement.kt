package com.sabgil.processor.common.model.element

import com.squareup.kotlinpoet.FunSpec
import javax.lang.model.element.ExecutableElement

data class KotlinFunElement(
    val kotlinFun: FunSpec,
    val jvmMethod: ExecutableElement
)