package br.dev.lucas.sandbox

import android.util.Log
import java.lang.Exception

class SandboxLogger {

    companion object {
        fun debug(message: Any?) {
            val tag: String = javaClass.simpleName
            Log.d(tag, message.toString())
        }

        fun debug(message: Any?, exception: Exception) {
            val tag: String = javaClass.simpleName
            Log.d(tag, message.toString(), exception)
        }
    }

}
