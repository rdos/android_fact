package ru.smartro.worknote.ac

interface ITooltip {
    fun getTooltipType(): Int
    fun getTooltipNext(): String?
    fun getIdText(): String
}