package ru.smartro.worknote.domain.handlers.workflow.sideEffects

sealed class SideEffect {
    object InsertToDb : SideEffect()
}