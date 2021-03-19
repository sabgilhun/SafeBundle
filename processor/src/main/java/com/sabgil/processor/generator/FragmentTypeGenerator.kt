package com.sabgil.processor.generator

import com.sabgil.processor.common.ext.toClassName
import com.sabgil.processor.common.model.Function
import com.sabgil.processor.common.model.result.AnnotatedClassAnalyzeResult
import com.sabgil.processor.common.model.result.TargetClassAnalyzeResult
import com.squareup.kotlinpoet.*

class FragmentTypeGenerator(
    annotatedClassAnalyzeResult: AnnotatedClassAnalyzeResult,
    private val targetClassAnalyzeResult: TargetClassAnalyzeResult
) {
    private val annotatedClass = annotatedClassAnalyzeResult.annotatedClass
    private val targetClassName = targetClassAnalyzeResult.targetClassElement.toClassName()
    private val generatingClassName =
        "${annotatedClass.simpleName}_${targetClassName.simpleName.replace(".", "_")}_Impl"

    fun generate() = FileSpec.builder(annotatedClass.packageName, generatingClassName)
        .addType(classBuild())
        .build()

    private fun classBuild(): TypeSpec =
        TypeSpec.classBuilder(generatingClassName)
            .addSuperinterface(targetClassName)
            .addOverrideFunctions()
            .build()

    private fun TypeSpec.Builder.addOverrideFunctions(): TypeSpec.Builder {
        val funSpecs = targetClassAnalyzeResult.functions.map {
            FunSpec.builder(it.name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameters(it)
                .addCodeBlock(it)
                .returns(annotatedClass.className)
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
        addStatement(
            "val f = %T()",
            annotatedClass.elementType
        )
        addStatement("val b = androidx.core.os.bundleOf(")
        function.parameters.map {
            addStatement("%S to %L,", it.name, it.name)
        }
        addStatement(")")
        addStatement("f.arguments = b")
        addStatement("return f")
        return this
    }
}