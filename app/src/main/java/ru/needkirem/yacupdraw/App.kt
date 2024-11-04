package ru.needkirem.yacupdraw

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.needkirem.yacupdraw.bottomtools.BottomToolsPanel
import ru.needkirem.yacupdraw.bottomtools.BottomToolsViewModel
import ru.needkirem.yacupdraw.bottomtools.ColorsPaletteOverlay
import ru.needkirem.yacupdraw.drawsurface.DrawViewModel
import ru.needkirem.yacupdraw.drawsurface.DrawingSurface
import ru.needkirem.yacupdraw.toptools.CanvasPreviewOverlay
import ru.needkirem.yacupdraw.toptools.PreviewEditButtons
import ru.needkirem.yacupdraw.toptools.TopToolsPanel

@Composable
fun App(
    drawViewModel: DrawViewModel = viewModel(),
    bottomToolsViewModel: BottomToolsViewModel = viewModel(),
) {
    val isMiniPaletteVisible = bottomToolsViewModel.isMiniPaletteVisible.collectAsState()
    val isSnapshotsPreviewVisible = drawViewModel.isSnapshotsPreviewVisible.collectAsState()
    val isPreviewEditButtonsVisible = drawViewModel.isPreviewEditButtonsVisible.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black),
            verticalArrangement = Arrangement.Center,
        ) {
            TopToolsPanel(drawViewModel)
            DrawingSurface(drawViewModel, bottomToolsViewModel)
            BottomToolsPanel(drawViewModel, bottomToolsViewModel)
        }
        if (isMiniPaletteVisible.value) {
            ColorsPaletteOverlay(bottomToolsViewModel)
        }
        if (isSnapshotsPreviewVisible.value) {
            CanvasPreviewOverlay(drawViewModel)
        }
        if (isPreviewEditButtonsVisible.value) {
            PreviewEditButtons(drawViewModel)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    App()
}
