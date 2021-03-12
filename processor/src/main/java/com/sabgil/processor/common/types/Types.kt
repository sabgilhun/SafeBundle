package com.sabgil.processor.common.types

import com.sabgil.annotation.SafeBundle
import com.sabgil.processor.common.ext.toClassName
import com.squareup.kotlinpoet.ClassName

const val activityPackageName = "android.app.Activity"

const val serializablePackageName = "java.io.Serializable"

const val parcelablePackageName = "android.os.Parcelable"

const val bundleValueHolderPackageName = "com.sabgil.safebundle.BundleValueHolder"

const val contextBasedNavigatorMarkPackageName = "com.sabgil.safebundle.ContextBasedNavigatorMark"

const val activityBasedNavigatorMarkPackageName = "com.sabgil.safebundle.ActivityBasedNavigatorMark"

const val factoryMarkPackageName = "com.sabgil.safebundle.FactoryMark"

const val contextPackageName = "android.content.Context"

const val intentPackageName = "android.content.Intent"


val unitPackageName: String = Unit::class.java.canonicalName

val fragmentClassData = "androidx.fragment.app.Fragment"


val contextClassName = ClassName("android.content", "Context")

val activityClassName = ClassName("android.app", "Activity")

val fragmentClassName = ClassName("androidx.fragment.app", "Fragment")

val safeBundleAnnotationClassName = SafeBundle::class.java.toClassName()

val bundleValueHolderClassName = ClassName("com.sabgil.safebundle", "BundleValueHolder")