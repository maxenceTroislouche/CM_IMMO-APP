package com.cm_immo_app.view.page

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cm_immo_app.viewmodel.SignatureViewModel

@Composable
fun SignaturePad(viewModel: SignatureViewModel) {
    val context = LocalContext.current
    var points by remember { mutableStateOf(mutableListOf<Offset>()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        points.add(offset)
                    },
                    onDrag = { change, _ ->
                        points.add(change.position)
                        change.consume()
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val paint = androidx.compose.ui.graphics.Paint().apply {
                color = Color.Black
                strokeWidth = 4f
                style = PaintingStyle.Stroke
            }
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = paint.color,
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = paint.strokeWidth
                )
            }
        }
        Button(
            onClick = {
                val bitmap = Bitmap.createBitmap(
                    context.resources.displayMetrics.widthPixels,
                    context.resources.displayMetrics.heightPixels,
                    Bitmap.Config.ARGB_8888
                )
                val androidCanvas = android.graphics.Canvas(bitmap)
                androidCanvas.drawColor(android.graphics.Color.WHITE) // Ensure the background is white
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    strokeWidth = 4f
                    style = android.graphics.Paint.Style.STROKE
                }
                for (i in 0 until points.size - 1) {
                    androidCanvas.drawLine(
                        points[i].x,
                        points[i].y,
                        points[i + 1].x,
                        points[i + 1].y,
                        paint
                    )
                }
                viewModel.saveSignature(bitmap, context) { uri ->
                    // Handle the saved signature URI here if needed
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) {
            Text("Enregistrer")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignaturePage() {
    val viewModel: SignatureViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Signature") }
            )
        },
        content = { padding ->
            Box(modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F4F8))
            ) {
                SignaturePad(viewModel)
            }
        }
    )
}
