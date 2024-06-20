package com.cm_immo_app.state

import androidx.camera.core.ImageCapture
import com.cm_immo_app.utils.http.Minute
import com.cm_immo_app.utils.http.Room

data class InventoryState(
    val progress: Int = 0,
    val emojis: List<String> = listOf("😡", "😞", "😐",  "🙂", "😄", "🤩"),
    val token: String = "",
    val inventoryId: Int = -1,
    val imageCapture: ImageCapture? = null,
    val rooms: List<Room> = listOf(),
    val currentRoom: Int? = null, // Index dans rooms
    val currentMinute: Int? = null, // Index dans currentRoom
    val savedPhotos: MutableMap<String, MutableList<String>> = hashMapOf(),
    )