package com.cm_immo_app.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm_immo_app.utils.http.AuthFormData
import com.cm_immo_app.utils.http.AuthService
import com.cm_immo_app.utils.http.AuthTokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginViewModel : ViewModel() {
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    fun onUsernameChanged(newUsername: String) {
        _username.value = newUsername
    }

    private val _token = MutableStateFlow<String?>(null)
    var token: StateFlow<String?> = _token

    suspend fun getAuthToken(username: String, password: String) {
        withContext(Dispatchers.IO) {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.5:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(AuthService::class.java)
            val authFormData = AuthFormData(
                username = username,
                password = password,
            )

            val call: Call<AuthTokenResponse> = service.signin(authFormData)
            val response: Response<AuthTokenResponse> = call.execute()

            if (response.isSuccessful) {
                // Requête réussie!
                _token.value = response.body()?.access_token
            } else {
                Log.e(TAG, "onLoginClicked: Echec lors de la récupération du token {${response.code()}: ${response.message()}}", )
            }
        }
    }
}
