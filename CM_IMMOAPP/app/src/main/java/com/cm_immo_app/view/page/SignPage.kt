package com.cm_immo_app.view.page

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.applyCanvas
import com.cm_immo_app.state.SignState

/**
 * Code original:
 * https://www.youtube.com/watch?v=xdebBD47wTY
 */

data class Line(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.Black,
    val strokeWidth: Dp = 1.dp
)

@Composable
fun SignaturePad(
    state: SignState,
    saveSignature: (bitmap: Bitmap, context: Context, onSaved: (String?) -> Unit) -> Unit,
    navigateToSignPage: (token: String, type: String, inventoryId: Int) -> Unit,
    navigateToPropertiesListPage: (token: String) -> Unit,
) {
    val context = LocalContext.current
    val lines = remember {
        mutableStateListOf<Line>()
    }
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color(0xFF8BC83F)),
        startY = 0f,
        endY = 400f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(true) {
                detectDragGestures { change, dragAmount ->
                    change.consume()

                    val line = Line(
                        start = change.position - dragAmount,
                        end = change.position
                    )

                    lines.add(line)
                }
            }
        ) {
            lines.forEach { line ->
                drawLine(
                    color = line.color,
                    start = line.start,
                    end = line.end,
                    strokeWidth = line.strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            }
            // Capture the canvas to a bitmap
            val bitmap = drawContext.canvas.nativeCanvas.let { canvas ->
                Bitmap.createBitmap(
                    canvas.width,
                    canvas.height,
                    Bitmap.Config.ARGB_8888
                ).applyCanvas {
                    drawColor(android.graphics.Color.WHITE)
                    lines.forEach { line ->
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            strokeWidth = line.strokeWidth.toPx()
                            style = android.graphics.Paint.Style.STROKE
                        }
                        drawLine(
                            line.start.x, line.start.y,
                            line.end.x, line.end.y, paint
                        )
                    }
                }
            }
            bitmapState.value = bitmap.asImageBitmap().asAndroidBitmap()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(gradient)
        ) {
            Button(
                onClick = {
                    bitmapState.value?.let { bitmap ->
                        saveSignature(bitmap, context) { uri ->
                            // Gérer l'URI de la signature enregistrée ici si nécessaire
                            Log.i("SignPage", "image canvas sauvegardée: $uri")


                            // Envoi de la signature via l'api

                            // Navigation vers la page de signature suivante OU page liste des biens
                            if (state.type == "LOCATAIRE") {
                                navigateToSignPage(
                                    state.token,
                                    "PROPRIETAIRE",
                                    state.inventoryId
                                )
                            } else if (state.type == "PROPRIETAIRE") {
                                navigateToSignPage(
                                    state.token,
                                    "AGENT",
                                    state.inventoryId
                                )
                            } else {
                                navigateToPropertiesListPage(state.token)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Valider la signature")
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignaturePage(
    state: SignState,
    saveSignature: (bitmap: Bitmap, context: Context, onSaved: (String?) -> Unit) -> Unit,
    navigateToSignPage: (token: String, type: String, inventoryId: Int) -> Unit,
    navigateToPropertiesListPage: (token: String) -> Unit,
) {

    var title = "Signature du locataire"
    if (state.type == "PROPRIETAIRE") {
        title = "Signature du propriétaire"
    } else if (state.type == "AGENT") {
        title = "Signature de l'agent"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
        content = { padding ->
            Box(modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F4F8))
            ) {
                SignaturePad(
                    state,
                    saveSignature,
                    navigateToSignPage,
                    navigateToPropertiesListPage,
                )
            }
        }
    )
}