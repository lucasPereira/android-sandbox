package br.dev.lucas.sandbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Main"
        log(intent.action)
    }

    fun onReadImageClick(view: android.view.View) {
        val bitmap = resources.getDrawable(R.drawable.otter).toBitmap()
        log(bitmap)
    }

    fun onReadFileClick(view: android.view.View) {
        log("Reading file from resources")
        val inputStream: InputStream = resources.openRawResource(R.raw.document)
        val bytes: ByteArray = inputStream.readBytes()
        log("${bytes.size} bytes")
        inputStream.close()
    }

}
