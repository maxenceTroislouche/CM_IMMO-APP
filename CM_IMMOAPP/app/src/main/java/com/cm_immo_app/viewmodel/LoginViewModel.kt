package com.cm_immo_app.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm_immo_app.state.LoginState
import com.cm_immo_app.utils.http.AuthFormData
import com.cm_immo_app.utils.http.AuthTokenResponse
import com.cm_immo_app.utils.http.RetrofitHelper
import kotlinx.coroutines.launch
import retrofit2.await

class LoginViewModel : ViewModel() {

    private val _state: MutableState<LoginState> = mutableStateOf(LoginState())
    val state: State<LoginState>
        get() = _state

    fun setUsername(username: String) {
        _state.value = _state.value.copy(username = username)
    }

    fun setPassword(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun setToken(token: String) {
        _state.value = _state.value.copy(token = token)
    }

    fun connect() {
        viewModelScope.launch {
            val authFormData: AuthFormData = AuthFormData(
                username = state.value.username,
                password = state.value.password,
            )
            val authTokenResponse: AuthTokenResponse = RetrofitHelper.authService.signin(
                authFormData = authFormData
            ).await()

            setToken(authTokenResponse.access_token)
        }
    }
}
