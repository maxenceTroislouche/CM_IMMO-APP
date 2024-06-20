package com.cm_immo_app.state

import androidx.camera.core.ImageCapture
import com.cm_immo_app.utils.http.Room

data class InventoryState(
    val progress: Float = 0.0f,
    val roomName: String = "",
    val wallImages: List<String> = mutableListOf(),
    val emojis: List<String> = listOf("😡", "😞", "😐",  "🙂", "😄", "🤩"),
    val selectedEmoji: String? = null,
    val token: String = "",
    val inventoryId: Int = -1,
    val imageCapture: ImageCapture? = null,
    val rooms: List<Room> = listOf()
)