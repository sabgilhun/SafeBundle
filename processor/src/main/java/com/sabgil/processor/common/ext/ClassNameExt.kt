package com.sabgil.processor.common.ext

import com.squareup.kotlinpoet.ClassName
import java.util.*

val ClassName.lowerSimpleName get() = simpleName.toLowerCase(Locale.ROOT)

fun Class<*>.toClassName() = ClassName(`package`.name, simpleName)