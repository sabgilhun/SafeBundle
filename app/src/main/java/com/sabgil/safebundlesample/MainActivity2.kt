package com.sabgil.safebundlesample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.annotation.ForResult
import com.sabgil.annotation.SafeBundle
import com.sabgil.safebundle.ActivityBasedCreatable
import com.sabgil.safebundle.bundle
import com.sabgil.safebundle.create

@SafeBundle(Navigator::class)
class MainActivity2 : AppCompatActivity(), MainFragment.Listener {

    private val factory = create<MainFragment.Factory>()

    private val param1 by bundle<String>()

    private val param2 by bundle<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.button).apply {
            text = param1
            setOnClickListener {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container, factory.newInstance(param2))
                    .commit()
            }
        }
    }

    override fun finishActivity() {
        setResult(RESULT_OK)
        finish()
    }
}

interface Navigator : ActivityBasedCreatable {

    @ForResult
    fun start(requestCode: Int, param1: String, param2: String)
}