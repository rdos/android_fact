package ru.smartro.worknote.log

import android.app.Application
import android.provider.Settings
import org.slf4j.LoggerFactory
import ru.smartro.worknote.AppParaMS
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.awORKOLDs.util.MyUtil.toStr
import java.io.File

abstract class AApp : Application() {
    protected val log = LoggerFactory.getLogger("${this::class.simpleName}")


    protected val aPPParamS: AppParaMS by lazy {
        AppParaMS.create()
    }

    public val TAG = "App"
    private val TAGLOG = "AAppLOG"


    fun LOGbeforeLOG(valueName: String? = null) {
        if (valueName == null) {
            log.debug("before")
            return
        }
        log.debug("before.valueName=${valueName}")
    }

    private fun logAfterResult(result: String? = null) {
        result?.let {
            log.debug("after result=${result} ")
            return@logAfterResult
        }
        log.debug("after")
    }

    protected fun LOGafterLOG(res: Boolean? = null) {
        logAfterResult(res.toStr())
    }

    protected fun log(valueNameAndValue: String) {
        log.debug(valueNameAndValue)
    }

    protected fun log(valueName: String, value: Int) {
        log("${valueName}=$value")
    }

    protected fun log(valueName: String, value: String) {
        log("${valueName}=$value")
    }

    fun setDevelMODE(): Boolean {
        val isResTrue = false
        if ((BuildConfig.BUILD_TYPE == "debug" || BuildConfig.BUILD_TYPE == "debugRC") && (BuildConfig.VERSION_CODE == 1234567890)) {
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

    fun getDPath(Dname: String): String {
        return this.dataDir.absolutePath + File.separator + Dname
    }

    fun getD(Dname: String): File {
        val fl = getDPath(Dname)
        makeD(fl)
        return File(fl)
    }


    fun makeD(fl: String) {
        val file = File(fl)
        if (!file.exists()) file.mkdirs()
    }


    fun getF(Dname: String, Fname: String): File {
        val fl = getDPath(Dname)
        makeD(fl)
        val file = File(fl, Fname)
        return file
    }


}