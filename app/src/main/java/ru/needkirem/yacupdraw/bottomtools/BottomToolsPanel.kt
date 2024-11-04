package ru.needkirem.yacupdraw.bottomtools

import androidx.annotation.DrawableRes
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.needkirem.yacupdraw.R
import ru.needkirem.yacupdraw.drawsurface.DrawTool
import ru.needkirem.yacupdraw.drawsurface.DrawViewModel

@Composable
fun BottomToolsPanel(
    drawViewModel: DrawViewModel,
    toolViewModel: BottomToolsViewModel,
) {
    val currentColor = toolViewModel.currentColor.collectAsState()
    val isIconsEnabled = drawViewModel.isBottomToolsEnabled.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        DrawToolsPanel(
            currentColor = currentColor.value,
            isIconsEnabled = isIconsEnabled.value,
            onPencilClick = {
                toolViewModel.selectTool(DrawTool.PENCIL)
            },
            onBrushClick = {
                toolViewModel.selectTool(DrawTool.BRUSH)
            },
            onEraserClick = {
                toolViewModel.selectTool(DrawTool.ERASER)
            },
            onColorClick = {
                toolViewModel.setMiniPaletteVisible(true)
            }
        )
    }
}

@Composable
private fun DrawToolsPanel(
    currentColor: Color,
    isIconsEnabled: Boolean,
    onPencilClick: () -> Unit,
    onBrushClick: () -> Unit,
    onEraserClick: () -> Unit,
    onColorClick: () -> Unit,
) {
    var activatedIcon by remember { mutableStateOf(BottomTool.PENCIL) }
    val interactionSource = remember { MutableInteractionSource() }
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        ToolIcon(R.drawable.ic_pencil, isIconsEnabled, activatedIcon == BottomTool.PENCIL) {
            onPencilClick.invoke()
            activatedIcon = BottomTool.PENCIL
        }
        ToolIcon(R.drawable.ic_brush, isIconsEnabled, activatedIcon == BottomTool.BRUSH) {
            onBrushClick.invoke()
            activatedIcon = BottomTool.BRUSH
        }
        ToolIcon(R.drawable.ic_eraser, isIconsEnabled, activatedIcon == BottomTool.ERASER) {
            onEraserClick.invoke()
            activatedIcon = BottomTool.ERASER
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(currentColor, CircleShape)
                .clip(RoundedCornerShape(4.dp))
                .clickable(
                    indication = LocalIndication.current.takeIf { isIconsEnabled },
                    interactionSource = interactionSource,
                ) {
                    if (isIconsEnabled) {
                        onColorClick.invoke()
                    }
                }
        )
    }
}

@Composable
fun ToolIcon(
    @DrawableRes resId: Int,
    isIconEnabled: Boolean,
    isIconActivated: Boolean,
    clickAction: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Icon(
        modifier = Modifier.size(32.dp).clickable(
            indication = LocalIndication.current.takeIf { isIconEnabled },
            interactionSource = interactionSource,
        ) {
            if (isIconEnabled) {
                clickAction()
            }
        },
        tint = if (isIconEnabled) getSelectColor(isIconActivated) else Color.Gray,
        painter = painterResource(resId),
        contentDescription = null,
    )
}

@Composable
private fun getSelectColor(isActive: Boolean): Color {
    return if (isActive) {
        colorResource(R.color.icon_active)
    } else {
        Color.White
    }
}

private enum class BottomTool {
    PENCIL, BRUSH, ERASER,
}
