package com.cm_immo_app.view.navigation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cm_immo_app.state.SignState
import com.cm_immo_app.view.page.SignaturePage
import com.cm_immo_app.viewmodel.SignViewModel
import kotlin.system.exitProcess

fun NavGraphBuilder.SignNavigation(
    navigateToSignPage: (token: String, type: String, inventoryId: Int) -> Unit,
    navigateToPropertiesListPage: (token: String) -> Unit,
) {
    val signViewModel = SignViewModel()

    composable(
        route = "SignPage/{token}/{type}/{inventoryId}",
        arguments = listOf(
            navArgument(name = "token") {
                type = NavType.StringType
            },
            navArgument(name = "type") {
                type = NavType.StringType
            },
            navArgument(name = "inventoryId") {
                type = NavType.IntType
            },
        )
    ) { backstackEntry ->
        val token = backstackEntry.arguments?.getString("token")
        val type = backstackEntry.arguments?.getString("type")
        val inventoryId = backstackEntry.arguments?.getInt("inventoryId")

        if (token == null || type == null || inventoryId == null) {
            Log.e("SignNavigation", "Un des paramètres n'a pas été saisi: {token: $token} / {type: $type} / {inventoryId: $inventoryId}")
            Log.i("SignNavigation", "${backstackEntry.arguments}")
            exitProcess(-1)
        }

        if (type != "AGENT" && type != "LOCATAIRE" && type != "PROPRIETAIRE") {
            Log.e("SignNavigation", "Le paramètre type est incorrect $type")
            exitProcess(-1)
        }

        signViewModel.setToken(token)
        signViewModel.setType(type)
        signViewModel.setInventoryId(inventoryId)

        val state: SignState by signViewModel.state

        SignaturePage(
            state,
            signViewModel::saveSignature,
            navigateToSignPage,
            navigateToPropertiesListPage,
        )
    }
}
