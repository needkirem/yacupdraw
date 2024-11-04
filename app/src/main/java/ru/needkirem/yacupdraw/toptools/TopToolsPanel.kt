package ru.needkirem.yacupdraw.toptools

import androidx.annotation.DrawableRes
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.needkirem.yacupdraw.R
import ru.needkirem.yacupdraw.drawsurface.DrawViewModel

@Composable
fun TopToolsPanel(
    viewModel: DrawViewModel,
) {
    val tools = remember { viewModel.disabledTopTools.stateList }
    val animationSpeed = viewModel.animationSpeed.collectAsState()
    IconsContainer {
        IconsGroup {
            ToolIcon(R.drawable.ic_back, !tools.contains(TopTool.BACK), viewModel::onBackClick)
            ToolIcon(
                R.drawable.ic_forward,
                !tools.contains(TopTool.FORWARD),
                viewModel::onForwardClick
            )
        }
        IconsGroup {
            ToolIcon(R.drawable.ic_bin, !tools.contains(TopTool.BIN), viewModel::onBinClick)
            ToolIcon(
                R.drawable.ic_capture_state,
                !tools.contains(TopTool.NEW_LAYER),
                viewModel::onCaptureClick
            )
            ToolIcon(
                R.drawable.ic_layers,
                !tools.contains(TopTool.LAYERS)
            ) { viewModel.setLayersOverlayVisibility(true) }
        }
        IconsGroup {
            ToolIcon(R.drawable.ic_pause, !tools.contains(TopTool.PAUSE), viewModel::onPauseClick)
            ToolIcon(R.drawable.ic_play, !tools.contains(TopTool.PLAY), viewModel::onPlayClick)
            Text(
                modifier = Modifier.size(32.dp).wrapContentSize(Alignment.Center).clickable {
                    viewModel.onSpeedClick()
                },
                textAlign = TextAlign.Center,
                text = "${animationSpeed.value}x",
                color = Color.White,
            )
        }
    }
}

@Composable
private fun IconsContainer(content: @Composable RowScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            content()
        }
    }
}

@Composable
private fun IconsGroup(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        content()
    }
}

@Composable
fun ToolIcon(
    @DrawableRes resId: Int,
    isIconEnabled: Boolean,
    clickAction: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Icon(
        modifier = Modifier
            .size(32.dp)
            .clickable(
                indication = LocalIndication.current.takeIf { isIconEnabled },
                interactionSource = interactionSource
            ) {
                if (isIconEnabled) {
                    clickAction()
                }
            },
        tint = if (isIconEnabled) Color.White else Color.Gray,
        painter = painterResource(resId),
        contentDescription = null,
    )
}