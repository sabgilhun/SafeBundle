package com.sabgil.processor.common.types

import com.sabgil.annotation.Factory
import com.sabgil.annotation.Navigator

const val activityPackageName = "android.app.Activity"

const val serializablePackageName = "java.io.Serializable"

const val parcelablePackageName = "android.os.Parcelable"

const val bundleValueHolderPackageName = "com.sabgil.safebundle.BundleValueHolder"

const val contextBasedNavigatorMarkPackageName = "com.sabgil.safebundle.ContextBasedNavigatorMark"

const val activityBasedNavigatorMarkPackageName = "com.sabgil.safebundle.ActivityBasedNavigatorMark"

const val factoryMarkPackageName = "com.sabgil.safebundle.FactoryMark"

const val contextPackageName = "android.content.Context"

const val intentPackageName = "android.content.Intent"

val navigatorPackageName: String = Navigator::class.java.canonicalName

val factoryPackageName: String = Factory::class.java.canonicalName

val unitPackageName: String = Unit::class.java.canonicalName

val fragmentClassData = "androidx.fragment.app.Fragment"