package com.cm_immo_app.view.page

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.cm_immo_app.viewmodel.EDLViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun EDL(viewModel: EDLViewModel, navController: NavController) {
    val scrollState = rememberScrollState()
    var cardIndex by remember { mutableStateOf(0) }
    val titles = listOf("État des Murs", "État du Sol")
    val images = listOf(viewModel.wallImages, viewModel.wallImages)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = viewModel.progress,
            color = Color(0xFF8BC83F),
            trackColor = Color(0xFFEBF3F2),
            modifier = Modifier
                .width(500.dp)
                .height(20.dp)
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(35.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = viewModel.roomName,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        ConditionCards(cardIndex, titles, images, viewModel, navController) { newIndex ->
            cardIndex = newIndex % titles.size
        }
    }
}

@Composable
fun ConditionCards(
    cardIndex: Int,
    titles: List<String>,
    imagesList: List<List<Int>>,
    viewModel: EDLViewModel,
    navController: NavController,
    onCardSwiped: (Int) -> Unit
) {
    val cardWidth = 600.dp
    var offsetX by remember { mutableStateOf(0f) }
    val dismissRight = remember { mutableStateOf(false) }
    val dismissLeft = remember { mutableStateOf(false) }
    val density = LocalDensity.current.density

    LaunchedEffect(dismissRight.value) {
        if (dismissRight.value) {
            delay(300)
            onCardSwiped(cardIndex + 1)
            dismissRight.value = false
        }
    }

    LaunchedEffect(dismissLeft.value) {
        if (dismissLeft.value) {
            delay(300)
            onCardSwiped(cardIndex - 1)
            dismissLeft.value = false
        }
    }

    val animatedOffset by animateFloatAsState(targetValue = offsetX, animationSpec = tween(300))

    Box(
        modifier = Modifier
            .width(600.dp)
            .padding(16.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(onDragEnd = {
                    offsetX = 0f
                }) { change, dragAmount ->
                    offsetX += (dragAmount / density)
                    when {
                        offsetX > 300f -> {
                            dismissRight.value = true
                        }
                        offsetX < -300f -> {
                            dismissLeft.value = true
                        }
                    }
                    if (change.positionChange() != Offset.Zero) change.consume()
                }
            }
            .graphicsLayer(
                alpha = 10f - animateFloatAsState(if (dismissRight.value) 1f else 0f).value,
                rotationZ = animateFloatAsState(offsetX / 50).value
            )
    ) {
        if (cardIndex < titles.size) {
            val imageList = imagesList[cardIndex % imagesList.size]
            ConditionCard(
                viewModel,
                navController,
                titles[cardIndex],
                imageList,
                Modifier.offset { IntOffset(animatedOffset.roundToInt(), 0) }
            )
        }
    }
}

@Composable
fun ConditionCard(viewModel: EDLViewModel, navController: NavController, title: String, images: List<Int>,  modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F4F8)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .height(700.dp)
                    .fillMaxWidth()
                    .background(Color.Gray, shape = RoundedCornerShape(12.dp))
            ) {
                Text("Camera View Placeholder", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Center))
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()).align(Alignment.TopStart).padding(8.dp)) {
                    images.forEach { image ->
                        Image(
                            painter = painterResource(id = image),
                            contentDescription = "Thumbnail",
                            modifier = Modifier
                                .size(50.dp)
                                .padding(end = 8.dp)
                                .clickable {
                                    selectedImage = image
                                    showDialog = true
                                }
                        )
                    }
                }
                if (showDialog && selectedImage != null) {
                    ImageFullScreenDialog(imageId = selectedImage!!, onDismiss = { showDialog = false })
                }
            }

            Spacer(modifier = Modifier.height(25.dp))
            EmojiFeedback(viewModel)
            Spacer(modifier = Modifier.height(25.dp))
            NoteSection(viewModel)
        }
    }
}

@Composable
fun ImageFullScreenDialog(imageId: Int, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = "Full-screen image",
                modifier = Modifier.fillMaxSize().clickable { onDismiss() }
            )
        }
    }
}


@Composable
fun EmojiFeedback(viewModel: EDLViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        viewModel.emojis.forEach { emoji ->
            Text(
                text = emoji,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable {
                    viewModel.onEmojiSelected(emoji)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteSection(viewModel: EDLViewModel) {
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Notes", textAlign = TextAlign.Center) },
        placeholder = { Text("Add your notes here", textAlign = TextAlign.Center) },
        singleLine = false,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}