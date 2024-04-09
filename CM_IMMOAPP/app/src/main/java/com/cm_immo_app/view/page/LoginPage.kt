package com.cm_immo_app.view.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cm_immo_app.view.components.LoginHeader
import com.cm_immo_app.R
import com.cm_immo_app.viewmodel.LoginViewModel

@Composable
fun LoginPage(viewModel: LoginViewModel) {
    val username by viewModel.username.collectAsState()

    Scaffold(
        topBar = { LoginHeader() },
        content = { paddingValues ->
            Column(modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()) {
                TextField(
                    value = username,
                    onValueChange = viewModel::onUsernameChanged,
                    label = { Text(stringResource(id = R.string.username)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
