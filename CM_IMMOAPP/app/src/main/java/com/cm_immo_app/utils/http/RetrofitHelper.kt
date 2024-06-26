package com.cm_immo_app.utils.http

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


object RetrofitHelper {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofitClient: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .baseUrl("https://api.taffin.ovh")
        .build()

    /*
        private val retrofitClient: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.1.63:3000")
            .build()
        */
    val authService: AuthService = retrofitClient.create(AuthService::class.java)
    val propertyService: PropertyService = retrofitClient.create(PropertyService::class.java)
    val inventoryService: InventoryService = retrofitClient.create(InventoryService::class.java)
    val minuteService: MinuteService = retrofitClient.create(MinuteService::class.java)
    val signService: SignService = retrofitClient.create(SignService::class.java)
}