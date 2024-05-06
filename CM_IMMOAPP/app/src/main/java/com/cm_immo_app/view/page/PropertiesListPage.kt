package com.cm_immo_app.view.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cm_immo_app.R
import com.cm_immo_app.viewmodel.PropertiesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertiesListPage(viewModel: PropertiesListViewModel, navController: NavController) {
    val properties = viewModel.properties.collectAsState().value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Les Biens", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            val propertyCount = properties.size
            Text(
                "$propertyCount maisons actuellement en gestion",
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.padding(innerPadding)
            ) {
                items(properties) { property ->
                    PropertyCard(property)
                }
            }
        }
    }
}


@Composable
fun PropertyCard(property: Property) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        //TODO elevation
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(id = property.imageId),
                contentDescription = property.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            LinearProgressIndicator(
                progress = property.completion.toFloat() / 100,
                color = if (property.completion < 50) Color.Red else Color.Green,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            )
            Text(
                text = property.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Location: ${property.location}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

data class Property(
    val name: String,
    val completion: Int,
    val imageId: Int,
    val location: String
)

