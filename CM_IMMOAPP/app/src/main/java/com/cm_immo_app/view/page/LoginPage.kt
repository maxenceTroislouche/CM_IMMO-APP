package com.cm_immo_app.view.page

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.cm_immo_app.R
import com.cm_immo_app.view.components.LoginHeader
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import com.cm_immo_app.state.LoginState

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun LoginPage(
    state: LoginState,
    setUsername: (username: String) -> Unit,
    setPassword: (password: String) -> Unit,
    connect: () -> Unit,
    navigateToPropertiesList: (token: String) -> Unit,
) {
    val username = state.username
    val password = state.password
    val token = state.token

    // Observe changes to the state variable
    LaunchedEffect(token) {
        // Navigate to the login screen if the state is "logged_out"
        if (token != "") {
            navigateToPropertiesList(token)
        }
    }

    var passwordVisible by remember { mutableStateOf(false) }

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFF5F4F8),
        unfocusedContainerColor = Color(0xFFF5F4F8),
        focusedIndicatorColor = Color(0xFF4CAF50),
        unfocusedIndicatorColor = Color.Gray
    )
    val gradient = Brush.radialGradient(
        colors = listOf(Color(0xFF1F4C6B), Color.Transparent),
        center = Offset(1500f, 1800f),
        radius = 800f
    )

    BoxWithConstraints {
        Scaffold(
            topBar = { LoginHeader() }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .background(gradient)
                    .padding(innerPadding)
                    .padding(horizontal = 200.dp, vertical = 50.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Text(
                    text = "Se connecter",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text(
                    text = "Se connecter sur la plateforme avec vos identifiants d'agent",
                    style = MaterialTheme.typography.bodySmall, // Adjusted for Material3
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(35.dp))

                TextField(
                    value = username,
                    onValueChange = { setUsername(it) },
                    label = { Text(stringResource(id = R.string.username)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(35.dp))
                TextField(
                    value = password,
                    onValueChange = { setPassword(it) },
                    label = { Text(stringResource(id = R.string.password)) },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(if (passwordVisible) "Hide" else "Show")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(80.dp))

                Button(
                    onClick = { connect() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text("Se connecter")
                }

                Text(text = "Mon token : $token")
            }
        }
    }
}