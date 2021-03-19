package com.sabgil.processor.generator

import com.sabgil.processor.common.ext.toClassName
import com.sabgil.processor.common.model.Function
import com.sabgil.processor.common.model.result.AnnotatedClassAnalyzeResult
import com.sabgil.processor.common.model.result.TargetClassAnalyzeResult
import com.squareup.kotlinpoet.*

class ActivityTypeGenerator(
    annotatedClassAnalyzeResult: AnnotatedClassAnalyzeResult,
    private val targetClassAnalyzeResult: TargetClassAnalyzeResult
) {
    private val annotatedClass = annotatedClassAnalyzeResult.annotatedClass
    private val packageName = annotatedClassAnalyzeResult.annotatedClass.packageName
    private val targetClassName = targetClassAnalyzeResult.targetClassElement.toClassName()
    private val generateClassName = "${targetClassName.simpleName.replace(".", "_")}_SafeBundleImpl"
    private val isIncludeForResult = targetClassAnalyzeResult.functions.any { it.isForResult }

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
        if (targetClassAnalyzeResult.functions.any { it.isForResult }) {
            addActivityPropConstructor()
        } else {
            addContextPropConstructor()
        }

    private fun TypeSpec.Builder.addOverrideFunctions(): TypeSpec.Builder {
        val funSpecs = targetClassAnalyzeResult.functions.map {
            FunSpec.builder(it.name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameters(it)
                .addCodeBlock(it)
                .build()
        }
        addFunctions(funSpecs)
        return this
    }

    private fun FunSpec.Builder.addParameters(function: Function): FunSpec.Builder {
        function.parameters.forEach {
            val parameterSpec = ParameterSpec.builder(it.name, it.typeName)
                .jvmModifiers(it.modifiers)
                .build()
            addParameter(parameterSpec)
        }
        return this
    }

    private fun FunSpec.Builder.addCodeBlock(function: Function): FunSpec.Builder {
        val memberPropName = if (isIncludeForResult) "activity" else "context"
        addStatement(
            if (function.isForResult) {
                "val i = android.content.Intent($memberPropName, %T::class.java)"
            } else {
                "val i = android.content.Intent($memberPropName, %T::class.java)"
            },
            annotatedClass.elementType
        )

        function.parameters.forEach {
            if (!it.isRequestCodeParam) {
                addStatement("i.putExtra(%S, %L)", it.name, it.name)
            }
        }

        addStatement(
            if (function.isForResult) {
                "$memberPropName.startActivityForResult(i, requestCode)"
            } else {
                "$memberPropName.startActivity(i)"
            },
            annotatedClass.elementType
        )
        return this
    }
}