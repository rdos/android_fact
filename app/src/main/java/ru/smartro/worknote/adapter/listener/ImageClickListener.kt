package ru.smartro.worknote.adapter.listener

interface ImageClickListener {
    fun imageDetailClicked(photoPath: String)
    fun imageRemoveClicked(photoPath: String)
}