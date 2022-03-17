package ru.smartro.worknote.abs

import android.util.Log
import ru.smartro.worknote.App

abstract class AbsObject(tagName: String?, val valueName: String?) {
    protected var TAGObject : String = tagName?:valueName?: "${this::class.simpleName}"


    protected fun LOGWork(value: String) {
        val method =  App.getMethodMan()
        method?.let {
            valueName?.let {
                Log.i(TAGObject, "${method}.${valueName}=${value}")
                return@LOGWork
            }
            Log.i(TAGObject, "${method}.valueName=${value}")
            return@LOGWork
        }
        Log.i(TAGObject, "${TAGObject}:.valueName=${value}")
    }

}
