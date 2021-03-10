package com.sabgil.processor.generator

import com.sabgil.processor.common.ext.packageName
import com.sabgil.processor.common.ext.toClassName
import com.sabgil.processor.common.model.AnalyzedResult
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
}