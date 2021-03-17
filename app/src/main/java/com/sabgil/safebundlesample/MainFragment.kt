package com.sabgil.safebundlesample

import androidx.fragment.app.Fragment
import com.sabgil.annotation.SafeBundle
import com.sabgil.safebundle.Creatable
import com.sabgil.safebundle.bundle

@SafeBundle(MainFragment.Factory::class)
class MainFragment : Fragment() {
    private val param1 by bundle<String?>()

    private val param2 by bundle<Int>()

    interface Factory : Creatable {
        fun start(param1: String?, param2: Int): Fragment
    }
}