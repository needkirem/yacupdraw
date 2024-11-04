package ru.needkirem.yacupdraw.drawsurface

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

data class DrawElement(
    val paths: List<Path>,
    val color: Color,
    val tool: DrawTool,
) {

    companion object {
        fun empty() = DrawElement(emptyList(), Color.Unspecified, DrawTool.PENCIL)
    }
}