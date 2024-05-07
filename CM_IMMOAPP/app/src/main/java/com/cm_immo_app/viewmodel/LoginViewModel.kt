package com.cm_immo_app.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.cm_immo_app.utils.http.AuthFormData
import com.cm_immo_app.utils.http.AuthService
import com.cm_immo_app.utils.http.AuthTokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginViewModel : ViewModel() {
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    fun onUsernameChanged(newUsername: String) {
        _username.value = newUsername
    }

    /**
     * Logs in the estate agent and returns the token
     */
    fun onLoginClicked(username: String, password: String): String {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.5:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(AuthService::class.java)
        val authFormData = AuthFormData(
            username = username,
            password = password,
        )

        var token = ""

        val response = service.signin(authFormData).enqueue(object : Callback<AuthTokenResponse> {
            override fun onResponse(call: Call<AuthTokenResponse>, response: Response<AuthTokenResponse>) {
                if (response.isSuccessful) {
                    Log.i(TAG, "onResponse: ${response.body()?.access_token}")
                    token = response.body()?.access_token ?: ""
                }
            }

            override fun onFailure(call: Call<AuthTokenResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: Echec lors de la récupération du token", t)
            }
        })
        // Check que la requête a bien réussie
        return token
    }
}
