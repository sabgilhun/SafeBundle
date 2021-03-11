package com.sabgil.safebundlesample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.annotation.Navigator
import com.sabgil.safebundle.BundleValueHolder
import com.sabgil.safebundle.ContextBasedNavigatorMark

@Navigator(MainActivity.Navigator::class)
class MainActivity : AppCompatActivity() {

    private val param1 by bundleOf<String>()

    private val param2 by bundleOf<Int>()

    val a: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    interface Navigator : ContextBasedNavigatorMark {
        fun start(param1: String, param2: Int)
    }
}

fun <T> bundleOf() = BundleValueHolder {
    @Suppress("UNCHECKED_CAST")
    Any() as T
}