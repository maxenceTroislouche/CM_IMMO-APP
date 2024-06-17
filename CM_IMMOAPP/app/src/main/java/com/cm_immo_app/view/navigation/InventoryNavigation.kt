package com.cm_immo_app.view.navigation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cm_immo_app.state.InventoryState
import com.cm_immo_app.state.LoginState
import com.cm_immo_app.view.page.InventoryPage
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

        edlViewModel.setToken(token)
        edlViewModel.setInventoryId(inventoryId)
        edlViewModel.setProgress(50.0f)
        edlViewModel.setWallImages(mutableListOf())
        val state: InventoryState by edlViewModel.state
        InventoryPage(
            state = state,
            edlViewModel::setProgress,
            edlViewModel::setRoomName,
            edlViewModel::setWallImages,
            edlViewModel::setSelectedEmoji,
            edlViewModel::startCamera,
            edlViewModel::capturePhoto,
            edlViewModel::encodeFileToBase64,
            edlViewModel::updateMinute,
        )
    }
}