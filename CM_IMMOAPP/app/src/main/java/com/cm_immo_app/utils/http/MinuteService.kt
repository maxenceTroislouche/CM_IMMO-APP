package com.cm_immo_app.utils.http

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH

data class MinuteUpdate(
    val id_edl: Int,
    val id_element: Int,
    val photos: List<String>,
    val remark: String,
    val grade: Int,
    val number: Int,
    val elementType: String
)

interface MinuteService {
    @PATCH("immotepAPI/v1/minutes/")
    fun updateMinute(@Header("Authorization") token: String, @Body minuteToUpdate: MinuteUpdate)
}