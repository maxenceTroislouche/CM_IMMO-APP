package com.cm_immo_app.viewmodel

import androidx.lifecycle.ViewModel
import com.cm_immo_app.R
import com.cm_immo_app.view.page.Property
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PropertiesListViewModel : ViewModel() {
    private val _properties = MutableStateFlow(listOf(
        Property("Maison Hecquet", 50, R.drawable.house_hecquet, "Lens"),
        Property("Maison Boone", 50, R.drawable.house_boone, "Lille"),
        // Add more properties here
    ))
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()
}