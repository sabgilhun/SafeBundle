package com.sabgil.processor.generator

import com.sabgil.processor.common.ext.lowerSimpleName
import com.sabgil.processor.common.ext.packageName
import com.sabgil.processor.common.ext.toClassName
import com.sabgil.processor.common.model.AnnotatedClassAnalyzeResult
import com.sabgil.processor.common.model.TargetClassAnalyzeResult
import com.sabgil.processor.common.types.contextClassName
import com.sabgil.processor.common.types.intentPackageName
import com.squareup.kotlinpoet.*

class CodeGenerator(
    private val annotatedClassAnalyzeResult: AnnotatedClassAnalyzeResult,
    private val targetClassAnalyzeResult: TargetClassAnalyzeResult
) {
    private val packageName = annotatedClassAnalyzeResult.annotatedClassElement.packageName()

    private val className =
        "${annotatedClassAnalyzeResult.annotatedClassElement.toClassName().simpleName}_Navigator_Impl"

    private val targetClassName = targetClassAnalyzeResult.targetClassElement.toClassName()

    fun generate() = FileSpec.builder(packageName, className)
        .addType(classBuild())
        .build()

    private fun classBuild(): TypeSpec =
        TypeSpec.classBuilder(className)
            .addConstructor()
            .addSuperinterface(targetClassName)
            .addImplFunctions()
            .build()

    private fun TypeSpec.Builder.addConstructor(): TypeSpec.Builder {
        addPropertyWithClassName(contextClassName, KModifier.PRIVATE)
            .addFunction(
                FunSpec.constructorBuilder()
                    .addParameterWithClassName(contextClassName)
                    .addStatement(
                        "this.%N = %N",
                        contextClassName.lowerSimpleName,
                        contextClassName.lowerSimpleName
                    )
                    .build()
            )
        return this
    }

    private fun TypeSpec.Builder.addImplFunctions(): TypeSpec.Builder {
        val funSpecs = targetClassAnalyzeResult.targetClassFunElements.map {
            FunSpec.builder(it.kotlinFun.name)
                .addModifiers(KModifier.OVERRIDE)
                .addParameters(it.kotlinFun.parameters)
                .addCodeBlock(it.kotlinFun)
                .build()
        }
        addFunctions(funSpecs)
        return this
    }

    private fun FunSpec.Builder.addCodeBlock(funSpec: FunSpec): FunSpec.Builder {
        addStatement(
            "val i = %L(context, %T::class.java)",
            intentPackageName,
            annotatedClassAnalyzeResult.annotatedClassElement.asType()
        )
        funSpec.parameters.map {
            addStatement("i.putExtra(%S, %L)", it.name, it.name)
        }
        addStatement("context.startActivity(i)")
        return this
    }

    private fun TypeSpec.Builder.addPropertyWithClassName(
        className: ClassName,
        vararg modifiers: KModifier
    ): TypeSpec.Builder {
        addProperty(className.lowerSimpleName, className, *modifiers)
        return this
    }

    private fun FunSpec.Builder.addParameterWithClassName(
        className: ClassName,
        vararg modifiers: KModifier
    ): FunSpec.Builder {
        addParameter(className.lowerSimpleName, className, *modifiers)
        return this
    }
}