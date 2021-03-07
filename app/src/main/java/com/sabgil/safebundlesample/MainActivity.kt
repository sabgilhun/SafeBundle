package com.sabgil.safebundlesample

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.annotation.Navigator
import com.sabgil.safebundle.BundleValueHolder
import com.sabgil.safebundle.ContextBasedNavigatorMark
import java.io.Serializable

@Navigator(QQ::class)
class MainActivity : AppCompatActivity() {

    private val test by extraOf<Q>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


}


fun <B> extraOf(): BundleValueHolder<B> {
    return BundleValueHolder {
        @Suppress("UNCHECKED_CAST")
        Any() as B
    }
}

interface QQ : ContextBasedNavigatorMark {
    fun a()
    fun b()
}

abstract class QQA {}

abstract class Q : Serializable {

}

class QSE() : Parcelable, Serializable {

    constructor(parcel: Parcel) : this() {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<QSE> {
        override fun createFromParcel(parcel: Parcel): QSE {
            return QSE(parcel)
        }

        override fun newArray(size: Int): Array<QSE?> {
            return arrayOfNulls(size)
        }
    }

}