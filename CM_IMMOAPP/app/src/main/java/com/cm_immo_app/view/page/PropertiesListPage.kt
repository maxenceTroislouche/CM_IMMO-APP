package com.cm_immo_app.view.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cm_immo_app.R
import com.cm_immo_app.viewmodel.PropertiesListViewModel
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertiesListPage(viewModel: PropertiesListViewModel, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Les Biens") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            viewModel.properties.collectAsState().value.forEach { property ->
                PropertyCard(property = property)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun PropertyCard(property: Property) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp),
        //elevation = CardDefaults.elevation(defaultElevation = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = property.imageId),
                contentDescription = property.name,
                modifier = Modifier.size(100.dp)
            )
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(property.name, style = MaterialTheme.typography.bodyLarge)
                LinearProgressIndicator(
                    progress = property.completion.toFloat() / 100,
                    color = if (property.completion < 50) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Text("${property.completion}% complété")
            }
        }
    }
}
data class Property(
    val name: String,
    val completion: Int,
    val imageId: Int
)
