package ru.needkirem.yacupdraw.drawsurface

import androidx.compose.ui.geometry.Size

data class DrawSnapshot(
    val elements: List<DrawElement>,
    val snapshotSize: Size,
)