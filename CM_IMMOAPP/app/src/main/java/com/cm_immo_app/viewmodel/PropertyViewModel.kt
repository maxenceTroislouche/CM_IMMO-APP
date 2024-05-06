package com.cm_immo_app.viewmodel

import androidx.lifecycle.ViewModel
import com.cm_immo_app.models.Contract
import com.cm_immo_app.models.PropertyDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class PropertyViewModel(var token: String, val idProperty: String) : ViewModel() {
    private val _property = MutableStateFlow(
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
        )
    )
    val property: StateFlow<PropertyDetails> = _property.asStateFlow()
}