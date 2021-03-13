package com.sabgil.safebundlesample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.annotation.ForResult
import com.sabgil.annotation.RequestCode
import com.sabgil.annotation.SafeBundle
import com.sabgil.safebundle.bundle

@SafeBundle(MainActivity.Navigator::class)
class MainActivity : AppCompatActivity() {

    private val param1 by bundle<String>()

    private val param2 by bundle<Int>()

    val a: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    interface Navigator {

        @ForResult
        fun start(@RequestCode requestCode: Int, param1: String, param2: Int)
    }
}