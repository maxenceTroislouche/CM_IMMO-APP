package com.cm_immo_app.utils.http

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class ImageData(
    val fileB64: String,
)

interface InventoryService {
    @POST("immotepAPI/v1/testimage/")
    fun sendFiles(
        @Body body: ImageData
    ): Call<ResponseBody>
}