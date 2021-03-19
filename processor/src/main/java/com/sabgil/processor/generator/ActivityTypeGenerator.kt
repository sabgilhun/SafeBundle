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
            FunSpec.builder("").build()
            // TODO: after modify param
//            FunSpec.builder(it.kotlinFun.name)
//                .addModifiers(KModifier.OVERRIDE)
//                .addParameters(it.kotlinFun.parameters)
//                .addCodeBlock(it)
//                .build()
        }
        addFunctions(funSpecs)
        return this
    }

    private fun FunSpec.Builder.addCodeBlock(function: Function): FunSpec.Builder {
        if (isIncludeForResult) {
            addStatement(
                "val i = android.content.Intent(activity, %T::class.java)",
                annotatedClass.elementType
            )
            function.excludeRequestCodeParam().forEach {
                addStatement("i.putExtra(%S, %L)", it.name, it.name)
            }
            addStatement("activity.startActivityForResult(i, requestCode)")
        } else {
            addStatement(
                "val i = android.content.Intent(context, %T::class.java)",
                annotatedClass.elementType
            )
            // TODO: after modify param
//            function.kotlinFun.parameters.forEach {
//                addStatement("i.putExtra(%S, %L)", it.name, it.name)
//            }
            addStatement("context.startActivity(i)")
        }
        return this
    }

    private fun Function.excludeRequestCodeParam(): List<ParameterSpec> {
        val requestCodeParam = requestCodeMap[this]
        return emptyList()
        // TODO: after modify param
//        return if (requestCodeParam != null) {
//            kotlinFun.parameters.filter { it != requestCodeParam }
//        } else {
//            kotlinFun.parameters
//        }
    }
}