package ru.smartro.worknote.ac

import ru.smartro.worknote.Fnull
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.AA


class FloatCool(valueName: String? = null): AbsObject(valueName) {

    constructor(valueName: String, app: AA) : this(valueName)
    var LET: Float = Fnull

    fun setDATAing(float: Float) {
        LOGWork(float.toString())
        LET = float
    }

}

 
