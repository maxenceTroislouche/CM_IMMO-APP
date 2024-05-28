package com.cm_immo_app.view.navigation

import android.util.Log
import androidx.navigation.NavController

fun NavController.navigateToPropertiesPage(token: String, propertyId: Int) {
    navigate(route = "PropertyPage/$token/$propertyId")
}

fun NavController.navigateToPropertiesList(token: String) {
    Log.i("Login", "token: $token")
    navigate(route = "PropertiesListPage/$token")
}