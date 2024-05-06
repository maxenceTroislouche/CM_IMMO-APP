package com.cm_immo_app.view.page

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cm_immo_app.viewmodel.ReviewViewModel

@Composable
fun ReviewPage(viewModel: ReviewViewModel, navController: NavController) {
    val token = viewModel.token
    val reviewId = viewModel.reviewId
    Text(text = "token: $token / reviewId: $reviewId")
}