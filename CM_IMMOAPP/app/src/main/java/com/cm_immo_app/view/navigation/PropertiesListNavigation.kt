package com.cm_immo_app.view.navigation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cm_immo_app.state.PropertiesListState
import com.cm_immo_app.view.page.PropertiesListPage
import com.cm_immo_app.viewmodel.PropertiesListViewModel
import kotlin.system.exitProcess

fun NavGraphBuilder.PropertiesListNavigation(
    navigateToPropertiesPage: (token: String, propertyId: Int) -> Unit,
) {
    val propertiesListViewModel = PropertiesListViewModel()

    composable(
        route = "PropertiesListPage/{token}",
        arguments = listOf(
            navArgument(name = "token") {
                type = NavType.StringType
            },
        )
    ) {backstackEntry ->
        // On récupère le token depuis le backstackEntry, si pas défini exit -1
        val token = backstackEntry.arguments?.getString("token")
        if (token == null) {
            Log.e("PropertiesListNavigation", "pas de token défini")
            exitProcess(-1)
        }

        propertiesListViewModel.setToken(token)

        val state: PropertiesListState by propertiesListViewModel.state
        PropertiesListPage(
            state = state,
            navigateToPropertiesPage = navigateToPropertiesPage,
            getProperties = propertiesListViewModel::getProperties,
            setError = propertiesListViewModel::setError,
        )
    }
}