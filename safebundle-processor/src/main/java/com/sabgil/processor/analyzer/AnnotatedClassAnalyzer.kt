package com.sabgil.processor.analyzer

import com.sabgil.processor.common.ext.*
import com.sabgil.processor.common.model.*
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

        return AnnotatedClassAnalyzeResult(
            AnnotatedClass(annotatedElement, inheritanceType),
            propertiesMap
        )
    }

    private fun checkInheritance(annotatedElement: TypeElement): InheritanceType {
        val elementTypeMirror = annotatedElement.asType()

        return when {
            env.isAssignable(elementTypeMirror, activityClassName) ->
                InheritanceType.ACTIVITY
            env.isAssignable(elementTypeMirror, fragmentClassName) ->
                InheritanceType.FRAGMENT
            else -> reportInheritanceError(annotatedElement)
        }
    }

    private fun checkProperties(annotatedElement: TypeElement): Map<String, Property> {
        val delegateFields = filterDelegateProperties(annotatedElement)
        val getters = filterGetters(annotatedElement)
        val properties = filterKotlinDelegateProperties(annotatedElement)

        val propertiesMap = mutableMapOf<String, Property>()

        delegateFields.forEach { field ->
            val rawFieldName = toFieldName(field)
            val property = properties.first { it.name == rawFieldName }
            val getter = getters.first { it.name == toGetterName(rawFieldName) }

            if (!env.isSerializableOrParcelable(getter.returnType)) {
                reportPropertyTypeError(getter)
            }
            propertiesMap[rawFieldName] = Property(property, field, getter)
        }

        return propertiesMap
    }

    private fun filterDelegateProperties(element: TypeElement): List<VariableElement> {
        val bundleExtraHolderTypeMirror =
            env.erasure(env.parseToTypeElement(bundleValueHolderClassName).asType())

        return element.enclosedElements
            .filterIsInstance<VariableElement>()
            .filter { it.simpleName.toString().endsWith(DELEGATE_SUFFIX) }
            .filter { it.asType() == bundleExtraHolderTypeMirror }
    }

    private fun filterGetters(
        element: TypeElement
    ) = element.enclosedElements.filterIsInstance<ExecutableElement>()

    private fun filterKotlinDelegateProperties(
        element: TypeElement
    ) = element.toTypeSpec()
        .propertySpecs
        .filter {
            it.delegated
        }

    private fun toFieldName(delegateField: Element) = delegateField
        .simpleName
        .removeSuffix(DELEGATE_SUFFIX)
        .toString()

    private fun toGetterName(rawFieldName: String) = "get" + rawFieldName.capitalize(Locale.ROOT)

    private fun ProcessingEnvironment.isSerializableOrParcelable(type: TypeMirror) =
        isAssignable(type, serializableClassName) || isAssignable(type, parcelableClassName)

    private fun reportInheritanceError(element: Element): Nothing {
        env.error("SafeBundle annotated class must assignable to activity or fragment", element)
    }

    private fun reportPropertyTypeError(element: Element) {
        env.error("Bundle properties must be Serializable or Parcelable", element)
    }

    companion object {
        private const val DELEGATE_SUFFIX = "\$delegate"
    }
}