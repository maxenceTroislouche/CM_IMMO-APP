package com.cm_immo_app.view.page

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.cm_immo_app.viewmodel.ReviewViewModel

@Composable
fun ReviewPage(viewModel: ReviewViewModel, navController: NavController) {
    val token = viewModel.token
    val reviewId = viewModel.reviewId
    Text(text = "token: $token / reviewId: $reviewId")
}