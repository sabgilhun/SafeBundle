package com.sabgil.processor.common.model

import com.sabgil.annotation.SafeBundle
import com.sabgil.processor.common.ext.toClassName
import com.squareup.kotlinpoet.ClassName

val contextClassName = ClassName("android.content", "Context")

val activityClassName = ClassName("android.app", "Activity")

val fragmentClassName = ClassName("androidx.fragment.app", "Fragment")

val serializableClassName = ClassName("java.io", "Serializable")

val parcelableClassName = ClassName("android.os", "Parcelable")

val safeBundleAnnotationClassName = SafeBundle::class.java.toClassName()

val bundleValueHolderClassName = ClassName("com.sabgil.safebundle", "BundleValueHolder")