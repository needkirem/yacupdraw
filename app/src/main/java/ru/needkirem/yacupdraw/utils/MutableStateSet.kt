package ru.needkirem.yacupdraw.utils

import androidx.compose.runtime.mutableStateListOf
import kotlin.reflect.KProperty

class MutableStateSet<T>(vararg elements: T) {
    private val values = mutableStateListOf(*elements)

    val stateList
        get() = values

    operator fun getValue(thisRef: Any?, property: KProperty<*>): MutableStateSet<T> {
        return this
    }

    fun put(value: T): MutableStateSet<T> {
        if (!values.contains(value)) {
            values.add(value)
        }
        return this
    }

    fun put(vararg values: T): MutableStateSet<T> {
        values.forEach { put(it) }
        return this
    }

    fun remove(value: T): MutableStateSet<T> {
        values.remove(value)
        return this
    }

    fun remove(vararg values: T): MutableStateSet<T> {
        values.forEach { remove(it) }
        return this
    }
}
