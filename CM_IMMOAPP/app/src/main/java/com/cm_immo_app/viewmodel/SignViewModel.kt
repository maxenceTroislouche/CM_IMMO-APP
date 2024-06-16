package com.cm_immo_app.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel

class SignatureViewModel : ViewModel() {
    fun saveSignature(bitmap: Bitmap, context: Context, onSaved: (Uri) -> Unit) {
        val filename = "signature_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Signatures")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri).use { outStream ->
                if (outStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                }
                onSaved(uri)
            }
        }
    }
}
