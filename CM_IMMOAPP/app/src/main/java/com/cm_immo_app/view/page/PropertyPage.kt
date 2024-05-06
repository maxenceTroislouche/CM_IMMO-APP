package com.cm_immo_app.view.page

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cm_immo_app.viewmodel.PropertyViewModel

@Composable
fun PropertyPage(viewModel: PropertyViewModel, navController: NavController) {
    val token = viewModel.token
    val idProperty = viewModel.idProperty
    Text(text = "token: $token / idProperty: $idProperty")
}
