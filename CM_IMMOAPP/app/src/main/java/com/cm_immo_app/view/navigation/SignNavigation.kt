package com.cm_immo_app.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.cm_immo_app.view.page.SignaturePage

@Composable
fun SignNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "signature") {
        composable("signature") { SignaturePage() }

    }
}
