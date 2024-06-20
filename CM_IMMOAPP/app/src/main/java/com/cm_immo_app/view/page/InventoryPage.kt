package com.cm_immo_app.view.page

import android.content.Context
import android.util.Log
import android.view.View
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.border
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
import com.cm_immo_app.utils.http.InventoryData
import com.cm_immo_app.utils.http.Minute
import com.cm_immo_app.utils.http.MinuteUpdate
import com.cm_immo_app.utils.http.Room
import kotlinx.coroutines.selects.select
import kotlin.math.min


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ConditionCards(
    cardIndex: Int,
    titles: List<String>,
    imagesList: List<List<String>>,
    state: InventoryState,
    startCamera: (context: Context, lifecycleOwner: LifecycleOwner, previewView: PreviewView) -> Unit,
    setGrade: (emoji: String) -> Unit,
    setNoteText: (noteText: String) -> Unit,
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
                onCardSwiped((cardIndex + 1))
                dismissDirection.value = 0
            }
            -1 -> {
                delay(300)
                onCardSwiped((cardIndex - 1 + titles.size))
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
                    setGrade,
                    setNoteText,
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
fun EmojiFeedback(state: InventoryState, setGrade: (Emoji: String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val indexEmoji = state.rooms[state.currentRoom!!].minutes[state.currentMinute!!].grade
        var selectedEmoji: String? = null

        if (indexEmoji in 0..5) {
            selectedEmoji = state.emojis[indexEmoji]
        }

        state.emojis.forEach { emoji ->
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedEmoji == emoji) Color.LightGray else Color.Transparent)
                    .border(
                        2.dp,
                        if (selectedEmoji == emoji) Color.Blue else Color.Gray,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        selectedEmoji = emoji
                        setGrade(emoji)
                    }
                    .padding(20.dp)
            ) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedEmoji == emoji) Color.Blue else Color.Black
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteSection(state: InventoryState, setNoteText: (noteText: String) -> Unit) {
    val remark = state.rooms[state.currentRoom!!].minutes[state.currentMinute!!].remark

    TextField(
        value = remark,
        onValueChange = { setNoteText(it) },
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
    setGrade: (emoji: String) -> Unit,
    setNoteText: (noteText: String) -> Unit,
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
            EmojiFeedback(state, setGrade)
            Spacer(modifier = Modifier.height(25.dp))
            NoteSection(state, setNoteText)
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
    setProgress: (progress: Int) -> Unit,
    setNoteText: (noteText: String) -> Unit,
    setGrade: (emoji: String) -> Unit,
    startCamera: (context: Context, lifecycleOwner: LifecycleOwner, previewView: PreviewView) -> Unit,
    capturePhoto: (context: Context, onImageCaptured: (String?) -> Unit) -> Unit,
    encodeFileToBase64: (filePath: String) -> String?,
    updateMinute: (minute: MinuteUpdate) -> Unit,
    navigateBack: () -> Unit,
    setCurrentRoom: (roomIndex: Int) -> Unit,
    setCurrentMinute: (minuteIndex: Int) -> Unit,
    getInventoryData: () -> Unit,
    addPhoto: (path: String) -> Unit,
    navigateToSignPage: (token: String, type: String, inventoryId: Int) -> Unit
) {
    LaunchedEffect(state.token) {
        getInventoryData()
    }

    Log.i("InventoryPage", "state: $state")
    val scrollState = rememberScrollState()
    var cardIndex by remember { mutableStateOf(0) }
    val titles = mutableListOf<String>()
    val images = mutableListOf<List<String>>()

    if (state.currentRoom != null) {
        state.rooms[state.currentRoom].minutes.forEach { minute: Minute ->
            titles.add("Etat de ${minute.elementType}")
            images.add(minute.photos)
        }
    }

    val context = LocalContext.current

    // State for menu visibility
    var isMenuVisible by remember { mutableStateOf(false) }

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
                progress = state.progress.toFloat(),
                color = Color(0xFF8BC83F),
                trackColor = Color(0xFFEBF3F2),
                modifier = Modifier
                    .width(500.dp)
                    .height(20.dp)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(35.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))

            var roomname = ""
            if (state.currentRoom != null) {
                roomname = state.rooms[state.currentRoom].description
            }

            Text(
                text = roomname,
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
                setGrade,
                setNoteText,
            ) { newIndex ->
                if (newIndex >= titles.size) {
                    Log.i(
                        "InventoryPage",
                        "affecte la pièce courante à ${state.rooms[state.currentRoom?.plus(1)!!]}"
                    )

                    if (state.currentRoom!! < state.rooms.size - 1) {
                        setCurrentRoom(state.currentRoom?.plus(1) ?: 0)
                        cardIndex = 0
                    }
                } else {
                    cardIndex = newIndex % titles.size
                }
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
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xFF8BC83F)),
                            startY = 0f,
                            endY = 1000f
                        )
                    )
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
                        // Ajoute la photo dans le state rooms
                        if (uri != null) {
                            addPhoto(uri)
                        }
                    }
                })
            }
        }

        // Menu icon
        Icon(
            painter = painterResource(id = R.drawable.ic_menu),
            contentDescription = "Menu",
            modifier = Modifier
                .size(70.dp)
                .padding(26.dp)
                .clickable { isMenuVisible = true }
                .align(Alignment.TopStart)
        )

        // Slide-out menu
        AnimatedVisibility(
            visible = isMenuVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxHeight()
                        .background(Color.White)
                        .padding(16.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                            .clickable { isMenuVisible = false }
                            .align(Alignment.Start)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center)
                        )
                    }
                    Text(
                        text = "Résumé",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Progrès: ${state.progress}",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    // Dynamically create menu items based on rooms
                    state.rooms.forEach { room ->
                        val isSelected = state.rooms.indexOf(room) == state.currentRoom
                        MenuItem(
                            roomName = room.description,
                            isSelected = isSelected,
                            onClick = {
                                Log.i("InventoryPage", "Changement de pièce: $room")
                                setCurrentRoom(state.rooms.indexOf(room))
                                isMenuVisible = false
                            }
                        )
                    }
                    if (state.progress >= 100) {
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = { navigateToSignPage(state.token, "LOCATAIRE", state.inventoryId) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Signer")
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { navigateBack() }, // Use navigateBack function
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC8473F)),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Quitter")
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItem(roomName: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF8BC83F) else Color(0xFF234F68),
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(roomName)
    }
}

