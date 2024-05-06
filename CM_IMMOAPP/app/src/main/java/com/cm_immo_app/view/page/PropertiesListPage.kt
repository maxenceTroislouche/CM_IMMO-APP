package com.cm_immo_app.view.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cm_immo_app.viewmodel.PropertiesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertiesListPage(viewModel: PropertiesListViewModel, navController: NavController) {
    val properties = viewModel.properties.collectAsState().value

    val gradient = Brush.radialGradient(
        colors = listOf(Color(0xFF1F4C6B), Color.Transparent),
        center = Offset(1500f, 1800f),
        radius = 800f
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Les Biens", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .background(Color.White)
            .background(gradient)
            .padding(innerPadding)
            .fillMaxHeight()
        ) {
            val propertyCount = properties.size
            Text(
                "$propertyCount maisons actuellement en gestion",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.padding(12.dp)
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
            .padding(10.dp)
            .fillMaxWidth(),

    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = painterResource(id = property.imageId),
                contentDescription = property.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            LinearProgressIndicator(
                progress = property.completion.toFloat() / 100,
                color = if (property.completion < 50) Color.Red else Color.Green,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Text(
                text = property.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Location: ${property.location}",
                fontSize = 16.sp,
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
