package com.cm_immo_app.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm_immo_app.models.Contract
import com.cm_immo_app.models.PropertyDetails
import com.cm_immo_app.state.PropertyState
import com.cm_immo_app.utils.http.PropertyDataResponse
import com.cm_immo_app.utils.http.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

/*
PropertyDetails(
id = idProperty,
propertyType = "Maison",
city = "Lens",
postalCode = "62300",
progressPercentage = 69.42f,
reviewId = "1",
reviewType = "Entrée",
contractId = "1",
numberOfRooms = 4,
streetNumber = 12,
streetName = "Jean Souvraz",
longitude = 5.2131342f,
latitude = 3.42142f,
description = "Bien de qualité qualitative",
contracts = listOf(
Contract(
dateBeginning = Date(),
dateEnd = Date(),
OwnerLastName = "Troislouche",
OwnerFirstName = "Maxence",
)
),
photos = listOf(),
)*/


class PropertyViewModel() : ViewModel() {
    private val _state: MutableState<PropertyState> = mutableStateOf(PropertyState())
    val state: State<PropertyState>
        get() = _state

    fun setToken(newToken: String) {
        _state.value = _state.value.copy(token = newToken)
    }

    fun setPropertyId(propertyId: Int) {
        _state.value = _state.value.copy(propertyId = propertyId)
    }

    fun getPropertyData() {
        viewModelScope.launch(Dispatchers.IO) {
            val call: Call<PropertyDataResponse> = RetrofitHelper
                .propertyService
                .getPropertyData("Bearer ${state.value.token}", state.value.propertyId)

            val response: Response<PropertyDataResponse> = call.execute()
            // TODO: Compléter pour reviewType
            if (response.isSuccessful) {

                var reviewType: String = "SORTANT"
                if (response.body()?.isStartingInventory == true) {
                    reviewType = "ENTRANT"
                }

                val contractsData = response.body()?.contracts
                val contracts = mutableListOf<Contract>()
                if (contractsData != null) {
                    for (contract in contractsData) {
                        contracts.add(
                            Contract(
                                dateBeginning = contract.beginDate,
                                dateEnd = contract.endDate,
                                OwnerLastName = contract.ownerLastName,
                                OwnerFirstName = contract.ownerFirstName,
                            )
                        )
                    }
                }

                _state.value = _state.value.copy(property = PropertyDetails(
                    id = response.body()?.id ?: -1,
                    propertyType = response.body()?.propertyType ?: "",
                    city = response.body()?.city ?: "",
                    postalCode = response.body()?.postalCode.toString(),
                    progressPercentage = response.body()?.progress ?: 0f,
                    reviewId = response.body()?.inventoryId ?: 0,
                    reviewType = reviewType,
                    contractId = response.body()?.contractId ?: 0,
                    numberOfRooms = response.body()?.numberOfRooms ?: 0,
                    streetNumber = response.body()?.streetNumber ?: 0,
                    streetName = response.body()?.streetName ?: "",
                    longitude = response.body()?.longitude ?: 0f,
                    latitude = response.body()?.latitude ?: 0f,
                    description = response.body()?.description ?: "",
                    contracts = contracts,
                    photos = response.body()?.photos ?: mutableListOf(),
                ))
            } else {
                Log.e("PropertyViewModel", "Echec lors de la récupération des données du bien: ${state.value.token} / ${state.value.propertyId} / ${response.errorBody()?.string()}")
            }
        }
    }
}