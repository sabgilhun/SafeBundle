package com.sabgil.processor.analyzer

import com.sabgil.processor.common.ext.erasure
import com.sabgil.processor.common.ext.isAssignable
import com.sabgil.processor.common.ext.name
import com.sabgil.processor.common.ext.typeElement
import com.sabgil.processor.common.model.AnnotatedClassAnalyzeResult
import com.sabgil.processor.common.model.InheritanceType
import com.sabgil.processor.common.model.element.KotlinDelegateElement
import com.sabgil.processor.common.types.*
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.toTypeSpec
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

class AnnotatedClassAnalyzer(
    private val env: ProcessingEnvironment
) {

    @KotlinPoetMetadataPreview
    fun analyze(annotatedElement: TypeElement): AnnotatedClassAnalyzeResult {
        val inheritanceType = checkInheritance(annotatedElement)
        val propertiesMap = checkProperties(annotatedElement)

        return AnnotatedClassAnalyzeResult(annotatedElement, inheritanceType, propertiesMap)
    }

    private fun checkInheritance(annotatedElement: TypeElement): InheritanceType {
        val elementTypeMirror = annotatedElement.asType()

        return when {
            env.isAssignable(elementTypeMirror, activityClassName) ->
                InheritanceType.ACTIVITY
            env.isAssignable(elementTypeMirror, fragmentClassName) ->
                InheritanceType.FRAGMENT
            else -> TODO("checkAssignable, error report")
        }
    }

    @KotlinPoetMetadataPreview
    private fun checkProperties(annotatedElement: TypeElement): Map<String, KotlinDelegateElement> {
        val bundleExtraHolderTypeMirror = env.erasure(
            env.typeElement(bundleValueHolderClassName).asType()
        )

        val delegateFields = annotatedElement.enclosedElements
            .filterIsInstance<VariableElement>()
            .filter { it.simpleName.toString().endsWith(DELEGATE_SUFFIX) }
            .filter { it.asType() == bundleExtraHolderTypeMirror }

        val getters = annotatedElement.enclosedElements
            .filterIsInstance<ExecutableElement>()

        val properties = annotatedElement.toTypeSpec()
            .propertySpecs
            .filter { it.delegated }

        val propertiesMap = mutableMapOf<String, KotlinDelegateElement>()
        delegateFields.forEach { field ->
            val rawFieldName = toFieldName(field)
            val property = properties.first { it.name == rawFieldName }
            val getter = getters.first { it.name == toGetterName(rawFieldName) }

            if (!env.isSerializableOrParcelable(getter.returnType)) {
                TODO("ArgumentsCheckStep, error report")
            }

            propertiesMap[rawFieldName] = KotlinDelegateElement(property, field, getter)
        }

        return propertiesMap
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