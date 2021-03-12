package com.sabgil.processor.generator

import com.sabgil.processor.common.ext.lowerSimpleName
import com.sabgil.processor.common.types.contextClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec

fun TypeSpec.Builder.addContextPropConstructor(): TypeSpec.Builder {
    addProperty(contextClassName.lowerSimpleName, contextClassName, KModifier.PRIVATE)
        .addFunction(
            FunSpec.constructorBuilder()
                .addParameter(contextClassName.lowerSimpleName, contextClassName)
                .addStatement(
                    "this.%N = %N",
                    contextClassName.lowerSimpleName,
                    contextClassName.lowerSimpleName
                )
                .build()
        )
    return this
}