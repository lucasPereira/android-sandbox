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
        log(intent.action)
        logIntent()
        processIntent()
    }

    private fun logIntent() {
        log("--- logIntent ---")
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
        log(" --- queryContent: $uri ---")
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    cursor.columnNames.forEach { name ->
                        val index = cursor.getColumnIndex(name)
                        if (cursor.getType(index) != Cursor.FIELD_TYPE_BLOB) {
                            val value = cursor.getString(index)
                            log("content[$index] = $name: $value")
                        }
                    }
                }
            } else {
                log("there is any row to read")
            }
        }
    }

    private fun isFromContacts(uri: Uri) {
        log(" --- isFromContacts: $uri ---")
        log("uri authority: ${uri.authority}")
        log("contacts authority: ${ContactsContract.AUTHORITY}")
        log("contacts authority uri: ${ContactsContract.AUTHORITY_URI}")
        log("contacts content uri: ${ContactsContract.Contacts.CONTENT_URI}")
        log("contacts vcard uri: ${ContactsContract.Contacts.CONTENT_VCARD_URI}")
        log("contacts multi vcard uri: ${ContactsContract.Contacts.CONTENT_MULTI_VCARD_URI}")
        log("result: ${ContactsContract.AUTHORITY == uri.authority}")
        log("has contact permission: ${applicationContext.checkSelfPermission(Manifest.permission.READ_CONTACTS)}")
    }

    private fun createAndCompareWithUriFromLocalFile(originalUri: Uri) {
        log(" --- createAndCompareWithUriFromLocalFile: $originalUri ---")
        val name = getNameFromUri(originalUri)
        val file = generateLocalFile(originalUri, name)
        if (file != null) {
            val localFileUri = Uri.fromFile(file)
            log("local file: ${file.absolutePath}")
            log("original uri: $originalUri")
            log("local file uri: $localFileUri")
            log("type from content resolver of original uri: ${contentResolver.getType(originalUri)}")
            log("type from content resolver of local file uri: ${contentResolver.getType(localFileUri)}")
            log("type from mime map of original uri path: ${MimeTypeMap.getFileExtensionFromUrl(originalUri.path)}")
            log("type from mime map of local file uri path: ${MimeTypeMap.getFileExtensionFromUrl(localFileUri.path)}")
            log("extension from mime map of original uri encoded path: ${MimeTypeMap.getFileExtensionFromUrl(originalUri.encodedPath)}")
            log("extension from mime map of local file uri encoded path: ${MimeTypeMap.getFileExtensionFromUrl(localFileUri.encodedPath)}")
            log("type intent: ${intent.type}")
            log("original uri authority: ${originalUri.authority}")
            log("original uri scheme: ${originalUri.scheme}")
            log("local file uri authority: ${localFileUri.authority}")
            log("local file uri scheme: ${localFileUri.scheme}")
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
            log("could not create local file", exception)
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
            log("could not read text content from file", exception)
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
