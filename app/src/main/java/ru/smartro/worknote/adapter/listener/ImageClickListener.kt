package ru.smartro.worknote.adapter.listener

interface ImageClickListener {
    fun imageDetailClicked(imageBase64: String)
    fun imageRemoveClicked(imageBase64: String)
}