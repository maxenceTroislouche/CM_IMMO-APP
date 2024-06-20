package com.cm_immo_app.state

data class LoginState(
    val username: String = "",
    val password: String = "",
    val token: String = "",
    val error: Boolean = false,
)