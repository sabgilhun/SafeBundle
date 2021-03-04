package com.sabgil.kapt.types

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

/* String */
val tString: TypeName = ClassName("kotlin", "String")

/* Intent */
val tIntent: TypeName = ClassName("android.content", "Intent")

/* Activity */
val tActivity: TypeName = ClassName("android.app", "Activity")

/* Fragment */
val tFragment: TypeName = ClassName("androidx.fragment.app", "Fragment")