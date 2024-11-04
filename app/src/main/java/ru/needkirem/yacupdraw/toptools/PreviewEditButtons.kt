package ru.needkirem.yacupdraw.toptools

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.needkirem.yacupdraw.drawsurface.DrawViewModel
import ru.needkirem.yacupdraw.overlay.OverlayHolder

@Composable
fun PreviewEditButtons(viewModel: DrawViewModel) {
    OverlayHolder(
        modifier = Modifier
            .wrapContentWidth()
            .padding(start = 30.dp, top = 90.dp)
    ) {
        Row(modifier = Modifier.align(Alignment.Center)) {
            IconButton(onClick = {
                viewModel.onPreviewCancelClick()
            }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    tint = Color.White,
                    contentDescription = "Exit "
                )
            }
            IconButton(onClick = {
                viewModel.onPreviewSaveClick()
            }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    tint = Color.White,
                    contentDescription = "Accept preview edit"
                )
            }
        }
    }
}