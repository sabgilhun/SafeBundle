package com.sabgil.processor.generator

import com.sabgil.processor.common.ext.packageName
import com.sabgil.processor.common.ext.toClassName
import com.sabgil.processor.common.model.AnnotatedClassAnalyzeResult
import com.sabgil.processor.common.model.TargetClassAnalyzeResult
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec

class FragmentTypeGenerator(
    private val annotatedClassAnalyzeResult: AnnotatedClassAnalyzeResult,
    private val targetClassAnalyzeResult: TargetClassAnalyzeResult
) {
    private val packageName = annotatedClassAnalyzeResult.annotatedClassElement.packageName()
    private val annotatedClassName = annotatedClassAnalyzeResult.annotatedClassElement.toClassName()
    private val targetClassName = targetClassAnalyzeResult.targetClassElement.toClassName()
    private val generatingClassName =
        "${annotatedClassName.simpleName}_${targetClassName.simpleName.replace(".", "_")}_Impl"

    fun generate() = FileSpec.builder(packageName, generatingClassName)
        .addType(classBuild())
        .build()

    private fun classBuild(): TypeSpec =
        TypeSpec.classBuilder(generatingClassName)
            .addSuperinterface(targetClassName)
            .addOverrideFunctions()
            .build()

    private fun TypeSpec.Builder.addOverrideFunctions(): TypeSpec.Builder {
        val funSpecs = targetClassAnalyzeResult.targetClassFunElements.map {
            FunSpec.builder(it.kotlinFun.name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameters(it.kotlinFun.parameters)
                .addCodeBlock(it.kotlinFun)
                .returns(annotatedClassName)
                .build()
        }
        addFunctions(funSpecs)
        return this
    }

    private fun FunSpec.Builder.addCodeBlock(funSpec: FunSpec): FunSpec.Builder {
        addStatement(
            "val f = %T()",
            annotatedClassAnalyzeResult.annotatedClassElement.asType()
        )
        addStatement("val b = androidx.core.os.bundleOf(")
        funSpec.parameters.map {
            addStatement("%S to %L,", it.name, it.name)
        }
        addStatement(")")
        addStatement("f.arguments = b")
        addStatement("return f")
        return this
    }
}