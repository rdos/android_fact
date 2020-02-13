package ru.smartro.worknote.domain.handlers.workflow.states

sealed class State {
    object Created : State() //машина создана но требует инициализации из базы данных
    object Ready : State() //машина готова к работе, смена не запущена
    object SetCommonDataInProgress :
        State() //машина в процессе заполнения основными данными, смена не запущена

    object ShiftStarted : State() //смена запущена
}