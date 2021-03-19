package com.sabgil.processor.generator

import com.sabgil.processor.common.model.result.AnnotatedClassAnalyzeResult
import com.sabgil.processor.common.model.result.TargetClassAnalyzeResult
import com.squareup.kotlinpoet.*

class ActivityTypeGenerator(
    annotatedClassAnalyzeResult: AnnotatedClassAnalyzeResult,
    targetClassAnalyzeResult: TargetClassAnalyzeResult
) {
    private val annotatedClass = annotatedClassAnalyzeResult.annotatedClass
    private val targetClass = targetClassAnalyzeResult.targetClass
    private val generateClassName = makeGeneratedClassName(targetClass.element)
    private val isIncludeForResult = targetClassAnalyzeResult.functions.any { it.isForResult }

    fun generate() = FileSpec.builder(annotatedClass.packageName, generateClassName)
        .addType(classBuild())
        .build()

    private fun classBuild(): TypeSpec =
        TypeSpec.classBuilder(generateClassName)
            .addConstructor()
            .addSuperinterface(targetClass.className)
            .addOverrideFunctions()
            .build()

    private fun TypeSpec.Builder.addConstructor() =
        if (isIncludeForResult) {
            addActivityPropConstructor()
        } else {
            addContextPropConstructor()
        }

    private fun TypeSpec.Builder.addOverrideFunctions(): TypeSpec.Builder {
        val funSpecs = targetClass.toBoOverridingFunSpecs.map {
            FunSpec.builder(it.name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameters(it)
                .addCodeBlock(it)
                .build()
        }
        addFunctions(funSpecs)
        return this
    }

    private fun FunSpec.Builder.addParameters(funSpecs: FunSpec): FunSpec.Builder {
        funSpecs.parameters.forEach {
            val parameterSpec = ParameterSpec.builder(it.name, it.type)
                .addModifiers(it.modifiers)
                .build()
            addParameter(parameterSpec)
        }
        return this
    }

    private fun FunSpec.Builder.addCodeBlock(funSpecs: FunSpec): FunSpec.Builder {
        val memberPropName = if (isIncludeForResult) "activity" else "context"
        val isForResult = funSpecs.parameters.any { it.name == "requestCode" }

        addStatement(
            if (isForResult) {
                "val i = android.content.Intent($memberPropName, %T::class.java)"
            } else {
                "val i = android.content.Intent($memberPropName, %T::class.java)"
            },
            annotatedClass.elementType
        )

        funSpecs.parameters.forEach {
            if (it.name != "requestCode") {
                addStatement("i.putExtra(%S, %L)", it.name, it.name)
            }
        }

        addStatement(
            if (isForResult) {
                "$memberPropName.startActivityForResult(i, requestCode)"
            } else {
                "$memberPropName.startActivity(i)"
            },
            annotatedClass.elementType
        )
        return this
    }
}