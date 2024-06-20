package com.cm_immo_app.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm_immo_app.state.SignState
import com.cm_immo_app.utils.http.RetrofitHelper
import com.cm_immo_app.utils.http.SignDataInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Base64

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

    fun setError(error: Boolean) {
        _state.value = _state.value.copy(error = error)
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

    private fun encodeFileToBase64(filePath: String): String? {
        // Lire le fichier en bytes
        val file = File(filePath)

        if (!file.exists()) {
            Log.e("EDLViewmodel", "Le fichier n'existe pas")
            return null
        }

        val fileBytes = file.readBytes()

        // Encoder les bytes en base64
        val base64Encoded = Base64.getEncoder().encodeToString(fileBytes)

        return base64Encoded
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

    fun sendSignature(path: String, type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val image = encodeFileToBase64(path)
            if (image == null) {
                Log.e("SignViewModel", "fichier non trouvé")
                return@launch
            }

            val call = RetrofitHelper.signService.addSignature(
                token = "Bearer ${state.value.token}",
                data = SignDataInput(
                    inventoryId = state.value.inventoryId,
                    type = type,
                    image = image,
                )
            )

            val response = call.execute()

            if (!response.isSuccessful) {
                Log.i("SignViewModel", "${response.code()}")
                Log.i("SignViewModel", "${response.raw()}")
                setError(true)
            } else {
                Log.i("SignViewModel", "Envoi signature OK !")
                Log.i("SignViewModel", "${response.toString()}")
            }
        }
    }
}
