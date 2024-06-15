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
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.cm_immo_app.R
import java.text.SimpleDateFormat
import java.util.Locale


class EDLViewModel : ViewModel() {
    var progress: Float = 0.5f
    var roomName: String = "Couloir, Commencer état des lieux"
    var wallImages: List<Int> = listOf(R.drawable.house_boone)
    var emojis: List<String> = listOf("🙂", "😐", "😞", "😡", "😄", "🤩")
    var selectedEmoji: String? = null

    private var imageCapture: ImageCapture? = null

    fun onEmojiSelected(emoji: String) {
        selectedEmoji = emoji
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

    fun capturePhoto(context: Context, onImageCaptured: (Uri) -> Unit) {
        val imageCapture = imageCapture ?: return

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/IMMOTEP")
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
                    Log.d("EDLViewModel", "Photo capture succeeded: $savedUri")
                    onImageCaptured(savedUri)
                }
            }
        )
    }

}

