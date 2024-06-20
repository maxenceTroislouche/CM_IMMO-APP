package com.cm_immo_app.view.navigation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cm_immo_app.state.InventoryState
import com.cm_immo_app.view.page.InventoryPage
import com.cm_immo_app.viewmodel.InventoryViewmodel
import kotlin.system.exitProcess

fun NavGraphBuilder.InventoryNavigation(navigateBack: () -> Unit) {
    val inventoryViewmodel = InventoryViewmodel()
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

        inventoryViewmodel.setToken(token)
        inventoryViewmodel.setInventoryId(inventoryId)
        inventoryViewmodel.setProgress(50.0f)
        inventoryViewmodel.setWallImages(mutableListOf("https://images.pexels.com/photos/1108099/pexels-photo-1108099.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"))
        inventoryViewmodel.getInventoryData()
        val state: InventoryState by inventoryViewmodel.state
        InventoryPage(
            state = state,
            setProgress = inventoryViewmodel::setProgress,
            setRoomName = inventoryViewmodel::setRoomName,
            setWallImages = inventoryViewmodel::setWallImages,
            setSelectedEmoji = inventoryViewmodel::setSelectedEmoji,
            startCamera = inventoryViewmodel::startCamera,
            capturePhoto = inventoryViewmodel::capturePhoto,
            encodeFileToBase64 = inventoryViewmodel::encodeFileToBase64,
            updateMinute = inventoryViewmodel::updateMinute,
            navigateBack = navigateBack,
            setCurrentRoom = inventoryViewmodel::setCurrentRoom
        )
    }
}


