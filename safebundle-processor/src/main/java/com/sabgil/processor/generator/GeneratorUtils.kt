package com.sabgil.processor.generator

import com.sabgil.processor.common.ext.lowerSimpleName
import com.sabgil.processor.common.ext.toClassName
import com.sabgil.processor.common.model.activityClassName
import com.sabgil.processor.common.model.contextClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import javax.lang.model.element.TypeElement

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

fun TypeSpec.Builder.addActivityPropConstructor(): TypeSpec.Builder {
    addProperty(activityClassName.lowerSimpleName, activityClassName, KModifier.PRIVATE)
        .addFunction(
            FunSpec.constructorBuilder()
                .addParameter(activityClassName.lowerSimpleName, activityClassName)
                .addStatement(
                    "this.%N = %N",
                    activityClassName.lowerSimpleName,
                    activityClassName.lowerSimpleName
                )
                .build()
        )
    return this
}

fun makeGeneratedClassName(targetElement: TypeElement) =
    "${targetElement.toClassName().simpleName.replace(".", "_")}_SafeBundleImpl"
