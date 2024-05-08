package com.cm_immo_app.view.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.cm_immo_app.viewmodel.EDLViewModel
import kotlinx.coroutines.launch
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
    val offsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val transitionState = remember { MutableTransitionState(false) }

    var remainingCards = remember { titles.size }

    transitionState.targetState = remainingCards > 0
    val cardModifier = Modifier
        .width(cardWidth)
        .height(420.dp)
        .padding(16.dp)

    LaunchedEffect(cardIndex) {
        offsetX.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 300)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consumeAllChanges()
                    coroutineScope.launch {
                        offsetX.snapTo(offsetX.value + dragAmount)
                        val swipeThreshold = 300f
                        if (Math.abs(offsetX.value) > swipeThreshold) {
                            val direction = if (offsetX.value > 0) 1 else -1
                            val newCardIndex = cardIndex + direction
                            if (newCardIndex < titles.size && remainingCards > 0) {

                                offsetX.animateTo(
                                    targetValue = cardWidth.value * direction,
                                    animationSpec = tween(500)
                                )
                                onCardSwiped(newCardIndex)
                                remainingCards--
                                offsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(300)
                                )
                            }
                        } else {
                            offsetX.animateTo(0f, tween(300))
                        }
                    }
                }
            }
    ) {
        AnimatedVisibility(
            visibleState = transitionState,
            enter = fadeIn() + slideInHorizontally(initialOffsetX = { -cardWidth.value.toInt() }),
            exit = fadeOut() + slideOutHorizontally(targetOffsetX = { cardWidth.value.toInt() }),
        ) {
            if (cardIndex < titles.size && remainingCards > 0) {
                val imageList = imagesList[cardIndex % imagesList.size]
                ConditionCard(
                    viewModel,
                    navController,
                    titles[cardIndex],
                    imageList,
                    cardModifier.offset { IntOffset(offsetX.value.roundToInt(), 0) }
                )
            }
        }
    }
}

@Composable
fun ConditionCard(viewModel: EDLViewModel, navController: NavController, title: String, images: List<Int>,  modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<Int?>(null) }

    // Swipe logic
    var offsetX by remember { mutableStateOf(0f) }
    val maxOffsetX = 300f

    Card(
        modifier = Modifier
            .width(600.dp)
            .padding(20.dp)
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consumeAllChanges()
                    offsetX = (offsetX + dragAmount).coerceIn(-maxOffsetX, maxOffsetX)
                }
            },
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