package com.cm_immo_app.viewmodel

import androidx.lifecycle.ViewModel
import com.cm_immo_app.R

class EDLViewModel : ViewModel() {
    var progress: Float = 0.5f
    var roomName: String = "Couloir, Commencer état des lieux"
    var wallImages: List<Int> = listOf(R.drawable.house_boone)
    var emojis: List<String> = listOf("🙂", "😐", "😞", "😡", "😄", "🤩")
    var selectedEmoji: String? = null

    fun onEmojiSelected(emoji: String) {
        selectedEmoji = emoji

    }
}
