package com.cm_immo_app.view.page


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cm_immo_app.models.PropertyDetails
import com.cm_immo_app.state.PropertyState
import com.cm_immo_app.view.components.ErrorPopup


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyPage(
    state: PropertyState,
    setError: (Boolean) -> Unit,
    navigateBack: () -> Unit,
    navigateToInventoryPage: (token: String, inventoryId: Int) -> Unit,
    getPropertyData: () -> Unit,
) {
    val token = state.token
    val idProperty = state.propertyId
    var propertyData by remember { mutableStateOf<PropertyDetails?>(null) }
    var selectedImage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = token, key2 = idProperty) {
        getPropertyData()
    }

    LaunchedEffect(key1 = state.property) {
        state.property?.let { property ->
            propertyData = property
            selectedImage = property.photos.firstOrNull()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { navigateBack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 45.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                propertyData?.let { property ->
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            AsyncImage(
                                model = selectedImage,
                                contentDescription = property.description,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                        ) {
                            property.photos.forEach { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(50.dp)
                                        .height(50.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedImage = imageUrl }
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = property.propertyType.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                    )
                    Text(
                        text = "${property.progressPercentage} %",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                    PropertyDetails(property)
                    PropertyLocation(property)
                    PropertyDescription(property)
                    PropertyContracts(property)
                } ?: run {
                    Text("Loading...", modifier = Modifier.padding(16.dp))
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.7f),
                                Color.White.copy(alpha = 1.0f)
                            ),
                            startY = 0f,
                            endY = 400f
                        )
                    )
            ) {
                Button(
                    onClick = { propertyData?.let { navigateToInventoryPage(token, it.reviewId) } },
                    modifier = Modifier
                        .padding(30.dp)
                        .width(300.dp)
                        .align(Alignment.Center),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC83F))
                ) {
                    Text(
                        text = if (propertyData?.progressPercentage == 0f) "Commencer l'état des lieux" else "Reprendre l'état des lieux",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
    ErrorPopup(
        error = state.error,
        setError = setError,
        title = "Une erreur est survenue",
        content = "Une erreur est survenue lors de la récupération des informations du bien : {token: ${state.token} / propertyId: ${state.propertyId}}"
    )
}

@Composable
fun PropertyDetails(property: PropertyDetails) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "N° d'état des lieux : ${property.reviewId}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Type : ${property.reviewType}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Bail concerné : ${property.contractId}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "N° du bien : ${property.id}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Type de bien : ${property.propertyType}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Nombre total de pièces : ${property.numberOfRooms}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun PropertyLocation(property: PropertyDetails) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Localisation",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "${property.streetNumber} ${property.streetName}, ${property.city} ${property.postalCode}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Gray)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Afficher la carte",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PropertyDescription(property: PropertyDetails) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Details du bien",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Text(
                text = property.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun PropertyContracts(property: PropertyDetails) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Historique de baux",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        property.contracts.forEach { contract ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF7DA0B3))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${contract.OwnerLastName} ${contract.OwnerFirstName}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "${contract.dateBeginning} - ${contract.dateEnd}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Signé",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
        Text(
            text = "Historique complet",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(200.dp))
    }
}
