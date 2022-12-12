package ru.smartro.worknote.presentation.ac

import ru.smartro.worknote.Snull

open class RPCBody<T> (
    var type: String = Snull,
    var payload: T? = null
) : NetObject()
