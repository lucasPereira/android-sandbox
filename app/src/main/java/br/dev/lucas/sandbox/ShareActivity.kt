package br.dev.lucas.sandbox

import android.content.ContentProvider
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ShareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        title = "Share"
        log(intent.action)
        logIntent()
        processIntent()
    }

    private fun logIntent() {
        log(intent.action)
        log(intent.type)
        log(intent.data)
        log(intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))
    }

    private fun processIntent() {
        setUri("")
        setType("")
        setContent("")
        if (intent.action == Intent.ACTION_SEND) {
            val uri: Uri = intent.getParcelableExtra(Intent.EXTRA_STREAM) ?: return
            setUri(uri.toString())
            setType(intent.type.toString())
            if (intent.type == "text/plain") {
                val content = readTextContent(uri)
                setContent(content.toString())
            }
        }
    }

    private fun readTextContent(uri: Uri): String? {
        try {
            contentResolver.openInputStream(uri).use { inputStream ->
                return inputStream?.bufferedReader()?.readText()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            return null
        }
    }

    private fun setUri(value: String) {
        findViewById<TextView>(R.id.sandbox_share_uri).text = value
    }

    private fun setType(value: String) {
        findViewById<TextView>(R.id.sandbox_share_type).text = value
    }

    private fun setContent(value: String) {
        findViewById<TextView>(R.id.sandbox_share_content).text = value
    }

}
