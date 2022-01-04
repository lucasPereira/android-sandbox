package br.dev.lucas.sandbox

import android.Manifest
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class ShareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        title = "Share"
        SandboxLogger.debug(intent.action)
        logIntent()
        processIntent()
    }

    private fun logIntent() {
        SandboxLogger.debug("--- logIntent ---")
        SandboxLogger.debug(intent.action)
        SandboxLogger.debug(intent.type)
        SandboxLogger.debug(intent.data)
        SandboxLogger.debug(intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))
    }

    private fun processIntent() {
        setUri("")
        setType("")
        setContent("")
        if (intent.action == Intent.ACTION_SEND) {
            val uri: Uri = intent.getParcelableExtra(Intent.EXTRA_STREAM) ?: return
            setUri(uri.toString())
            setType(intent.type.toString())
            isFromContacts(uri)
            queryContent(uri)
            createAndCompareWithUriFromLocalFile(uri)
            if (intent.type == "text/plain") {
                val content = readTextContent(uri)
                setContent(content.toString())
            }
        }
    }

    private fun queryContent(uri: Uri) {
        SandboxLogger.debug(" --- queryContent: $uri ---")
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    cursor.columnNames.forEach { name ->
                        val index = cursor.getColumnIndex(name)
                        if (cursor.getType(index) != Cursor.FIELD_TYPE_BLOB) {
                            val value = cursor.getString(index)
                            SandboxLogger.debug("content[$index] = $name: $value")
                        }
                    }
                }
            } else {
                SandboxLogger.debug("there is any row to read")
            }
        }
    }

    private fun isFromContacts(uri: Uri) {
        SandboxLogger.debug(" --- isFromContacts: $uri ---")
        SandboxLogger.debug("uri authority: ${uri.authority}")
        SandboxLogger.debug("contacts authority: ${ContactsContract.AUTHORITY}")
        SandboxLogger.debug("contacts authority uri: ${ContactsContract.AUTHORITY_URI}")
        SandboxLogger.debug("contacts content uri: ${ContactsContract.Contacts.CONTENT_URI}")
        SandboxLogger.debug("contacts vcard uri: ${ContactsContract.Contacts.CONTENT_VCARD_URI}")
        SandboxLogger.debug("contacts multi vcard uri: ${ContactsContract.Contacts.CONTENT_MULTI_VCARD_URI}")
        SandboxLogger.debug("result: ${ContactsContract.AUTHORITY == uri.authority}")
        SandboxLogger.debug("has contact permission: ${applicationContext.checkSelfPermission(Manifest.permission.READ_CONTACTS)}")
    }

    private fun createAndCompareWithUriFromLocalFile(originalUri: Uri) {
        SandboxLogger.debug(" --- createAndCompareWithUriFromLocalFile: $originalUri ---")
        val name = getNameFromUri(originalUri)
        val file = generateLocalFile(originalUri, name)
        if (file != null) {
            val localFileUri = Uri.fromFile(file)
            SandboxLogger.debug("local file: ${file.absolutePath}")
            SandboxLogger.debug("original uri: $originalUri")
            SandboxLogger.debug("local file uri: $localFileUri")
            SandboxLogger.debug("type from content resolver of original uri: ${contentResolver.getType(originalUri)}")
            SandboxLogger.debug("type from content resolver of local file uri: ${contentResolver.getType(localFileUri)}")
            SandboxLogger.debug("type from mime map of original uri path: ${MimeTypeMap.getFileExtensionFromUrl(originalUri.path)}")
            SandboxLogger.debug("type from mime map of local file uri path: ${MimeTypeMap.getFileExtensionFromUrl(localFileUri.path)}")
            SandboxLogger.debug("extension from mime map of original uri encoded path: ${MimeTypeMap.getFileExtensionFromUrl(originalUri.encodedPath)}")
            SandboxLogger.debug("extension from mime map of local file uri encoded path: ${MimeTypeMap.getFileExtensionFromUrl(localFileUri.encodedPath)}")
            SandboxLogger.debug("type intent: ${intent.type}")
            SandboxLogger.debug("original uri authority: ${originalUri.authority}")
            SandboxLogger.debug("original uri scheme: ${originalUri.scheme}")
            SandboxLogger.debug("local file uri authority: ${localFileUri.authority}")
            SandboxLogger.debug("local file uri scheme: ${localFileUri.scheme}")
            queryContent(localFileUri)
        }
    }

    private fun generateLocalFile(originalUri: Uri, name: String): File? {
        try {
            contentResolver.openInputStream(originalUri).use { inputStream ->
                val file = File(filesDir, name)
                file.createNewFile()
                file.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
                return file
            }
        } catch (exception: Exception) {
            SandboxLogger.debug("could not create local file", exception)
            return null
        }
    }

    private fun getNameFromUri(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            if (cursor.moveToNext()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                return cursor.getString(nameIndex)
            }
        }
        return System.currentTimeMillis().toString()
    }

    private fun readTextContent(uri: Uri): String? {
        try {
            contentResolver.openInputStream(uri).use { inputStream ->
                return inputStream?.bufferedReader()?.readText()
            }
        } catch (exception: Exception) {
            SandboxLogger.debug("could not read text content from file", exception)
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
