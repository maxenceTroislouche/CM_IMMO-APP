package com.cm_immo_app.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cm_immo_app.R

@Composable
fun LoginHeader() {
    Image(
        painter = painterResource(id = R.drawable.undraw_city_life_gnpr_2),
        contentDescription = "City Life",
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
    )
}