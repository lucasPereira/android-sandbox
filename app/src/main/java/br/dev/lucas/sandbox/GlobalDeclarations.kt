package br.dev.lucas.sandbox

import android.util.Log
import java.lang.Exception

fun Any.log(message: Any?) {
    val tag: String = javaClass.simpleName
    Log.d(tag, message.toString())
}

fun Any.log(message: Any?, exception: Exception) {
    val tag: String = javaClass.simpleName
    Log.d(tag, message.toString(), exception)
}
