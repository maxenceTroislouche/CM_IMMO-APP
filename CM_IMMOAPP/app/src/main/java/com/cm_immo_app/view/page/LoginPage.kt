package com.cm_immo_app.view.page

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.cm_immo_app.view.components.LoginHeader
import com.cm_immo_app.view.components.PasswordField
import com.cm_immo_app.viewmodel.LoginViewModel
import java.lang.reflect.Modifier

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginPage(viewModel: LoginViewModel) {
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()

    Scaffold(
        topBar = { LoginHeader() },
        content = {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = username,
                    onValueChange = { viewModel.onUsernameChanged(it) },
                    label = { Text(stringResource(R.string.username)) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordField(
                    password = password,
                    onPasswordChange = { viewModel.onPasswordChanged(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.login))
                }
            }
        }
    )
}
