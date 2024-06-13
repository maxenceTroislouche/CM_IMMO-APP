package com.cm_immo_app.view.page

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.cm_immo_app.state.PropertyState

@Composable
fun PropertyPage(
    state: PropertyState,
    navigateToInventoryPage: (token: String, inventoryId: Int) -> Unit,
    getPropertyData: () -> Unit,
) {
    val token = state.token
    val idProperty = state.propertyId
    val propertyData = state.property

    LaunchedEffect(key1 = token, key2 = idProperty) {
        getPropertyData()
    }

    Text(text = "token: $token / idProperty: $idProperty / propertyData: $propertyData")

    Button(onClick = { navigateToInventoryPage(token, propertyData?.reviewId ?: -1) }) {
        Text(text = "Continuer l'état des lieux")
    }
}
