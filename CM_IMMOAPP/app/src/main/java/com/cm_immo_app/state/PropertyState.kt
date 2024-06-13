package com.cm_immo_app.state

import com.cm_immo_app.models.PropertyDetails

data class PropertyState(
    val token: String = "",
    val propertyId: Int = -1,
    val property: PropertyDetails? = null,
)