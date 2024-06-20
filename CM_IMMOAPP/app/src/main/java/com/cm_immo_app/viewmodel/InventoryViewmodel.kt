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
import androidx.compose.runtime.currentCompositionErrors
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm_immo_app.state.InventoryState
import com.cm_immo_app.utils.http.Minute
import com.cm_immo_app.utils.http.MinuteUpdate
import com.cm_immo_app.utils.http.ProgressData
import com.cm_immo_app.utils.http.RetrofitHelper
import com.cm_immo_app.utils.http.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Locale
import kotlin.math.min
import kotlin.time.Duration.Companion.minutes


class InventoryViewmodel : ViewModel() {
    private val _state: MutableState<InventoryState> = mutableStateOf(InventoryState())
    val state: State<InventoryState>
        get() = _state

    private var imageCapture: ImageCapture? = null

    fun setProgress(progress: Int) {
        _state.value = _state.value.copy(progress = progress)
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

    fun saveRoom() {
        if (state.value.currentRoom != null && state.value.currentMinute != null) {
            // On souhaite sauvegarder toutes les minutes de la pièce
            val currentRoom = state.value.currentRoom
            val rooms = state.value.rooms

            rooms[currentRoom!!].minutes.forEachIndexed { index, minute ->
                // On souhaite appeler updateMinute pour chaque minute
                val key = "r$currentRoom/m$index"
                var photos: List<String> = mutableListOf()
                if (state.value.savedPhotos[key] != null) {
                    photos = state.value.savedPhotos[key]?.toList() ?: mutableListOf()
                }

                updateMinute(
                    MinuteUpdate(
                        id_edl = minute.id_edl,
                        id_element = minute.id_element,
                        photos = photos,
                        remark = minute.remark,
                        grade = minute.grade,
                        number = minute.number,
                        elementType = minute.elementType
                    )
                )
            }

            // Recalcule le progress
            val numberOfRooms = rooms.size
            var completedRooms = 0
            rooms.forEach { room ->
                var complete = true
                room.minutes.forEach { minute ->
                    if (minute.grade !in 0..5) {
                        complete = false
                    }
                }
                if (complete) {
                    completedRooms += 1
                }
            }

            val progress = (completedRooms * 100 / numberOfRooms)
            updateProgress(progress)
            setProgress(progress)
        }
    }

    fun setCurrentRoom(roomIndex: Int) {
        saveRoom()
        _state.value = _state.value.copy(currentRoom = roomIndex, currentMinute = 0, savedPhotos = mutableMapOf())
    }

    fun setCurrentMinute(minuteIndex: Int) {
        _state.value = _state.value.copy(currentMinute = minuteIndex)
    }

    fun setGrade(emoji: String) {
        val newGrade = state.value.emojis.indexOf(emoji)
        val currentRoom = state.value.currentRoom
        val currentMinute = state.value.currentMinute

        if (currentRoom != null && currentMinute != null) {
            // Copie immuable de la liste de minutes avec la nouvelle note
            val updatedMinutes = _state.value.rooms[currentRoom].minutes.mapIndexed { index, minute ->
                if (index == currentMinute) {
                    minute.copy(grade = newGrade)
                } else {
                    minute
                }
            }

            // Copie immuable de la liste de rooms avec les minutes mises à jour
            val updatedRooms = _state.value.rooms.mapIndexed { index, room ->
                if (index == currentRoom) {
                    room.copy(minutes = updatedMinutes)
                } else {
                    room
                }
            }

            // Mise à jour de l'état avec les nouvelles rooms
            _state.value = _state.value.copy(rooms = updatedRooms)
        }
    }

    fun setNoteText(text: String) {
        val currentRoom = state.value.currentRoom
        val currentMinute = state.value.currentMinute

        if (currentRoom != null && currentMinute != null) {
            // Copie immuable de la liste de minutes avec la nouvelle note
            val updatedMinutes = _state.value.rooms[currentRoom].minutes.mapIndexed { index, minute ->
                if (index == currentMinute) {
                    minute.copy(remark = text)
                } else {
                    minute
                }
            }

            // Copie immuable de la liste de rooms avec les minutes mises à jour
            val updatedRooms = _state.value.rooms.mapIndexed { index, room ->
                if (index == currentRoom) {
                    room.copy(minutes = updatedMinutes)
                } else {
                    room
                }
            }

            // Mise à jour de l'état avec les nouvelles rooms
            _state.value = _state.value.copy(rooms = updatedRooms)
        }
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
            try {
                val call = RetrofitHelper
                    .minuteService
                    .updateMinute(
                        token = "Bearer ${state.value.token}",
                        minuteToUpdate = minute
                    )

                val response = call.execute()
                if (response.isSuccessful) {
                    Log.i("InventoryViewModel", "Succès !")
                } else {
                    Log.e("InventoryViewModel", "Echec lors de la mise à jour de la minute: $minute")
                }
            } catch (e: Exception) {
                Log.e("InventoryViewmodel", "Exception while updating minute", e)
            }
        }
    }

    fun updateProgress(progress: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val call = RetrofitHelper
                .inventoryService
                .setProgress(
                    inventoryId = state.value.inventoryId,
                    token = "Bearer ${_state.value.token}",
                    body = ProgressData(progress)
                )

            val response = call.execute()
            if (response.isSuccessful) {
                Log.i("InventoryViewModel", "Progrès succès")
            } else {
                Log.e("InventoryViewModel", "Progrès échec")
            }
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
                    if (inventoryData.rooms.isNotEmpty()) {
                        setCurrentRoom(0)
                        setProgress(inventoryData.progress)
                    }
                }
                Log.i("InventoryViewmodel", "Successfully retrieved inventory data")
            } else {
                Log.e("InventoryViewmodel", "Failed to retrieve inventory data: inventoryId: ${state.value.inventoryId} / token: ${state.value.token}")
            }
        }
    }

    fun addPhoto(path: String) {
        val b64 = encodeFileToBase64(path)
        val currentRoom = state.value.currentRoom
        val currentMinute = state.value.currentMinute

        if (currentRoom != null && currentMinute != null) {
            val copyHashMapPhotos = _state.value.savedPhotos.toMutableMap()
            val key = "r$currentRoom/m$currentMinute"
            val listPhotos = copyHashMapPhotos[key]
            if (listPhotos == null) {
                copyHashMapPhotos[key] = mutableListOf()
            }
            if (b64 != null) {
                copyHashMapPhotos[key]?.add(b64)
            }

            _state.value = _state.value.copy(savedPhotos = copyHashMapPhotos)
        }

        Log.i("InventoryViewModel", "photos: ${_state.value.savedPhotos}")
    }
}
