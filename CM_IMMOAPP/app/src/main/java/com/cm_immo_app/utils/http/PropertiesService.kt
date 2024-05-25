package com.cm_immo_app.utils.http

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header


data class PropertiesResponseData(
    val id: Int,
    val nomProprietaire: String,
    val prenomProprietaire: String,
    val typeBien: String,
    val pourcentageAvancement: Int,
    val photos: List<String>,
)

interface PropertiesService {
    @GET("immotepAPI/v1/biens/")
    fun getProperties(@Header("Authorization") token: String): Call<List<PropertiesResponseData>>
}