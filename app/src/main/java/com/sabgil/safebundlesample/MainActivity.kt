package com.sabgil.safebundlesample

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.safebundle.create

class MainActivity : AppCompatActivity() {

    private val navigator = create<Navigator>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.button).setOnClickListener {
            navigator.start(1001, "started from MainActivity", "passed from MainActivity")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            Toast.makeText(this, "result", Toast.LENGTH_SHORT).show()
        }
    }
}