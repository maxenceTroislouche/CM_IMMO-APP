package com.cm_immo_app.view.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cm_immo_app.R
import com.cm_immo_app.models.PropertySimple
import com.cm_immo_app.viewmodel.PropertiesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertiesListPage(viewModel: PropertiesListViewModel, navController: NavController) {
    LaunchedEffect(Unit) {
        viewModel.getProperties()
    }

    val properties = viewModel.properties.collectAsState().value
    val token = viewModel.token

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
                    titleContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(gradient)
                .padding(innerPadding)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val propertyCount = properties.size
            Text(
                "$propertyCount maisons actuellement en gestion",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 180.dp),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                items(properties) { property ->
                    Text(text = "${property.id} / ${property.name}")
                    // PropertyCard(property, navController, token)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyCard(property: PropertySimple, navController: NavController, token: String) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .height(360.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = {
            navController.navigate("PropertyPage/$token/${property.id}")
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                /*
                Image(
                    painter = painterResource(id = property.ImageUrl),
                    contentDescription = property.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                )*/
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(
                            color = Color(0xFF1F4C6B).copy(alpha = 0.6f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = "${property.completion}%",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 2.dp)
            ) {
                Text(
                    text = property.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location_pin),
                        contentDescription = "Location",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = property.location,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
