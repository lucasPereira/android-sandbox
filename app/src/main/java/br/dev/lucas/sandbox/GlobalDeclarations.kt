package br.dev.lucas.sandbox

import android.util.Log

fun Any.log(message: Any?) {
    val tag: String = javaClass.simpleName
    Log.d(tag, message.toString())
}
