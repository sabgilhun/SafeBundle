package com.sabgil.safebundle

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment

fun <B> Activity.bundle(key: String? = null) =
    BundleValueHolder { propertyName ->
        @Suppress("UNCHECKED_CAST")
        intent.extras?.get(key ?: propertyName) as B
    }

fun <B> Fragment.bundle(key: String? = null) =
    BundleValueHolder { propertyName ->
        @Suppress("UNCHECKED_CAST")
        arguments?.get(key ?: propertyName) as B
    }