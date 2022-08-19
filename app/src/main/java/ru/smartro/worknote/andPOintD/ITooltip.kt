package ru.smartro.worknote.andPOintD

import android.view.View

interface ITooltip {
    fun getTooltipType(): Int
    fun getTooltipNext(): String?
    fun getIdText(): String
}