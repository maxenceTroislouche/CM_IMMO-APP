package com.cm_immo_app.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.cm_immo_app.state.SignState

class SignViewModel : ViewModel() {
    private val _state: MutableState<SignState> = mutableStateOf(SignState())
    val state: State<SignState>
        get() = _state

    fun setToken(token: String) {
        _state.value = _state.value.copy(token = token)
    }

    fun setType(type: String) {
        _state.value = _state.value.copy(type = type)
    }

    fun setInventoryId(inventoryId: Int) {
        _state.value = _state.value.copy(inventoryId = inventoryId)
    }

    private fun getRealPathFromURI(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                it.getString(columnIndex)
            } else null
        }
    }

    fun saveSignature(bitmap: Bitmap, context: Context, onSaved: (String?) -> Unit) {
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
                val path = getRealPathFromURI(context, uri)
                onSaved(path)
            }
        }
    }
}
