package com.sabgil.processor.common.ext

import com.squareup.kotlinpoet.ClassName
import java.util.*
import kotlin.reflect.KClass

val ClassName.lowerSimpleName get() = simpleName.toLowerCase(Locale.ROOT)

fun Class<*>.toClassName() = ClassName(`package`.name, simpleName)

val KClass<*>.packageName get() = requireNotNull(qualifiedName).removeSuffix(".$simpleName")

fun KClass<*>.toClassName() = ClassName(packageName, requireNotNull(simpleName))