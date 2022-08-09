package ru.smartro.worknote.log

import android.app.Application
import android.provider.Settings
import android.util.Log
import ru.smartro.worknote.App
import ru.smartro.worknote.AppParaMS
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.awORKOLDs.util.MyUtil.toStr

abstract class AApp : Application() {


    protected val aPPParamS: AppParaMS by lazy {
        AppParaMS.create()
    }

    protected var mMethodName: String? = null
    public val TAG = "App"
    private val TAGLOG = "AAppLOG"


    fun beforeLOG(method: String, valueName: String = "") {
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

    protected fun LOGafterLOG(res: Boolean? = null) {
        logAfterResult(res.toStr())
    }

    protected fun log(valueNameAndValue: String) {
        mMethodName?.let {
            Log.i(TAGLOG, "${TAGLOG}:${mMethodName}.${valueNameAndValue}")
            return@log
        }
        Log.i(TAGLOG, "${TAGLOG}:${valueNameAndValue}")
    }

    protected fun log(valueName: String, value: Int) {
        log("${valueName}=$value")
    }

    protected fun log(valueName: String, value: String) {
        log("${valueName}=$value")
    }

    fun setDevelMODE(): Boolean {
        val isResTrue = false
        if ((BuildConfig.BUILD_TYPE != "release") && (BuildConfig.VERSION_CODE == 1234567890)) {
            return true
        }
        return isResTrue
    }

    fun getDeviceId(): String {
//        log("${Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)}")
        try {
            return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        } catch (ex: Throwable) {
            return "ThrowableDeviceId"
        }
    }


}