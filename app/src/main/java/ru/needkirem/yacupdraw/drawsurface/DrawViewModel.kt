package ru.needkirem.yacupdraw.drawsurface

import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.needkirem.yacupdraw.toptools.TopTool
import ru.needkirem.yacupdraw.utils.MutableStateSet

class DrawViewModel : ViewModel() {
    private var listener: DrawActions? = null
    private var snapshots: MutableList<DrawSnapshot> = mutableListOf()
    private var lastSnapshot = DrawSnapshot(emptyList(), Size.Unspecified)
    private val removedDrawElements: MutableList<DrawElement> = mutableListOf()

    private var playingJob: Job? = null
    private var previewEditSnapshot: DrawSnapshot? = null

    val previewSnapshots: MutableList<DrawSnapshot> = mutableListOf()
    val disabledTopTools by MutableStateSet(TopTool.PLAY, TopTool.PAUSE, TopTool.LAYERS)
    val isSnapshotsPreviewVisible = MutableStateFlow(false)
    val isPreviewEditButtonsVisible = MutableStateFlow(false)
    val isBottomToolsEnabled = MutableStateFlow(true)
    val animationSpeed = MutableStateFlow(1)

    fun setListener(listener: DrawActions) {
        this.listener = listener
    }

    fun saveDrawState(elements: List<DrawElement>, size: Size) {
        lastSnapshot = DrawSnapshot(elements, size)
        recalculateScreenState()
    }

    private fun recalculateScreenState() {
        if (snapshots.isEmpty()) {
            disabledTopTools.put(TopTool.LAYERS, TopTool.PLAY)
        } else {
            disabledTopTools.remove(TopTool.LAYERS, TopTool.PLAY)
        }
    }

    private fun clearAndDrawOnMain(elements: List<DrawElement>) {
        safeClearMainCanvas()
        listener?.drawOnMainCanvas(elements)
    }

    fun onBackClick() {
        if (lastSnapshot.elements.isEmpty()) {
            return
        }
        val snapshotElements = lastSnapshot.elements.toMutableList()
        val elementForDrop = snapshotElements.removeAt(snapshotElements.lastIndex)
        lastSnapshot = lastSnapshot.copy(elements = snapshotElements)
        clearAndDrawOnMain(snapshotElements)
        removedDrawElements.add(elementForDrop)
    }

    fun onForwardClick() {
        if (removedDrawElements.isNotEmpty()) {
            val snapshotElements = lastSnapshot.elements.toMutableList()
            val elementForRestore = removedDrawElements.removeAt(removedDrawElements.lastIndex)
            lastSnapshot = lastSnapshot.copy(elements = snapshotElements + elementForRestore)
            clearAndDrawOnMain(lastSnapshot.elements)
        }
    }

    fun onBinClick() {
        removedDrawElements.addAll(lastSnapshot.elements)
        safeClearMainCanvas()
        lastSnapshot = DrawSnapshot(emptyList(), Size.Unspecified)
        recalculateScreenState()
    }

    fun onCaptureClick() {
        if (lastSnapshot.elements.isNotEmpty()) {
            snapshots.add(lastSnapshot)
            listener?.clearPhantomCanvas()
            listener?.drawOnPhantomCanvas(lastSnapshot.elements)
            safeClearMainCanvas()
        }
        recalculateScreenState()
    }

    fun onPlayClick() {
        safeClearMainCanvas()
        listener?.clearPhantomCanvas()
        disabledTopTools.put(
            TopTool.PLAY,
            TopTool.LAYERS,
            TopTool.NEW_LAYER,
            TopTool.BIN,
            TopTool.BACK,
            TopTool.FORWARD
        ).remove(TopTool.PAUSE)
        playingJob = viewModelScope.launch {
            isBottomToolsEnabled.emit(false)
            while (true) {
                snapshots.forEach { snapshot ->
                    withContext(Dispatchers.Main) {
                        clearAndDrawOnMain(snapshot.elements)
                    }
                    delay((1000 / animationSpeed.value).toLong())
                }
            }
        }
    }

    fun onPauseClick() {
        playingJob?.cancel()
        disabledTopTools.put(TopTool.PAUSE).remove(
            TopTool.PLAY,
            TopTool.LAYERS,
            TopTool.NEW_LAYER,
            TopTool.BIN,
            TopTool.BACK,
            TopTool.FORWARD
        )
        viewModelScope.launch { isBottomToolsEnabled.emit(true) }
        recalculateScreenState()
        // Отрисуем последний кадр
        safeClearMainCanvas()
        snapshots.lastOrNull()?.let {
            listener?.drawOnPhantomCanvas(it.elements)
        }
    }

    fun setLayersOverlayVisibility(isVisible: Boolean) {
        viewModelScope.launch {
            previewSnapshots.clear()
            previewSnapshots.addAll(snapshots)
            isSnapshotsPreviewVisible.emit(isVisible)
        }
    }

    fun onRemoveSnapshotClick(drawSnapshot: DrawSnapshot) {
        snapshots.removeIf { it == drawSnapshot }
        listener?.clearPhantomCanvas()
        recalculateScreenState()
    }

    fun onPreviewSnapshotClick(drawSnapshot: DrawSnapshot) {
        lastSnapshot = DrawSnapshot(emptyList(), Size.Unspecified)
        previewEditSnapshot = drawSnapshot
        disabledTopTools.put(TopTool.NEW_LAYER, TopTool.PLAY, TopTool.LAYERS)
        listener?.clearPhantomCanvas()
        clearAndDrawOnMain(drawSnapshot.elements)
        viewModelScope.launch { isPreviewEditButtonsVisible.emit(true) }
    }

    fun onPreviewSaveClick() {
        val snapShotForReplace = lastSnapshot
        snapshots = snapshots.map { snapshot ->
            if (snapshot == previewEditSnapshot) {
                snapShotForReplace
            } else {
                snapshot
            }
        }.toMutableList()
        onPreviewCancelClick()
    }

    fun onPreviewCancelClick() {
        previewEditSnapshot = null
        recalculateScreenState()
        safeClearMainCanvas()
        disabledTopTools.remove(TopTool.NEW_LAYER, TopTool.PLAY, TopTool.LAYERS)
        viewModelScope.launch { isPreviewEditButtonsVisible.emit(false) }
    }

    fun onClearAllSnapshotsClick() {
        lastSnapshot = DrawSnapshot(emptyList(), Size.Unspecified)
        snapshots = mutableListOf()
        listener?.clearPhantomCanvas()
        safeClearMainCanvas()
        recalculateScreenState()
    }

    fun onSpeedClick() {
        viewModelScope.launch {
            val newSpeed = animationSpeed.value + 1
            if (newSpeed > 10) {
                animationSpeed.emit(1)
            } else {
                animationSpeed.emit(newSpeed)
            }
        }
    }

    private fun safeClearMainCanvas() {
        snapshots = snapshots.map {
            DrawSnapshot(it.elements.map { it.copy() }, it.snapshotSize)
        }.toMutableList()
        listener?.clearMainCanvas()
    }
}