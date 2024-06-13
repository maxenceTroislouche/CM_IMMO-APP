package com.cm_immo_app.models

import java.util.Date

data class PropertyDetails(
    val id: Int,
    val propertyType: String,
    val city: String,
    val postalCode: String,
    val progressPercentage: Float,
    val reviewId: Int,
    val reviewType: String,
    val contractId: Int,
    val numberOfRooms: Int,
    val streetNumber: Int,
    val streetName: String,
    val longitude: Float,
    val latitude: Float,
    val description: String,
    val contracts: List<Contract>,
    val photos: List<String>,
)

data class Contract(
    val dateBeginning: Date,
    val dateEnd: Date,
    val OwnerLastName: String,
    val OwnerFirstName: String,
)