package com.sabgil.safebundlesample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.safebundle.create

class MainActivity : AppCompatActivity() {

    private val navigator = create<MainActivity2.Navigator>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.button).setOnClickListener {
            navigator.start("started from MainActivity1")
        }
    }
}