package ru.needkirem.yacupdraw.toptools

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.needkirem.yacupdraw.R
import ru.needkirem.yacupdraw.drawsurface.DrawSnapshot
import ru.needkirem.yacupdraw.drawsurface.DrawTool
import ru.needkirem.yacupdraw.drawsurface.DrawViewModel
import ru.needkirem.yacupdraw.overlay.OverlayHolder
import ru.needkirem.yacupdraw.utils.draw

@Composable
fun CanvasPreviewOverlay(viewModel: DrawViewModel) {
    val interactionSource = remember { MutableInteractionSource() }
    val snapshots = viewModel.previewSnapshots
    Box(modifier = Modifier
        .fillMaxSize()
        .clickable(interactionSource, null) {
            viewModel.setLayersOverlayVisibility(false)
        }
    ) {
        OverlayHolder(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 30.dp, vertical = 100.dp)
        ) {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 54.dp)
                    .fillMaxHeight(),
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(snapshots) { snapshot ->
                    CanvasItem(snapshot, onRemoveClick = {
                        viewModel.onRemoveSnapshotClick(snapshot)
                        viewModel.setLayersOverlayVisibility(false)
                    }) {
                        viewModel.onPreviewSnapshotClick(snapshot)
                        viewModel.setLayersOverlayVisibility(false)
                    }
                }
            }
            Button(
                onClick = {
                    viewModel.onClearAllSnapshotsClick()
                    viewModel.setLayersOverlayVisibility(false)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.blue),
                    contentColor = colorResource(R.color.white),
                )
            ) {
                Text(stringResource(R.string.remove_all_previews))
            }
        }
    }
}

@Composable
private fun CanvasItem(snapshot: DrawSnapshot, onRemoveClick: () -> Unit, onClick: () -> Unit) {
    var imageBitmap by remember(snapshot) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(snapshot) {
        imageBitmap = withContext(Dispatchers.Default) {
            val bitmap = Bitmap.createBitmap(
                snapshot.snapshotSize.width.toInt(),
                snapshot.snapshotSize.height.toInt(),
                Bitmap.Config.ARGB_8888,
            ).apply {
                eraseColor(Color.White.toArgb())
            }
            val canvas = Canvas(bitmap)
            snapshot.elements.forEach { element ->
                val paint = Paint().apply {
                    color = if (element.tool != DrawTool.ERASER) {
                        element.color.toArgb()
                    } else {
                        Color.White.toArgb()
                    }
                    strokeWidth = element.tool.toolWidth.toFloat()
                    style = Paint.Style.STROKE
                }
                element.paths.forEach {
                    canvas.draw(it.asAndroidPath(), paint)
                }
            }

            val scale = 0.3f
            val matrix = Matrix().apply { setScale(scale, scale) }

            val reducedBitmap = Bitmap.createBitmap(
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                Bitmap.Config.ARGB_8888
            )

            val reducedCanvas = Canvas(reducedBitmap)
            reducedCanvas.drawBitmap(bitmap, matrix, null)

            reducedBitmap.asImageBitmap()
        }
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        imageBitmap?.let {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                bitmap = it,
                contentDescription = null,
            )
            Icon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .clickable { onRemoveClick() },
                painter = painterResource(R.drawable.ic_bin),
                tint = Color.Black,
                contentDescription = null,
            )
        }
    }
}