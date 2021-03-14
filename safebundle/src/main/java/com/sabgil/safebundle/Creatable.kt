package com.sabgil.safebundle

import android.app.Activity
import android.content.Context

interface ContextBasedCreatable : Creatable

interface ActivityBasedCreatable : Creatable

interface Creatable {

    companion object {

        inline fun <reified T : Creatable> parseToImplClass(): Class<*> {
            val packageName = T::class.java.`package`?.name
            val packageLastName = packageName?.split(".")?.last()
            val fullName = T::class.java.canonicalName?.split(".") ?: emptyList()
            val indexOfFullName = if (packageLastName == null) {
                0
            } else {
                fullName.indexOf(packageLastName)
            }

            val simpleName = if (indexOfFullName != -1) {
                fullName.subList(indexOfFullName + 1, fullName.size)
            } else {
                // TODO: add error report
                throw IllegalArgumentException()
            }

            val stringBuilder = StringBuilder()
            if (packageName != null) {
                stringBuilder.append(packageName)
                stringBuilder.append(".")
            }
            stringBuilder.append(simpleName.joinToString("_"))
            stringBuilder.append("_SafeBundleImpl")

            return Class.forName(stringBuilder.toString())
        }
    }
}

inline fun <reified T : ContextBasedCreatable> Context.create() =
    Creatable.parseToImplClass<T>()
        .getDeclaredConstructor(Context::class.java)
        .newInstance(this) as T

inline fun <reified T : ActivityBasedCreatable> Activity.create() =
    Creatable.parseToImplClass<T>()
        .getDeclaredConstructor(Activity::class.java)
        .newInstance(this) as T

inline fun <reified T : Creatable> create() =
    Creatable.parseToImplClass<T>()
        .newInstance() as T