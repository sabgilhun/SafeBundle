package com.sabgil.safebundlesample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.annotation.ForResult
import com.sabgil.annotation.RequestCode
import com.sabgil.annotation.SafeBundle
import com.sabgil.safebundle.ActivityBasedCreatable
import com.sabgil.safebundle.bundle

@SafeBundle(MainActivity2.Navigator::class)
class MainActivity2 : AppCompatActivity() {

    private val param1 by bundle<String>()

    private val param2 by bundle<String?>()

    abstract class Navigator : ActivityBasedCreatable {

        abstract fun start(param1: String)

        @ForResult
        abstract fun start(@RequestCode a: Any, param1: String, param2: String)

        abstract fun start(param2: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.button).text = param1
    }
}