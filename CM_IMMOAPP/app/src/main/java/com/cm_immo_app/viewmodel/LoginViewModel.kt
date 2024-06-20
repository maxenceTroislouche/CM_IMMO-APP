package com.cm_immo_app.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm_immo_app.state.LoginState
import com.cm_immo_app.utils.http.AuthFormData
import com.cm_immo_app.utils.http.AuthTokenResponse
import com.cm_immo_app.utils.http.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel : ViewModel() {

    private val _state: MutableState<LoginState> = mutableStateOf(LoginState())
    val state: State<LoginState>
        get() = _state

    fun setUsername(username: String) {
        Log.i("LoginPage", username)
        _state.value = _state.value.copy(username = username)
    }

    fun setPassword(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun setToken(token: String) {
        _state.value = _state.value.copy(token = token)
    }

    fun setError(error: Boolean) {
        _state.value = _state.value.copy(error = error)
    }

    fun connect() {
        Log.i("Auth", "Connexion ...")
        viewModelScope.launch(Dispatchers.IO) {
            val authFormData: AuthFormData = AuthFormData(
                username = state.value.username,
                password = state.value.password,
            )

            Log.i("Auth", "avant execute")
            val authTokenResponse: Response<AuthTokenResponse> = RetrofitHelper.authService.signin(
                authFormData = authFormData
            ).execute()

            Log.i("Auth", "Envoyé")

            var navigated: Boolean = false
            if (authTokenResponse.isSuccessful) {
                Log.i("Auth", "OK ! ${authTokenResponse.body()}")
                val token = authTokenResponse.body()?.access_token
                if (token != null) {
                    setToken(token)
                    navigated = true
                }
            }
            if (!navigated) {
                Log.e("LoginViewModel", "Echec de la connexion")
                setError(true)
            }
        }
    }
}
