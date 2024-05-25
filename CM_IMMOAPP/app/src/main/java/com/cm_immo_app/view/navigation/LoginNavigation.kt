package com.cm_immo_app.view.navigation

import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cm_immo_app.state.LoginState
import com.cm_immo_app.view.page.LoginPage
import com.cm_immo_app.viewmodel.LoginViewModel

const val LoginRoute = "login"

fun NavGraphBuilder.LoginNavigation(
    loginViewModel: LoginViewModel,
    navigateToPropertiesList: (token: String) -> Unit,
) {
    composable(LoginRoute) {
        val state: LoginState by loginViewModel.state
        LoginPage(
            state = state,
            setUsername = loginViewModel::setUsername,
            setPassword = loginViewModel::setPassword,
            connect = loginViewModel::connect,
            navigateToPropertiesList = navigateToPropertiesList,
        )
    }
}

fun NavController.navigateToPropertiesList(token: String) {
    navigate(route = "PropertiesListPage/$token")
}