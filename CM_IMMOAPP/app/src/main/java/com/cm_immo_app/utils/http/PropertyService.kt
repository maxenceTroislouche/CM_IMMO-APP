package com.cm_immo_app.utils.http

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import java.util.Date

data class PropertiesResponseData(
    val id: Int,
    val nomProprietaire: String,
    val prenomProprietaire: String,
    val typeBien: String,
    val pourcentageAvancement: Float,
    val photos: List<String>,
)

data class ContractData(
    val beginDate: Date,
    val endDate: Date,
    val ownerLastName: String,
    val ownerFirstName: String,
)

data class PropertyDataResponse(
    val id: Int,
    val propertyType: String,
    val city: String,
    val postalCode: Int,
    val progress: Float,
    val inventoryId: Int,
    val isStartingInventory: Boolean,
    val contractId: Int,
    val numberOfRooms: Int,
    val streetNumber: Int,
    val streetName: String,
    val floor: Int,
    val flatNumber: Int,
    val longitude: Float,
    val latitude: Float,
    val description: String,
    val contracts: List<ContractData>,
    val photos: List<String>,
)

interface PropertyService {
    @GET("immotepAPI/v1/properties")
    fun getProperties(@Header("authorization") token: String): Call<List<PropertiesResponseData>>

    @GET("immotepAPI/v1/properties/{id}")
    fun getPropertyData(@Header("authorization") token: String, @Path("id") propertyId: Int): Call<PropertyDataResponse>
}