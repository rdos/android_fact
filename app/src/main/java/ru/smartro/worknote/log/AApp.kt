package ru.smartro.worknote.log

import android.app.Application
import android.provider.Settings
import ru.smartro.worknote.AppParaMS
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.LOG
import ru.smartro.worknote.presentation.came.EXTENSION_WHITELIST
import java.io.File
import java.util.*

abstract class AApp : Application() {
    protected val aPPParamS: AppParaMS by lazy {
        AppParaMS.create()
    }

    fun setDevelMODE(): Boolean {
        val isResTrue = false
        if ((BuildConfig.BUILD_TYPE != "release") && (BuildConfig.VERSION_CODE == 1234567890)) {
            return true
        }
        return isResTrue
    }

    fun getDeviceId(): String {
//        LOG.debug("${Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)}")
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

    //r_dos что такое Array<out File>? !!
    fun getDFileList(Dname: String): Array<File> {
        // Get root directory of media from
        var result = this.getD(Dname).listFiles { file ->
            EXTENSION_WHITELIST.contains(file.extension.toUpperCase(Locale.ROOT))
        }
        if (result == null) {
            result = emptyArray()
        }
        LOG.info("result=${result.size}")
        return result
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