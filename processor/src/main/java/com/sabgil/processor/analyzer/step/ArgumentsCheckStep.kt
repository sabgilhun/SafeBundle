package com.sabgil.processor.analyzer.step

import com.sabgil.processor.analyzer.result.ArgumentsCheckResult
import com.sabgil.processor.common.Parameterizable.Empty
import com.sabgil.processor.common.Step
import com.sabgil.processor.common.ext.erasure
import com.sabgil.processor.common.ext.isAssignable
import com.sabgil.processor.common.ext.typeElement
import com.sabgil.processor.common.model.DelegateElement
import com.sabgil.processor.common.types.bundleValueHolderPackageName
import com.sabgil.processor.common.types.parcelablePackageName
import com.sabgil.processor.common.types.serializablePackageName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.toTypeSpec
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror


class ArgumentsCheckStep : Step<Empty, ArgumentsCheckResult>() {

    @KotlinPoetMetadataPreview
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

        val rawFieldNames = delegateFields.map { toFieldName(it) }
        val getterNames = rawFieldNames.map { toGetterName(it) }

        val getters = rootElement.enclosedElements
            .filterIsInstance<ExecutableElement>()
            .filter { getterNames.contains(it.simpleName.toString()) }

        val properties = (rootElement as TypeElement).toTypeSpec()
            .propertySpecs
            .filter { it.delegated }

        if (!getters.all { env.isSerializableOrParcelable(it.returnType) }) {
            TODO("ArgumentsCheckStep, error report")
        }

        if (delegateFields.size != properties.size) {
            TODO("ArgumentsCheckStep, error report")
        }

        val delegateElements = delegateFields.indices.map {
            DelegateElement(
                variable = delegateFields[it],
                getter = getters[it],
                type = getters[it].returnType.toString(),
                isNullable = properties[it].type.isNullable
            )
        }

        delegateElements.forEach {
            println(it)
        }

        return ArgumentsCheckResult(rawFieldNames.zip(delegateElements).toMap())
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