package com.cm_immo_app.view.navigation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cm_immo_app.state.PropertyState
import com.cm_immo_app.view.page.PropertyPage
import com.cm_immo_app.viewmodel.PropertyViewModel
import kotlin.system.exitProcess

fun NavGraphBuilder.PropertyNavigation(
    navigateToInventoryPage: (token: String, inventoryId: Int) -> Unit
) {
    val propertyViewModel = PropertyViewModel()

    composable(
        route = "PropertyPage/{token}/{propertyId}",
        arguments = listOf(
            navArgument(name = "token") {
                type = NavType.StringType
            },
            navArgument(name = "propertyId") {
                type = NavType.IntType
            },
        )
    ) {backstackEntry ->
        val token = backstackEntry.arguments?.getString("token")
        if (token == null) {
            Log.e("PropertyNavigation", "pas de token défini")
            exitProcess(-1)
        }

        val propertyId = backstackEntry.arguments?.getInt("propertyId")
        if (propertyId == null) {
            Log.e("PropertyNavigation", "Pas d'id bien")
            exitProcess(-1)
        }

        propertyViewModel.setToken(token)
        propertyViewModel.setPropertyId(propertyId)

        val state: PropertyState by propertyViewModel.state
        PropertyPage(
            state = state,
            navigateToInventoryPage = navigateToInventoryPage,
            getPropertyData = propertyViewModel::getPropertyData,
        )
    }
}