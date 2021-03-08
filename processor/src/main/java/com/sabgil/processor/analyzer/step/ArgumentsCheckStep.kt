package com.sabgil.processor.analyzer.step

import com.sabgil.processor.analyzer.result.ArgumentsCheckResult
import com.sabgil.processor.common.Parameterizable.Empty
import com.sabgil.processor.common.Step
import com.sabgil.processor.common.ext.erasure
import com.sabgil.processor.common.ext.isAssignable
import com.sabgil.processor.common.ext.typeElement
import com.sabgil.processor.common.types.bundleValueHolderPackageName
import com.sabgil.processor.common.types.parcelablePackageName
import com.sabgil.processor.common.types.serializablePackageName
import java.lang.reflect.AnnotatedParameterizedType
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror


class ArgumentsCheckStep : Step<Empty, ArgumentsCheckResult>() {

    override fun process(
        rootElement: Element,
        env: ProcessingEnvironment,
        input: Empty
    ): ArgumentsCheckResult {
        val bundleExtraHolderTypeMirror = env.erasure(
            env.typeElement(bundleValueHolderPackageName).asType()
        )

        val delegateFields = rootElement.enclosedElements
            .filterIsInstance<VariableElement>()
            .filter { it.simpleName.toString().endsWith(DELEGATE_SUFFIX) }
            .filter { it.asType() == bundleExtraHolderTypeMirror }

        delegateFields.forEach {
            val type = it.asType()
            if (type is DeclaredType) {
                val capture = env.typeUtils.capture(type)
                val erasure = env.typeUtils.erasure(type)
                val element = env.typeUtils.asElement(type)
                env.typeUtils.nullType
                println("element: ${env.typeUtils.nullType}")
            }
        }

        val rawFiledNames = delegateFields.map { toFieldName(it) }
        val getterNames = rawFiledNames.map { toGetterName(it) }

        val getters = rootElement.enclosedElements
            .filterIsInstance<ExecutableElement>()
            .filter { getterNames.contains(it.simpleName.toString()) }

        if (!getters.all { env.isSerializableOrParcelable(it.returnType) }) {
            TODO("ArgumentsCheckStep, error report")
        }

        return ArgumentsCheckResult(rawFiledNames.zip(delegateFields.zip(getters)).toMap())
    }

    private fun toFieldName(delegateField: Element) = delegateField
        .simpleName
        .removeSuffix(DELEGATE_SUFFIX)
        .toString()

    private fun toGetterName(rawFieldName: String) = "get" + rawFieldName.capitalize(Locale.ROOT)

    private fun ProcessingEnvironment.isSerializableOrParcelable(type: TypeMirror): Boolean {
        val serializableTypeMirror = typeElement(serializablePackageName).asType()
        val parcelableTypeMirror = typeElement(parcelablePackageName).asType()
        return isAssignable(type, serializableTypeMirror)
                || isAssignable(type, parcelableTypeMirror)
    }

    companion object {
        private const val DELEGATE_SUFFIX = "\$delegate"
    }
}