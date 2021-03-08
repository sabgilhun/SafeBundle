package com.sabgil.safebundlesample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.annotation.Navigator
import com.sabgil.safebundle.BundleValueHolder
import com.sabgil.safebundle.BundleValueHolderNonNull
import com.sabgil.safebundle.ContextBasedNavigatorMark

@Navigator(MainActivity.Navigator::class)
class MainActivity : AppCompatActivity() {

    private val param1 by extraOf<String?>()

    val a: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    interface Navigator : ContextBasedNavigatorMark {
        fun a(param1: String)
    }
}


fun <B> extraOf(): BundleValueHolder<B?> {
    return BundleValueHolder {
        @Suppress("UNCHECKED_CAST")
        Any() as B?
    }
}