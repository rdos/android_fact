package ru.smartro.worknote.domain.handlers.workflow.events

sealed class Event {
    object OnUpdate : Event() //при взятии данных из базы
    object OnSetCommonData : Event() //при добавлении начальных данных
    object OnShiftStart : Event() //при запуске смены
    object OnShiftFinish : Event() //при завершении смены
}