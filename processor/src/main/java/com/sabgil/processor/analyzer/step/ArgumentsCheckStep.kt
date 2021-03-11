package com.sabgil.processor.analyzer.step

import com.sabgil.processor.analyzer.result.ArgumentsCheckResult
import com.sabgil.processor.common.Parameterizable.Empty
import com.sabgil.processor.common.Step
import com.sabgil.processor.common.ext.erasure
import com.sabgil.processor.common.ext.isAssignable
import com.sabgil.processor.common.ext.name
import com.sabgil.processor.common.ext.typeElement
import com.sabgil.processor.common.model.kelement.KotlinDelegateElement
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

        val getters = rootElement.enclosedElements
            .filterIsInstance<ExecutableElement>()

        val properties = (rootElement as TypeElement).toTypeSpec()
            .propertySpecs
            .filter { it.delegated }

        val argumentsMap = mutableMapOf<String, KotlinDelegateElement>()
        delegateFields.forEach { field ->
            val rawFieldName = toFieldName(field)

            val property = properties.first { it.name == rawFieldName }

            val getter = getters.first { it.name == toGetterName(rawFieldName) }

            if (!env.isSerializableOrParcelable(getter.returnType)) {
                TODO("ArgumentsCheckStep, error report")
            }

            argumentsMap[rawFieldName] = KotlinDelegateElement(
                property,
                field,
                getter
            )
        }

        return ArgumentsCheckResult(argumentsMap)
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