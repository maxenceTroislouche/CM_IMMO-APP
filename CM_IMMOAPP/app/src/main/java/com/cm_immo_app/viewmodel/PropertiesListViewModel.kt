package com.cm_immo_app.viewmodel

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm_immo_app.models.PropertySimple
import com.cm_immo_app.state.PropertiesListState
import com.cm_immo_app.utils.http.PropertiesResponseData
import com.cm_immo_app.utils.http.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class PropertiesListViewModel() : ViewModel() {
    private val _state: MutableState<PropertiesListState> = mutableStateOf(PropertiesListState())
    val state: State<PropertiesListState>
        get() = _state

    /**
     * Setter pour le token car on a besoin d'initialiser l'objet PropertiesListViewModel sans
     * qu'on connaisse le token
     */
    fun setToken(newToken: String) {
        // Mise à jour du token
        _state.value = _state.value.copy(token = newToken)
    }

    fun setError(error: Boolean) {
        _state.value = _state.value.copy(error = error)
    }

    fun getProperties() {
        viewModelScope.launch(Dispatchers.IO) {
            val call: Call<List<PropertiesResponseData>> = RetrofitHelper
                .propertyService
                .getProperties("Bearer ${state.value.token}")


            val response: Response<List<PropertiesResponseData>> = call.execute()
            val newList: MutableList<PropertySimple> = mutableListOf()

            if (response.isSuccessful) {
                response.body()?.forEach {
                    newList.add(
                        PropertySimple(
                            it.id.toString(),
                            "${it.typeBien} ${it.nomProprietaire} ${it.prenomProprietaire}",
                            it.pourcentageAvancement,
                            it.photos[0],
                            it.nomProprietaire
                        )
                    )
                }

                Log.e(TAG, "getProperties: $newList")
                // Copie de la liste pour mettre à jour le state
                _state.value = _state.value.copy(properties = newList.toMutableList())
            } else {
                Log.e(ContentValues.TAG, "PropertiesList: Echec lors de la récupération des biens pour : ${state.value.token} / ${response.errorBody()
                    ?.string()}")
                setError(true)
            }
        }
    }
}