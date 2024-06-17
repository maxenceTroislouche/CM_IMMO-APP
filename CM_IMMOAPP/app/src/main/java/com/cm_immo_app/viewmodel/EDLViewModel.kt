package com.cm_immo_app.viewmodel

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm_immo_app.R
import com.cm_immo_app.utils.http.ImageData
import com.cm_immo_app.utils.http.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Locale
import kotlin.system.exitProcess


class EDLViewModel : ViewModel() {
    var progress: Float = 0.5f
    var roomName: String = "Couloir, Commencer état des lieux"
    var wallImages: List<Int> = listOf(R.drawable.house_boone)
    var emojis: List<String> = listOf("🙂", "😐", "😞", "😡", "😄", "🤩")
    var selectedEmoji by mutableStateOf<String?>(null)

    private var imageCapture: ImageCapture? = null

    fun onEmojiSelected(emoji: String) {
        selectedEmoji = emoji
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

    fun startCamera(context: Context, lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e("EDLViewModel", "Camera use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun capturePhoto(context: Context, onImageCaptured: (String?) -> Unit) {
        val imageCapture = imageCapture ?: return

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/immotep")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("EDLViewModel", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.EMPTY
                    val path = getRealPathFromURI(context, savedUri)
                    onImageCaptured(path)
                }
            }
        )
    }

    fun encodeFileToBase64(filePath: String): String? {
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

    fun sendPhoto(path: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (path == null) {
                exitProcess(-1)
            }
            Log.i("EDLViewmodel", "$path")
            val b64 = encodeFileToBase64(path)
            if (b64 == null) {
                Log.e("EDLViewmodel", "Impossible d'obtenir le b64")
                exitProcess(-1)
            }

            val call = RetrofitHelper
                .inventoryService
                .sendFiles(ImageData(b64))

            val response = call.execute()
            Log.i("EDLViewmodel", "$response")
        }
    }
}

