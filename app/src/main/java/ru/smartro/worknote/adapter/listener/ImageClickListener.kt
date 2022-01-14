package ru.smartro.worknote.adapter.listener

import ru.smartro.worknote.work.ImageEntity

interface ImageClickListener {
    fun imageDetailClicked(imageBase64: ImageEntity)
    fun imageRemoveClicked(imageBase64: ImageEntity)
}