package com.cm_immo_app.state

import com.cm_immo_app.models.PropertySimple

data class PropertiesListState(
    val token: String = "",
    val properties: List<PropertySimple> = mutableListOf(),
    val error: Boolean = false,
)