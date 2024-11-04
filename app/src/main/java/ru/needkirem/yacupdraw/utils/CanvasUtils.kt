package ru.needkirem.yacupdraw.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas

fun Canvas.draw(path: Path, paint: Paint) {
    val count = save()
    drawPath(path, paint)
    restoreToCount(count)
}

fun DrawScope.draw(content: () -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val savedLayer = saveLayer(null, null)
        content()
        restoreToCount(savedLayer)
    }
}