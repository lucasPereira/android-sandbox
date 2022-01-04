package br.dev.lucas.sandbox

import android.content.Intent
import android.hardware.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import java.io.InputStream
import java.security.KeyStore
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Main"
        SandboxLogger.debug(intent.action)
    }

    fun onReadImageClick(view: android.view.View) {
        val bitmap = resources.getDrawable(R.drawable.otter).toBitmap()
        SandboxLogger.debug(bitmap)
    }

    fun onReadFileClick(view: android.view.View) {
        SandboxLogger.debug("Reading file from resources")
        val inputStream: InputStream = resources.openRawResource(R.raw.document)
        val bytes: ByteArray = inputStream.readBytes()
        SandboxLogger.debug("${bytes.size} bytes")
        inputStream.close()
    }

    fun onReadKeyStoreClick(view: android.view.View) {
        SandboxLogger.debug("Test")
        val store: KeyStore = KeyStore.getInstance("AndroidKeyStore")
        store.load(null)
        val aliases: Enumeration<String> = store.aliases()
        SandboxLogger.debug(aliases.toList().size)
        while (aliases.hasMoreElements()) {
            SandboxLogger.debug(aliases.nextElement())
        }
        val entry1: KeyStore.Entry = store.getEntry("com.csg.federaldev", null)
        val entry2: KeyStore.Entry = store.getEntry("5437d76b-d87a-3e0a-a96b-73087f98becf", null)
        SandboxLogger.debug(entry1)
        SandboxLogger.debug(entry2)
    }

    fun onOpenCameraActivityClick(view: android.view.View) {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

}
