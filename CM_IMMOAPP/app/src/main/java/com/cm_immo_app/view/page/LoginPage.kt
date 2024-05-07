package com.cm_immo_app.view.page

import android.content.ContentValues.TAG
import android.util.Log
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
import com.cm_immo_app.viewmodel.LoginViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.navigation.NavController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun LoginPage(viewModel: LoginViewModel, navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                    style = MaterialTheme.typography.titleLarge, // Adjusted for Material3
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
                    onValueChange = { username = it },
                    label = { Text(stringResource(id = R.string.username)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(35.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
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
                    onClick = {
                        // Navigate when the login is successful
                        val token = viewModel.onLoginClicked(
                            username = username,
                            password = password
                        )
                        Log.i(TAG, "LoginPage: token <$token>")
                        // Navigates to the propertiesListPage with the token of the estateagent
                        navController.navigate("PropertiesListPage/$token")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text("Se connecter")
                }
            }
        }
    }
}