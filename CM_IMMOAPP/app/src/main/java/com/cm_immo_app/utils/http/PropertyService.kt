package com.cm_immo_app.utils.http

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

data class PropertiesListResponse(
    val id: Int,
    val nomProprietaire: String,
    val prenomProprietaire: String,
    val typeBien: String,
    val photos: List<String>,
    val pourcentageAvancement: Float,
)

interface PropertyService {
    @GET("immotepAPI/v1/properties")
    fun getProperties(@Header("authorization") token: String): Call<List<PropertiesListResponse>>
}