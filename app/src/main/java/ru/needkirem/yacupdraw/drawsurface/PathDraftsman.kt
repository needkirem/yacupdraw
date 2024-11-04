package ru.needkirem.yacupdraw.drawsurface

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

private val DrawScope.PencilStyle: Stroke
    get() = Stroke(width = DrawTool.PENCIL.toolWidth.dp.toPx(), miter = 1F, cap = StrokeCap.Round)

private val DrawScope.BrushStyle: Stroke
    get() = Stroke(width = DrawTool.BRUSH.toolWidth.dp.toPx(), miter = 1F, cap = StrokeCap.Round)

fun DrawScope.drawPath(
    drawElement: DrawElement,
    asSnapshot: Boolean = false,
) {
    drawElement.paths.forEach { path ->
        drawPath(
            path = path,
            color = drawElement.color,
            tool = drawElement.tool,
            asSnapshot = asSnapshot,
        )
    }
}

private fun DrawScope.drawPath(path: Path, color: Color, tool: DrawTool, asSnapshot: Boolean) {
    val pathColor = if (asSnapshot) color.snapShotColor() else color
    val blendMode = if (asSnapshot) BlendMode.Multiply else BlendMode.SrcOver
    when (tool) {
        DrawTool.PENCIL -> {
            drawPath(
                path = path,
                color = pathColor,
                style = PencilStyle,
                blendMode = blendMode,
            )
        }

        DrawTool.ERASER -> {
            drawPath(
                path = path,
                color = Color.Transparent,
                style = BrushStyle,
                blendMode = BlendMode.Clear,
            )
        }

        DrawTool.BRUSH -> {
            drawPath(
                path = path,
                color = pathColor,
                style = BrushStyle,
                blendMode = blendMode,
            )
        }
    }
}

private fun Color.snapShotColor() = copy(alpha = 0.005F)
