package ru.smartro.worknote.andPOintD

import ru.smartro.worknote.Fnull
import ru.smartro.worknote.TAG
import ru.smartro.worknote.abs.AbsObject
import ru.smartro.worknote.log.AApp


class FloatCool(tagName: String? = null, valueName: String? = null): AbsObject(tagName, valueName) {

    constructor(valueName: String, app: AApp) : this(app.TAG, valueName)
    var LET: Float = Fnull

    fun setDATAing(float: Float) {
        LOGWork(float.toString())
        LET = float
    }

}

 
