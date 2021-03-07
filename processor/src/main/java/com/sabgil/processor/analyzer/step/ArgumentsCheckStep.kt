package com.sabgil.processor.analyzer.step

import com.sabgil.processor.analyzer.model.ArgumentsCheckResult
import com.sabgil.processor.analyzer.model.Parameterizable.Empty
import com.sabgil.processor.ext.isAssignable
import com.sabgil.processor.ext.typeElement
import com.sabgil.processor.types.bundleExtraHolderPackage
import com.sabgil.processor.types.parcelablePackage
import com.sabgil.processor.types.serializablePackage
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror


class ArgumentsCheckStep : Step<Empty, ArgumentsCheckResult>() {

    override fun process(
        rootElement: Element,
        env: ProcessingEnvironment,
        input: Empty
    ): ArgumentsCheckResult {
        val bundleExtraHolderTypeMirror = env.typeElement(bundleExtraHolderPackage).asType()
        val delegateFields = rootElement.enclosedElements
            .filterIsInstance<VariableElement>()
            .filter { it.simpleName.toString().endsWith(DELEGATE_SUFFIX) }
            .filter { it.asType() == bundleExtraHolderTypeMirror }

        val getterNames: List<String> = delegateFields.map { toGetterName(it) }

        val getters = rootElement.enclosedElements
            .filterIsInstance<ExecutableElement>()
            .filter { getterNames.contains(it.simpleName.toString()) }

        if (!getters.all { env.isSerializableOrParcelable(it.asType()) }) {
            // TODO : error report
        }

        delegateFields
            .zip(getters)
            .toMap()

        return ArgumentsCheckResult(delegateFields.zip(getters).toMap())
    }

    private fun toGetterName(delegateField: Element) = "get" + delegateField
        .simpleName
        .removeSuffix(DELEGATE_SUFFIX)
        .toString()
        .capitalize(Locale.ROOT)

    private fun ProcessingEnvironment.isSerializableOrParcelable(type: TypeMirror): Boolean {
        val serializableTypeMirror = typeElement(serializablePackage).asType()
        val parcelableTypeMirror = typeElement(parcelablePackage).asType()
        return isAssignable(type, serializableTypeMirror)
                || isAssignable(type, parcelableTypeMirror)
    }

    companion object {
        private const val DELEGATE_SUFFIX = "\$delegate"
    }
}