package ru.smartro.worknote.work.abs

import ru.smartro.worknote.App
import ru.smartro.worknote.Fnull

class FloatCool(tagName: String? = null, valueName: String? = null): AbsObject(tagName, valueName) {

    constructor(valueName: String, app: App) : this(app.TAG, valueName)
    private var mFloat: Float = Fnull

    fun setDATAing(float: Float) {
        LOGWork(float.toString())
        mFloat = float
    }

}

 
