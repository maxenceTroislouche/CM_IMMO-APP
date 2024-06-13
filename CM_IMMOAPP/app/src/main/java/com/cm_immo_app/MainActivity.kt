package com.cm_immo_app
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.cm_immo_app.view.navigation.InventoryNavigation
import com.cm_immo_app.view.navigation.LoginNavigation
import com.cm_immo_app.view.navigation.PropertiesListNavigation
import com.cm_immo_app.view.navigation.PropertyNavigation
import com.cm_immo_app.view.navigation.navigateToInventoryPage
import com.cm_immo_app.view.navigation.navigateToPropertiesList
import com.cm_immo_app.view.navigation.navigateToPropertiesPage


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Fixer l'orientation de l'écran en portrait
            val context = LocalContext.current
            (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            val navController = rememberNavController()
            NavHost(navController, startDestination = "login") {
                LoginNavigation(navController::navigateToPropertiesList)
                PropertiesListNavigation(navController::navigateToPropertiesPage)
                PropertyNavigation(navController::navigateToInventoryPage)
                InventoryNavigation()
            }
        }
    }
}