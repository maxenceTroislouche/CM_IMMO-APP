package com.cm_immo_app.utils.http

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

data class ImageData(
    val fileB64: String,
)

data class Person(
    val id: Int,
    val lastname: String,
    val firstname: String,
)
data class Contract(
    val id: Int,
    val beginDate: String,
    val endDate: String,
)
data class Minute(
    val id_edl: Int,
    val id_element: Int,
    val photos: List<String>,
    val remark: String,
    val grade: Int,
    val number: Int,
    val elementType: String,
)
data class Room(
    val id: Int,
    val number: Int,
    val description: String,
    val area: Int,
    val minutes: List<Minute>,
)
data class Property(
    val id: Int,
)

data class InventoryData(
    val id: Int,
    val isStartingInventory: Boolean,
    val date: String,
    val progress: Int,
    val contract: Contract,
    val renter: Person,
    val owner: Person,
    val property: Property,
    val rooms: List<Room>,
)

data class ProgressData(
    val progress: Int,
)

interface InventoryService {
    @GET("immotepAPI/v1/inventories/{id}")
    fun getInventoryData(
        @Path("id") inventoryId: Int,
        @Header("Authorization") token: String
    ): Call<InventoryData>

    @PATCH("immotepAPI/v1/inventories/{id}")
    fun setProgress(
        @Path("id") inventoryId: Int,
        @Header("Authorization") token: String,
        @Body body: ProgressData,
    ): Call<ResponseBody>
}