package ru.smartro.worknote.work.abs

import android.app.Application
import android.util.Log
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.util.MyUtil.toStr

abstract class AApp : Application() {

    protected var mMethodName: String? = null
    public val TAG = "App"
    private val TAGLOG = "AAppLOG"


    fun before(method: String, valueName: String = "") {
        mMethodName = method
        Log.w(TAG, ".thread_id=${Thread.currentThread().id}")
        Log.d(TAGLOG, "${mMethodName}.before")
    }

    private fun logAfterResult(result: String) {
        result?.let {
            Log.d(TAGLOG, "${mMethodName}.after result=${result} ")
            return@logAfterResult
        }
        Log.d(TAGLOG, "${mMethodName}.after")
        mMethodName = null
    }

    protected fun after(res: Boolean? = null) {
        logAfterResult(res.toStr())
    }

    protected fun LOGWork(valueNameAndValue: String) {
        mMethodName?.let {
            Log.i(TAGLOG, "${TAGLOG}:${mMethodName}.${valueNameAndValue}")
            return@LOGWork
        }
        Log.i(TAGLOG, "${TAGLOG}:${valueNameAndValue}")
    }

    protected fun LOGWork(valueName: String, value: Int) {
        LOGWork("${valueName}=$value")
    }

}