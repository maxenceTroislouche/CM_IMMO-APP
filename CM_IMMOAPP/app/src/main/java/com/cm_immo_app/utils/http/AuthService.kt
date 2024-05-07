package com.cm_immo_app.utils.http

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// User auth model
data class AuthFormData(val username: String, val password: String)

// Token response
data class AuthTokenResponse(val access_token: String)

// Interface définissant les endpoints pour l'authentification
interface AuthService {
    @POST("immotepAPI/v1/auth/signin")
    fun signin(@Body authFormData: AuthFormData): Call<AuthTokenResponse>
}