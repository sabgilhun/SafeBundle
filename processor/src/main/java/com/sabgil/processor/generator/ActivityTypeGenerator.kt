package com.sabgil.processor.generator

import com.sabgil.processor.common.ext.packageName
import com.sabgil.processor.common.ext.toClassName
import com.sabgil.processor.common.model.element.KotlinFunElement
import com.sabgil.processor.common.model.result.AnnotatedClassAnalyzeResult
import com.sabgil.processor.common.model.result.TargetClassAnalyzeResult
import com.squareup.kotlinpoet.*

class ActivityTypeGenerator(
    private val annotatedClassAnalyzeResult: AnnotatedClassAnalyzeResult,
    private val targetClassAnalyzeResult: TargetClassAnalyzeResult
) {
    private val packageName = annotatedClassAnalyzeResult.annotatedClassElement.packageName()
    private val targetClassName = targetClassAnalyzeResult.targetClassElement.toClassName()
    private val generateClassName = "${targetClassName.simpleName.replace(".", "_")}_SafeBundleImpl"
    private val requestCodeMap = targetClassAnalyzeResult.requestCodeMap
    private val isIncludeForResult = targetClassAnalyzeResult.isIncludeForResult

    fun generate() = FileSpec.builder(packageName, generateClassName)
        .addType(classBuild())
        .build()

    private fun classBuild(): TypeSpec =
        TypeSpec.classBuilder(generateClassName)
            .addConstructor()
            .addSuperinterface(targetClassName)
            .addOverrideFunctions()
            .build()

    private fun TypeSpec.Builder.addConstructor() =
        if (isIncludeForResult) {
            addActivityPropConstructor()
        } else {
            addContextPropConstructor()
        }

    private fun TypeSpec.Builder.addOverrideFunctions(): TypeSpec.Builder {
        val funSpecs = targetClassAnalyzeResult.targetClassFunElements.map {
            FunSpec.builder(it.kotlinFun.name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameters(it.kotlinFun.parameters)
                .addCodeBlock(it)
                .build()
        }
        addFunctions(funSpecs)
        return this
    }

    private fun FunSpec.Builder.addCodeBlock(kotlinFunElement: KotlinFunElement): FunSpec.Builder {
        if (isIncludeForResult) {
            addStatement(
                "val i = android.content.Intent(activity, %T::class.java)",
                annotatedClassAnalyzeResult.annotatedClassElement.asType()
            )
            kotlinFunElement.excludeRequestCodeParam().forEach {
                addStatement("i.putExtra(%S, %L)", it.name, it.name)
            }
            addStatement("activity.startActivityForResult(i, requestCode)")
        } else {
            addStatement(
                "val i = android.content.Intent(context, %T::class.java)",
                annotatedClassAnalyzeResult.annotatedClassElement.asType()
            )
            kotlinFunElement.kotlinFun.parameters.forEach {
                addStatement("i.putExtra(%S, %L)", it.name, it.name)
            }
            addStatement("context.startActivity(i)")
        }
        return this
    }

    private fun KotlinFunElement.excludeRequestCodeParam(): List<ParameterSpec> {
        val requestCodeParam = requestCodeMap[this]
        return if (requestCodeParam != null) {
            kotlinFun.parameters.filter { it != requestCodeParam }
        } else {
            kotlinFun.parameters
        }
    }
}