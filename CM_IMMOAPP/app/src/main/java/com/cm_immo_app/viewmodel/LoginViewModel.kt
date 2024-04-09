package com.cm_immo_app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    fun onUsernameChanged(newUsername: String) {
        _username.value = newUsername
    }

    fun onLoginClicked(username: String, password: String) {
        // Actions to be performed on login click
        // For example, show a toast or navigate to another page
    }
}
