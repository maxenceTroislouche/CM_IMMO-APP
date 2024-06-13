package com.cm_immo_app.view.navigation

import android.util.Log
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cm_immo_app.view.page.EDL
import com.cm_immo_app.viewmodel.EDLViewModel
import kotlin.system.exitProcess

fun NavGraphBuilder.InventoryNavigation() {
    val edlViewModel = EDLViewModel()
    composable(
        route = "InventoryPage/{token}/{inventoryId}",
        arguments = listOf(
            navArgument(name = "token") {
                type = NavType.StringType
            },
            navArgument(name = "inventoryId") {
                type = NavType.IntType
            },
        )
    ) { backstackEntry ->
        val token = backstackEntry.arguments?.getString("token")
        if (token == null) {
            Log.e("InventoryNavigation", "Pas de token !")
            exitProcess(-1)
        }

        val inventoryId = backstackEntry.arguments?.getInt("inventoryId")
        if (inventoryId == null) {
            Log.e("InventoryNavigation", "Pas d'id edl !")
            exitProcess(-1)
        }

        EDL(edlViewModel)
    }
}