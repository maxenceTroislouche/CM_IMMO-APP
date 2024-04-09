package com.cm_immo_app.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.Visibility
import com.cm_immo_app.R

@Composable
fun PasswordField(password: String, onPasswordChange: (String) -> Unit) {
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(stringResource(R.string.password)) },
        visualTransformation = PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { /* Toggle password visibility */ }) {
                Icon(imageVector = Icons.Default.Visibility, contentDescription = stringResource(R.string.show_password))
            }
        }
    )
}