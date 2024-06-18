package com.cm_immo_app.view.navigation

import android.util.Log
import androidx.navigation.NavController

fun NavController.navigateToPropertiesPage(token: String, propertyId: Int) {
    navigate(route = "PropertyPage/$token/$propertyId")
}

fun NavController.navigateToPropertiesListPage(token: String) {
    Log.i("Login", "token: $token")
    navigate(route = "PropertiesListPage/$token")
}

fun NavController.navigateToInventoryPage(token: String, inventoryId: Int) {
    Log.i("NavigateToInventoryPage", "token: $token / inventoryId: $inventoryId")
    navigate(route = "InventoryPage/$token/$inventoryId")
}

fun NavController.navigateToSignPage(
    token: String,
    type: String,
    inventoryId: Int,
) {
    Log.i("NavigateToSignaturePage", "token: $token / type: $type / inventoryId: $inventoryId")
    navigate(route = "SignPage/$token/$type/$inventoryId")
}