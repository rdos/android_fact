package ru.smartro.worknote.andPOintD

import ru.smartro.worknote.Fnull
import ru.smartro.worknote.TAG
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.log.AApp


class FloatCool(valueName: String? = null): AbsObject(valueName) {

    constructor(valueName: String, app: AApp) : this(valueName)
    var LET: Float = Fnull

    fun setDATAing(float: Float) {
        LOGWork(float.toString())
        LET = float
    }

}

 
