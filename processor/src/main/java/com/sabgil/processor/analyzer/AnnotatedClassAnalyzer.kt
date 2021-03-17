package com.sabgil.processor.analyzer

import com.sabgil.processor.common.ext.*
import com.sabgil.processor.common.model.*
import com.sabgil.processor.common.model.element.KotlinDelegateElement
import com.sabgil.processor.common.model.result.AnnotatedClassAnalyzeResult
import com.squareup.kotlinpoet.metadata.specs.toTypeSpec
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

class AnnotatedClassAnalyzer(private val env: ProcessingEnvironment) {

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
            else -> env.error(
                "SafeBundle annotated class must assignable to activity or fragment",
                annotatedElement
            )
        }
    }

    private fun checkProperties(annotatedElement: TypeElement): Map<String, KotlinDelegateElement> {
        val bundleExtraHolderTypeMirror = env.erasure(
            env.parseToTypeElement(bundleValueHolderClassName).asType()
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
                env.error(
                    "Bundle properties must be Serializable or Parcelable",
                    getter
                )
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

    private fun ProcessingEnvironment.isSerializableOrParcelable(type: TypeMirror) =
        isAssignable(type, serializableClassName) || isAssignable(type, parcelableClassName)

    companion object {
        private const val DELEGATE_SUFFIX = "\$delegate"
    }
}