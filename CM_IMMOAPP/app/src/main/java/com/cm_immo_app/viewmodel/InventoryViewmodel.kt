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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm_immo_app.state.InventoryState
import com.cm_immo_app.utils.http.ImageData
import com.cm_immo_app.utils.http.MinuteUpdate
import com.cm_immo_app.utils.http.RetrofitHelper
import com.cm_immo_app.utils.http.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Locale

class InventoryViewmodel : ViewModel() {
    private val _state: MutableState<InventoryState> = mutableStateOf(InventoryState())
    val state: State<InventoryState>
        get() = _state

    private var imageCapture: ImageCapture? = null

    fun setProgress(progress: Float) {
        _state.value = _state.value.copy(progress = progress)
    }

    fun setRoomName(roomName: String) {
        _state.value = _state.value.copy(roomName = roomName)
    }

    fun setWallImages(wallImages: List<String>) {
        _state.value = _state.value.copy(wallImages = wallImages)
    }

    fun setSelectedEmoji(selectedEmoji: String) {
        _state.value = _state.value.copy(selectedEmoji = selectedEmoji)
    }

    fun setToken(token: String) {
        _state.value = _state.value.copy(token = token)
    }

    fun setInventoryId(inventoryId: Int) {
        _state.value = _state.value.copy(inventoryId = inventoryId)
    }

    fun updateRooms(rooms: List<Room>) {
        _state.value = _state.value.copy(rooms = rooms)
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
                Log.e("InventoryViewmodel", "Camera use case binding failed", exc)
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
                    Log.e("InventoryViewmodel", "Photo capture failed: ${exc.message}", exc)
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
        val file = File(filePath)

        if (!file.exists()) {
            Log.e("InventoryViewmodel", "File does not exist")
            return null
        }

        val fileBytes = file.readBytes()
        return Base64.getEncoder().encodeToString(fileBytes)
    }

    fun updateMinute(minute: MinuteUpdate) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("InventoryViewmodel", "$minute")
        }
    }

    fun getInventoryData() {
        viewModelScope.launch(Dispatchers.IO) {
            val call = RetrofitHelper
                .inventoryService
                .getInventoryData(state.value.inventoryId, "Bearer ${state.value.token}")

            val response = call.execute()
            if (response.isSuccessful) {
                response.body()?.let { inventoryData ->
                    updateRooms(inventoryData.rooms)
                }
                Log.i("InventoryViewmodel", "Successfully retrieved inventory data")
            } else {
                Log.e("InventoryViewmodel", "Failed to retrieve inventory data: inventoryId: ${state.value.inventoryId} / token: ${state.value.token}")
            }
        }
    }
    fun setCurrentRoom(roomName: String) {
        _state.value = _state.value.copy(roomName = roomName)
    }
}
