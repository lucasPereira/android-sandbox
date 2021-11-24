package br.dev.lucas.sandbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import java.io.InputStream
import java.security.KeyStore
import java.util.*

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

    fun onReadKeyStoreClick(view: android.view.View) {
        log("Test")
        val store: KeyStore = KeyStore.getInstance("AndroidKeyStore")
        store.load(null)
        val aliases: Enumeration<String> = store.aliases()
        log(aliases.toList().size)
        while (aliases.hasMoreElements()) {
            log(aliases.nextElement())
        }
        val entry1: KeyStore.Entry = store.getEntry("com.csg.federaldev", null)
        val entry2: KeyStore.Entry = store.getEntry("5437d76b-d87a-3e0a-a96b-73087f98becf", null)
        log(entry1)
        log(entry2)
    }

}
