package com.cm_immo_app
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cm_immo_app.view.navigation.LoginNavigation
import com.cm_immo_app.view.navigation.PropertiesListNavigation
import com.cm_immo_app.view.navigation.PropertyNavigation
import com.cm_immo_app.view.navigation.ReviewNavigation
import com.cm_immo_app.view.navigation.navigateToPropertiesList
import com.cm_immo_app.view.navigation.navigateToPropertiesPage
import com.cm_immo_app.view.page.*
import com.cm_immo_app.viewmodel.*

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
                PropertyNavigation()
                ReviewNavigation()


//                // PropertyPage/{token}/{idProperty}
//                composable(
//                    route = "PropertyPage/{token}/{propertyId}",
//                    arguments = listOf(
//                        navArgument(name = "token") {
//                            type = NavType.StringType
//                        },
//                        navArgument(name = "propertyId") {
//                            type = NavType.StringType
//                        },
//                    )
//                ) { backstackEntry ->
//                    val token = backstackEntry.arguments?.getString("token")
//                    val idProperty = backstackEntry.arguments?.getString("propertyId")
//                    if (token != null && idProperty != null) {
//                        val propertyViewModel = PropertyViewModel(token, idProperty)
//                        PropertyPage(propertyViewModel, navController)
//                    }
//                }
//
//                // Review/{token}/{reviewId}
//                composable(
//                    route = "ReviewPage/{token}/{reviewId}",
//                    arguments = listOf(
//                        navArgument(name = "token") {
//                            type = NavType.StringType
//                        },
//                        navArgument(name = "reviewId") {
//                            type = NavType.StringType
//                        },
//                    ),
//                ) { backstackEntry ->
//                    val token = backstackEntry.arguments?.getString("token")
//                    val reviewId = backstackEntry.arguments?.getString("reviewId")
//                    if (token != null && reviewId != null) {
//                        val reviewViewModel = ReviewViewModel(token, reviewId)
//                        ReviewPage(reviewViewModel, navController)
//                    }
//                }
            }
        }
    }
}


/*
Test chlag du EDL Page
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val edlViewModel = EDLViewModel()

        setContent {
            // Fixer l'orientation de l'écran en portrait
            val context = LocalContext.current
            (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            val navController = rememberNavController()
            NavHost(navController, startDestination = "edl") {
                composable("edl") {
                    EDL(edlViewModel, navController)
                }
            }
        }
    }
}
*/