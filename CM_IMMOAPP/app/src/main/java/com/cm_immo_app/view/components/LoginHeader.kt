package com.cm_immo_app.view.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.cm_immo_app.R

@Composable
fun LoginHeader() {
    Image(
        painter = painterResource(id = R.drawable.undraw_city_life_gnpr_2),
        contentDescription = "City Life"
    )
}
