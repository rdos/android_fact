package ru.smartro.worknote.adapter.listener

import ru.smartro.worknote.service.database.entity.work_order.ImageEntity

interface ImageClickListener {
    fun imageDetailClicked(imageBase64: ImageEntity)
    fun imageRemoveClicked(imageBase64: ImageEntity)
}