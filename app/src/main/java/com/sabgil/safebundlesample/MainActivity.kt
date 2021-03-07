package com.sabgil.safebundlesample

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.annotation.Navigator
import java.io.Serializable
import kotlin.reflect.KProperty

@Navigator(QQ::class)
class MainActivity : AppCompatActivity() {

    private val test by extraOf<QSE>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


}


fun <B> extraOf(): ExtraValueHolder<B> {
    return ExtraValueHolder {
        @Suppress("UNCHECKED_CAST")
        Any() as B
    }
}

interface QQ : Serializable{
    fun a()
    fun b()
}

abstract class QQA {}

class Q: Serializable  {

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
class ExtraValueHolder<V>(
    private var initializer: (String) -> V
) {
    @Volatile
    private var _value: Any? = UninitializedValue
    private val lock = this

    operator fun getValue(thisRef: Any?, property: KProperty<*>): V {
        val v1 = _value
        if (v1 !== UninitializedValue) {
            @Suppress("UNCHECKED_CAST")
            return v1 as V
        }

        return synchronized(lock) {
            val v2 = _value
            if (v2 !== UninitializedValue) {
                @Suppress("UNCHECKED_CAST") (v2 as V)
            } else {
                @Suppress("UNCHECKED_CAST")
                val typedValue = initializer(property.name)
                _value = typedValue
                typedValue
            }
        }
    }
}

private object UninitializedValue