package com.cm_immo_app.viewmodel

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.cm_immo_app.R
import com.cm_immo_app.models.PropertySimple
import com.cm_immo_app.utils.http.AuthFormData
import com.cm_immo_app.utils.http.AuthService
import com.cm_immo_app.utils.http.AuthTokenResponse
import com.cm_immo_app.utils.http.PropertiesResponseData
import com.cm_immo_app.utils.http.PropertiesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PropertiesListViewModel(var token: String) : ViewModel() {
    private val _properties = MutableStateFlow(mutableListOf<PropertySimple>())
    val properties: StateFlow<MutableList<PropertySimple>> = _properties.asStateFlow()

    suspend fun getProperties() {
        withContext(Dispatchers.IO) {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.5:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(PropertiesService::class.java)
            val call: Call<List<PropertiesResponseData>> = service.getProperties("Bearer $token")
            val response: Response<List<PropertiesResponseData>> = call.execute()

            val newList: MutableList<PropertySimple> = mutableListOf()

            if (response.isSuccessful) {
                // Requête réussie!
                response.body()?.forEach {
                    newList.add(
                        PropertySimple(
                            it.id.toString(),
                        "${it.typeBien} ${it.nomProprietaire} ${it.prenomProprietaire}",
                            it.pourcentageAvancement,
                            it.photos.get(0),
                            it.nomProprietaire
                        )
                    )
                }
                Log.e(TAG, "getProperties: $newList")
                _properties.value = newList.toMutableList()
            } else {
                Log.e(ContentValues.TAG, "PropertiesList: Echec lors de la récupération du token {${response.code()}: ${response.message()}}", )
            }
        }
    }
}