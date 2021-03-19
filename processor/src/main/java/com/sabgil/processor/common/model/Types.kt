package com.sabgil.processor.common.model

import com.sabgil.annotation.ForResult
import com.sabgil.annotation.SafeBundle
import com.sabgil.processor.common.ext.toClassName
import com.squareup.kotlinpoet.ClassName
import org.jetbrains.annotations.Nullable

val contextClassName = ClassName("android.content", "Context")

val activityClassName = ClassName("android.app", "Activity")

val fragmentClassName = ClassName("androidx.fragment.app", "Fragment")

val serializableClassName = ClassName("java.io", "Serializable")

val parcelableClassName = ClassName("android.os", "Parcelable")

val safeBundleAnnotationClassName = SafeBundle::class.toClassName()

val forResultAnnotationClassName = ForResult::class.toClassName()

val nullableAnnotationClassName = Nullable::class.toClassName()

val bundleValueHolderClassName = ClassName("com.sabgil.safebundle", "BundleValueHolder")

val creatableClassName = ClassName("com.sabgil.safebundle", "Creatable")

val contextBasedCreatableClassName = ClassName("com.sabgil.safebundle", "ContextBasedCreatable")

val activityBasedCreatableClassName = ClassName("com.sabgil.safebundle", "ActivityBasedCreatable")
