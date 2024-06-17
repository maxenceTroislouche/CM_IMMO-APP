package com.cm_immo_app.view.page

import android.content.Context
import android.view.View
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.cm_immo_app.R
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import com.cm_immo_app.state.InventoryState
import com.cm_immo_app.utils.http.MinuteUpdate


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ConditionCards(
    cardIndex: Int,
    titles: List<String>,
    imagesList: List<List<String>>,
    state: InventoryState,
    startCamera: (context: Context, lifecycleOwner: LifecycleOwner, previewView: PreviewView) -> Unit,
    setSelectedEmoji: (selectedEmoji: String) -> Unit,
    onCardSwiped: (Int) -> Unit
) {
    val cardWidth = 600.dp
    var offsetX by remember { mutableStateOf(0f) }
    val dismissDirection = remember { mutableStateOf(0) }
    val density = LocalDensity.current.density

    LaunchedEffect(dismissDirection.value) {
        when (dismissDirection.value) {
            1 -> {
                delay(300)
                onCardSwiped((cardIndex + 1) % titles.size)
                dismissDirection.value = 0
            }
            -1 -> {
                delay(300)
                onCardSwiped((cardIndex - 1 + titles.size) % titles.size)
                dismissDirection.value = 0
            }
        }
    }

    val animatedOffset by animateFloatAsState(targetValue = offsetX, animationSpec = tween(300))
    val animatedWidth by animateDpAsState(targetValue = if (offsetX == 0f) cardWidth else cardWidth - 50.dp)

    Box(
        modifier = Modifier
            .width(animatedWidth)
            .padding(16.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(onDragEnd = {
                    offsetX = 0f
                }) { change, dragAmount ->
                    offsetX += (dragAmount / density)
                    if (offsetX > 300f) dismissDirection.value = 1
                    if (offsetX < -300f) dismissDirection.value = -1
                    if (change.positionChange() != Offset.Zero) change.consume()
                }
            }
            .graphicsLayer(
                alpha = 1f,
                rotationZ = animatedOffset / 50
            )
    ) {
        AnimatedContent(
            targetState = cardIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { width -> width } + fadeIn() with
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() with
                            slideOutHorizontally { width -> width } + fadeOut()
                }.using(SizeTransform(clip = false))
            }
        ) { targetCardIndex ->
            if (targetCardIndex < titles.size) {
                val imageList = imagesList[targetCardIndex]
                ConditionCard(
                    state,
                    startCamera,
                    setSelectedEmoji,
                    titles[targetCardIndex],
                    imageList,
                    Modifier.offset { IntOffset(animatedOffset.roundToInt(), 0) }
                )
            }
        }
    }
}

@Composable
fun ImageFullScreenDialog(imageString: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = imageString,
                contentDescription = "Full-screen image",
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onDismiss() }
            )
        }
    }
}

@Composable
fun EmojiFeedback(state: InventoryState, setSelectedEmoji: (selectedEmoji: String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        state.emojis.forEach { emoji ->
            Text(
                text = emoji,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable {
                    setSelectedEmoji(emoji)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteSection(state: InventoryState) {
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

@Composable
fun ConditionCard(
    state: InventoryState,
    startCamera: (context: Context, lifecycleOwner: LifecycleOwner, previewView: PreviewView) -> Unit,
    setSelectedEmoji: (selectedEmoji: String) -> Unit,
    title: String,
    images: List<String>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F4F8)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight()) {
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
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray)
            ) {
                val previewView = remember { PreviewView(context).apply { id = View.generateViewId() } }
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize()
                ) { view ->
                    startCamera(context, lifecycleOwner, view)
                }
                Column(modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .align(Alignment.TopStart)
                    .padding(8.dp)) {
                    images.forEach { image ->
                        AsyncImage(
                            model = image,
                            contentDescription = "Thumbnail",
                            modifier = Modifier
                                .size(100.dp)
                                .padding(8.dp)
                                .clickable {
                                    selectedImage = image
                                    showDialog = true
                                }
                        )
                    }
                }
                if (showDialog && selectedImage != null) {
                    ImageFullScreenDialog(imageString = selectedImage!!, onDismiss = { showDialog = false })
                }
            }

            Spacer(modifier = Modifier.height(25.dp))
            EmojiFeedback(state, setSelectedEmoji)
            Spacer(modifier = Modifier.height(25.dp))
            NoteSection(state)
        }
    }
}


@Composable
fun CaptureButton(onCaptureClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        FloatingActionButton(
            onClick = onCaptureClick,
            shape = CircleShape,
            containerColor = Color(0xFF8BC83F),
            contentColor = Color.White,
            modifier = Modifier.size(70.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.camera),
                contentDescription = "Capture Photo",
                tint = Color.White
            )
        }
    }
}

@Composable
fun InventoryPage(
    state: InventoryState,
    setProgress: (progress: Float) -> Unit,
    setRoomName: (roomName: String) -> Unit,
    setWallImages: (wallImages: List<String>) -> Unit,
    setSelectedEmoji: (selectedEmoji: String) -> Unit,
    startCamera: (context: Context, lifecycleOwner: LifecycleOwner, previewView: PreviewView) -> Unit,
    capturePhoto: (context: Context, onImageCaptured: (String?) -> Unit) -> Unit,
    encodeFileToBase64: (filePath: String) -> String?,
    updateMinute: (minute: MinuteUpdate) -> Unit,
) {
    val scrollState = rememberScrollState()
    var cardIndex by remember { mutableStateOf(0) }
    val titles = listOf("État des Murs", "État du Sol")
    val images = listOf(state.wallImages, state.wallImages)
    val context = LocalContext.current

    // Dégradé de fond
    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color(0xFF8BC83F)),
        startY = 0f,
        endY = 1000f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = state.progress,
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
                text = state.roomName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            ConditionCards(
                cardIndex,
                titles,
                images,
                state,
                startCamera,
                setSelectedEmoji,
            ) { newIndex ->
                cardIndex = newIndex % titles.size
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(gradient)
                    .align(Alignment.BottomCenter)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                CaptureButton(onCaptureClick = {
                    capturePhoto(context) { uri ->
                        // Handle the captured photo URI here if needed
                    }
                })
            }
        }
    }
}