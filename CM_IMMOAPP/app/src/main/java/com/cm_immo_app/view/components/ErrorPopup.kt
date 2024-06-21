package com.cm_immo_app.view.components

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun ErrorPopup(
    error: Boolean,
    setError: (Boolean) -> Unit,
    title: String,
    content: String,
) {
    if (error) {
        AlertDialog(
            onDismissRequest = { setError(false) },
            title = { Text(text = title) },
            text = { Text(text = content) },
            containerColor = Color.White,
            confirmButton = {
                Button(
                    onClick = {
                        setError(false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                ) {
                    Text("OK")
                }
            },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        )
    }
}