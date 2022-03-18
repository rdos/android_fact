package ru.smartro.worknote.abs

import ru.smartro.worknote.App
import ru.smartro.worknote.Fnull
import ru.smartro.worknote.log.AApp

class FloatCool(tagName: String? = null, valueName: String? = null): AbsObject(tagName, valueName) {

    constructor(valueName: String, app: AApp) : this(app.TAG, valueName)
    var VAL: Float = Fnull

    fun setDATAing(float: Float) {
        LOGWork(float.toString())
        VAL = float
    }

}

 
