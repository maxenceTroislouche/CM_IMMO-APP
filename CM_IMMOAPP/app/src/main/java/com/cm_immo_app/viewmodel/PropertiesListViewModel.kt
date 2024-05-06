package com.cm_immo_app.viewmodel

import androidx.lifecycle.ViewModel
import com.cm_immo_app.R
import com.cm_immo_app.models.PropertySimple
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PropertiesListViewModel(var token: String) : ViewModel() {
    private val _properties = MutableStateFlow(listOf(
        PropertySimple("1", "Maison Hecquet", 50, R.drawable.house_hecquet, "Lens"),
        PropertySimple("2", "Maison Boone", 50, R.drawable.house_boone, "Lille"),
        // Add more properties here
    ))
    val properties: StateFlow<List<PropertySimple>> = _properties.asStateFlow()
}