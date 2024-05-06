package com.cm_immo_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cm_immo_app.view.page.LoginPage
import com.cm_immo_app.view.page.PropertiesListPage
import com.cm_immo_app.viewmodel.LoginViewModel
import com.cm_immo_app.viewmodel.PropertiesListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loginViewModel = LoginViewModel()
        val propertiesViewModel = PropertiesListViewModel()

        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "login") {
                composable("login") {
                    LoginPage(loginViewModel, navController)
                }
                composable("PropertiesListPage") {
                    PropertiesListPage(propertiesViewModel, navController)
                }
            }
        }
    }
}
