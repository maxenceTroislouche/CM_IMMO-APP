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
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private val CAMERA_PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, CAMERA_PERMISSION_REQUEST_CODE)
        }

        setContent {
            // Fixer l'orientation de l'écran en portrait
            val context = LocalContext.current
            (context as? Activity)?.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            val navController = rememberNavController()
            NavHost(navController, startDestination = "login") {
                LoginNavigation(navController::navigateToPropertiesList)
                PropertiesListNavigation(navController::navigateToPropertiesPage)
                PropertyNavigation(
                    navigateToInventoryPage = { token, inventoryId ->
                        navController.navigate("inventoryPage/$token/$inventoryId")
                    },
                    navigateBack = { navController.popBackStack() }
                )
                InventoryNavigation()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }
}
