package com.cm_immo_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cm_immo_app.view.page.LoginPage
import com.cm_immo_app.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialisez votre ViewModel ici. Cela dépend de la manière dont vous gérez les instances de ViewModel.
        // Pour simplifier, je crée une nouvelle instance, mais en réalité, vous voudrez peut-être utiliser ViewModelProvider ou un autre mécanisme de dépendance.
        val loginViewModel = LoginViewModel()

        setContent {
            // Ici, nous appelons LoginPage. Vous pourriez envelopper cela dans votre thème si vous en avez un.
            LoginPage(viewModel = loginViewModel)
        }
    }
}
