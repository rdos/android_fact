package ru.smartro.worknote.utils

import android.util.SparseArray
import androidx.core.util.set

/**
 * [resolver] should returns [Pair] that contains first and second key values of [MODEL] for [load].
 */
class Local2KeysArrayCache<MODEL>(private val resolver: (MODEL) -> Pair<Int, Int>) {
    private val store = SparseArray<ListableSparseArray<MODEL>>()

    fun getAllByFirstKey(firstKey: Int): List<MODEL> {
        return store[firstKey]?.asList() ?: emptyList()
    }

    fun set(firstKey: Int, secondKey: Int, model: MODEL) {
        if (store.get(firstKey) == null) {
            store[firstKey] = ListableSparseArray()
        }
        store[firstKey][secondKey] = model
    }


    fun load(data: List<MODEL>) {
        store.clear()
        data.forEach {
            val (firstKey, secondKey) = resolver(it)
            set(firstKey = firstKey, secondKey = secondKey, model = it)
        }
    }
}