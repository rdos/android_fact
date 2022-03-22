package ru.smartro.worknote.log

import android.app.Application
import android.util.Log
import com.yandex.mapkit.geometry.Point
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.awORKOLDs.util.MyUtil.toStr

abstract class AApp : Application() {


    protected val aPPParamS: AppParaMS by lazy {
        AppParaMS.create()
    }

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

    protected fun LOGWork(valueName: String, value: String) {
        LOGWork("${valueName}=$value")
    }


    fun isDevelMODE(): Boolean {
        val isResTrue = true
        if (BuildConfig.BUILD_TYPE == "debug" || BuildConfig.BUILD_TYPE == "debugRC") {
            return isResTrue
            // TODO: ?R_dos
//            if (BuildConfig.VERSION_NAME == "1.2.3-STAGE") {
//                return isResTrue
//            }
//            if (BuildConfig.VERSION_CODE <= 0) {
//                return isResTrue
//            }
        }
        return false
    }
}