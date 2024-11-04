package ru.needkirem.yacupdraw.drawsurface

interface DrawActions {
    fun drawOnMainCanvas(elements: List<DrawElement>)
    fun clearMainCanvas()
    fun drawOnPhantomCanvas(elements: List<DrawElement>)
    fun clearPhantomCanvas()
}