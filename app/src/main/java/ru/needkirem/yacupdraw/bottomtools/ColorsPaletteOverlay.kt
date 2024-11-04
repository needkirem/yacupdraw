package ru.needkirem.yacupdraw.bottomtools

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.needkirem.yacupdraw.R
import ru.needkirem.yacupdraw.overlay.OverlayHolder

@Composable
fun ColorsPaletteOverlay(viewModel: BottomToolsViewModel) {
    val interactionSource = remember { MutableInteractionSource() }
    var isDynamicPaletteVisible by remember { mutableStateOf(false) }
    val colors = listOf(
        colorResource(R.color.white),
        colorResource(R.color.red),
        colorResource(R.color.soft_black),
        colorResource(R.color.blue),
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .clickable(interactionSource, null) {
            viewModel.setMiniPaletteVisible(false)
        }
    ) {
        OverlayHolder(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp, start = 16.dp, end = 16.dp),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            isDynamicPaletteVisible = !isDynamicPaletteVisible
                        },
                    tint = Color.White,
                    painter = painterResource(R.drawable.ic_palette),
                    contentDescription = null,
                )
                for (color in colors) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(color, CircleShape)
                            .clickable(interactionSource, null) {
                                viewModel.selectColor(color)
                                viewModel.setMiniPaletteVisible(false)
                            }
                    )
                }
            }
        }
        if (isDynamicPaletteVisible) {
            OverlayHolder(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 136.dp, start = 16.dp, end = 16.dp),
            ) {
                Box(Modifier.padding(16.dp)) {
                    DynamicColorsPicker(viewModel)
                }
            }
        }
    }
}
