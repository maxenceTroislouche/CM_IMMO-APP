package com.cm_immo_app.utils.http

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private val retrofitClient: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.taffin.ovh")
        .build()

    val authService: AuthService = retrofitClient.create(AuthService::class.java)
    val propertyService: PropertyService = retrofitClient.create(PropertyService::class.java)
    val inventoryService: InventoryService = retrofitClient.create(InventoryService::class.java)
    val minuteService: MinuteService = retrofitClient.create(MinuteService::class.java)
}