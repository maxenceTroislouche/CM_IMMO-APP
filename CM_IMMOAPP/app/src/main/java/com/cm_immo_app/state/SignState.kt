package com.cm_immo_app.state

data class SignState (
    val token: String = "",
    val type: String = "",
    val inventoryId: Int = -1,
    val error: Boolean = false,
)