package com.sabgil.processor.generator

import com.sabgil.processor.common.ext.packageName
import com.sabgil.processor.common.ext.toClassName
import com.sabgil.processor.common.model.AnalyzedResult
import com.sabgil.processor.common.types.intentPackageName
import com.squareup.kotlinpoet.*
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

class NavigatorImplGenerator(
    private val analyzedResult: AnalyzedResult
) {
    fun generator() = FileSpec.builder(
        analyzedResult.rootElement.packageName(),
        analyzedResult.rootElement.toClassName().simpleName + "_Navigator_Impl"
    ).addType(classBuild())
        .build()

    private fun classBuild(): TypeSpec =
        TypeSpec.classBuilder(analyzedResult.rootElement.toClassName().simpleName + "_Navigator_Impl")
            .addProperty("context", ClassName("android.content", "Context"), KModifier.PRIVATE)
            .addFunction(
                FunSpec.constructorBuilder()
                    .addParameter("context", ClassName("android.content", "Context"))
                    .addStatement("this.%N = %N", "context", "context")
                    .build()
            )
            .addSuperinterface(
                ClassName(
                    analyzedResult.targetElement.packageName(),
                    (analyzedResult.targetElement as TypeElement).qualifiedName.toString()
                )
            )
            .addImplFunctions()
            .build()

    private fun TypeSpec.Builder.addImplFunctions(): TypeSpec.Builder {
        val funSpecs = analyzedResult.targetFunctionElements.map {
            FunSpec.builder(it.simpleName.toString())
                .addModifiers(KModifier.OVERRIDE)
                .addImplFunParams(it)
                .addCodeBlock(it)
                .build()
        }
        addFunctions(funSpecs)
        return this
    }

    private fun FunSpec.Builder.addImplFunParams(executableElement: ExecutableElement): FunSpec.Builder {
        val paramSpecs = executableElement.parameters.map {
            ParameterSpec
                .builder(
                    it.simpleName.toString(),
                    analyzedResult.argumentsMap[it.simpleName.toString()]?.type!!
                )
                .build()
        }
        addParameters(paramSpecs)
        return this
    }

    private fun FunSpec.Builder.addCodeBlock(executableElement: ExecutableElement): FunSpec.Builder {
        addStatement(
            "val i = %L(context, %T::class.java)",
            intentPackageName,
            analyzedResult.rootElement.asType()
        )
        executableElement.parameters.map {
            addStatement("i.putExtra(%S, %L)", it.simpleName.toString(), it.simpleName.toString())
        }
        addStatement("context.startActivity(i)")
        return this
    }
}