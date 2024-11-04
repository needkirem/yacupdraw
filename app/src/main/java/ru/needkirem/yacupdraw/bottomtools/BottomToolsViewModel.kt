package ru.needkirem.yacupdraw.bottomtools

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.needkirem.yacupdraw.drawsurface.DrawTool

class BottomToolsViewModel : ViewModel() {
    val currentTool = MutableStateFlow(DrawTool.PENCIL)
    val currentColor = MutableStateFlow(Color(0xFF1976D2))
    val isMiniPaletteVisible = MutableStateFlow(false)

    fun selectTool(tool: DrawTool) {
        viewModelScope.launch {
            currentTool.emit(tool)
        }
    }

    fun selectColor(color: Color) {
        viewModelScope.launch {
            currentColor.emit(color)
        }
    }

    fun setMiniPaletteVisible(isVisible: Boolean) {
        viewModelScope.launch {
            isMiniPaletteVisible.emit(isVisible)
        }
    }
}