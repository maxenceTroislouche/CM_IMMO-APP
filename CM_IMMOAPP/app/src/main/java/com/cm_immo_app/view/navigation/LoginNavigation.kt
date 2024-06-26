package com.cm_immo_app.view.navigation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cm_immo_app.state.LoginState
import com.cm_immo_app.view.page.LoginPage
import com.cm_immo_app.viewmodel.LoginViewModel

const val LoginRoute = "login"

fun NavGraphBuilder.LoginNavigation(
    navigateToPropertiesList: (token: String) -> Unit,
    navigateToSignPage: (token: String, type: String, inventoryId: Int) -> Unit,
) {
    val loginViewModel = LoginViewModel()
    composable(LoginRoute) {
        val state: LoginState by loginViewModel.state
        LoginPage(
            state = state,
            setUsername = loginViewModel::setUsername,
            setPassword = loginViewModel::setPassword,
            setError = loginViewModel::setError,
            connect = loginViewModel::connect,
            navigateToPropertiesList = navigateToPropertiesList,
        )
        // TODO: test chlag pour tester les signatures
        // navigateToSignPage("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJpYXQiOjE3MTg4ODY5OTIsImV4cCI6MTcxOTQ5MTc5Mn0.ebSpBXcwPrDzMQnUpcDw8e1Dca6PdtBBkz9pMO9TS0Y", "LOCATAIRE", 1)
    }
}