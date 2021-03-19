package com.sabgil.safebundlesample

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sabgil.annotation.SafeBundle
import com.sabgil.safebundle.Creatable
import com.sabgil.safebundle.bundle

@SafeBundle(MainFragment.Factory::class)
class MainFragment : Fragment() {

    private val param1 by bundle<String>()

    private var listener : Listener?  =null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Listener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment, container, false)
        v.findViewById<TextView>(R.id.text).apply {
            text = param1
            setOnClickListener {
                listener?.finishActivity()
            }
        }
        return v
    }

    interface Listener {
        fun finishActivity()
    }

    interface Factory : Creatable {
        fun newInstance(param1: String): Fragment
    }
}