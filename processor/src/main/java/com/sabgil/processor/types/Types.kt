package com.sabgil.processor.types

import com.sabgil.annotation.Factory
import com.sabgil.annotation.Navigator

const val activityPackageName = "android.app.Activity"

const val serializablePackageName = "java.io.Serializable"

const val parcelablePackageName = "android.os.Parcelable"

const val bundleExtraHolderPackageName = "test" // TODO : replace real class name

val navigatorPackageName = Navigator::class.java.canonicalName

val factoryPackageName = Factory::class.java.canonicalName

val fragmentClassData = "androidx.fragment.app.Fragment"