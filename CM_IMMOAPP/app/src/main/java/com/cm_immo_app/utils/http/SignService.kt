package com.cm_immo_app.utils.http

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class SignDataInput(
    val inventoryId: Int,
    val type: String,
    val image: String,
)

interface SignService {
    @POST("immotepAPI/v1/signatures")
    fun addSignature(@Header("authorization") token: String, @Body data: SignDataInput): Call<ResponseBody>
}