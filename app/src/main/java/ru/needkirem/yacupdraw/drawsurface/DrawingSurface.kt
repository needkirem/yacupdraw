package ru.needkirem.yacupdraw.drawsurface

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import ru.needkirem.yacupdraw.R
import ru.needkirem.yacupdraw.bottomtools.BottomToolsViewModel
import ru.needkirem.yacupdraw.utils.draw

@Composable
fun DrawingSurface(
    drawViewModel: DrawViewModel,
    toolViewModel: BottomToolsViewModel,
) {
    val currentColor = toolViewModel.currentColor.collectAsState()
    val currentTool = toolViewModel.currentTool.collectAsState()
    val isBottomToolsEnabled = drawViewModel.isBottomToolsEnabled.collectAsState()

    val drawElements = remember { mutableStateListOf<DrawElement>() }
    val snapshotElements = remember { mutableStateListOf<DrawElement>() }
    var currentDrawElement by remember { mutableStateOf(DrawElement.empty()) }

    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    drawViewModel.setListener(object : DrawActions {
        override fun drawOnMainCanvas(elements: List<DrawElement>) {
            drawElements.addAll(elements)
        }

        override fun clearMainCanvas() {
            drawElements.clear()
        }

        override fun drawOnPhantomCanvas(elements: List<DrawElement>) {
            snapshotElements.addAll(elements)
        }

        override fun clearPhantomCanvas() {
            snapshotElements.clear()
        }
    })

    Box(
        modifier = Modifier
            .fillMaxHeight(fraction = 0.9F)
            .padding(10.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.draw_background),
            contentDescription = "Canvas background image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        val canvasModifier = Modifier
            .matchParentSize()
            .clipToBounds()
            .clip(RoundedCornerShape(16.dp))

        // Phantom canvas
        Canvas(modifier = canvasModifier) {
            draw {
                snapshotElements.forEach { drawPath(it, asSnapshot = true) }
            }
        }

        // Main canvas
        Canvas(
            modifier = canvasModifier
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            if (!isBottomToolsEnabled.value) {
                                return@detectDragGestures
                            }
                            if (offset.isWithinBounds(size)) {
                                val newPath = Path()
                                newPath.moveTo(offset.x, offset.y)
                                currentDrawElement = currentDrawElement.copy(
                                    paths = currentDrawElement.paths + newPath,
                                    color = currentColor.value,
                                    tool = currentTool.value,
                                )
                                currentPosition = offset
                            }
                        },
                        onDrag = { _, dragAmount ->
                            if (!isBottomToolsEnabled.value) {
                                return@detectDragGestures
                            }

                            currentPosition = Offset(
                                x = currentPosition.x + dragAmount.x,
                                y = currentPosition.y + dragAmount.y
                            )

                            if (currentPosition.isWithinBounds(size)) {
                                val pathContinuation = currentDrawElement.paths.last()
                                pathContinuation.lineTo(
                                    x = currentPosition.x + dragAmount.x,
                                    y = currentPosition.y + dragAmount.y
                                )
                                currentDrawElement = currentDrawElement.copy(
                                    paths = currentDrawElement.paths + pathContinuation,
                                )

                                previousPosition = currentPosition
                            }
                        },
                        onDragEnd = {
                            if (!isBottomToolsEnabled.value) {
                                return@detectDragGestures
                            }

                            drawElements.add(currentDrawElement)
                            drawViewModel.saveDrawState(drawElements, size.toSize())
                            currentDrawElement = DrawElement.empty()
                        },
                    )
                }
        ) {
            draw {
                drawElements.forEach(::drawPath)
                drawPath(currentDrawElement)
            }
        }
    }
}

private fun Offset.isWithinBounds(canvasSize: IntSize): Boolean {
    return this.x >= 0 && this.x <= canvasSize.width &&
            this.y >= 0 && this.y <= canvasSize.height
}