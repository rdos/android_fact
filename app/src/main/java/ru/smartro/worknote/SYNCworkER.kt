package ru.smartro.worknote

import android.content.Context
import android.provider.Settings.Secure
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import io.realm.Realm
import io.sentry.Sentry
import kotlinx.coroutines.delay
import ru.smartro.worknote.awORKOLDs.service.network.NetworkRepository
import ru.smartro.worknote.awORKOLDs.service.network.Status
import ru.smartro.worknote.awORKOLDs.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.awORKOLDs.util.MyUtil.toStr
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.RealmRepository
import java.io.File
import java.io.FileOutputStream


//private var App.LocationLAT: Double
//    get() {
//        TODO("Not yet implemented")
//    }
//    set() {}




class SYNCworkER(
    p_application: Context,
    params: WorkerParameters
) : CoroutineWorker(p_application, params) {



    private val mNetworkRepository = NetworkRepository(applicationContext)
    private val mDeviceId = Secure.getString(p_application.contentResolver, Secure.ANDROID_ID)
    private val mMinutesInSec = 30 * 60


    private fun showWorkERNotification(isForceMode: Boolean = false,
                                       content: String = "Не закрывайте приложение",
                                       title: String = "Служба отправки данных работает") {
//        if (isForceMode) {
//            App.getAppliCation().showNotificationForce(content, title)
//        } else {
//            App.getAppliCation().showNotification(content, title)
//        }
    }

    private fun showWorkERROR(isForceMode: Boolean,
                              content: String ,
                              title: String) {
        showWorkERNotification(isForceMode, content, title)
    }

    override suspend fun doWork(): Result {
        before("doWork")
        val params = App.getAppParaMS()
        params.isModeSYNChrONize_FoundError = false
        try {
            var db = RealmRepository(Realm.getDefaultInstance())
            val DELAY_MS: Long =  if (App.getAppParaMS().isModeDEVEL) 5_000 else 30_000
            while (true) {
                beforeCycles("while (true)")


                if (params.isModeSYNChrONize) {
                    showWorkERNotification()
                    Log.d(TAG, "SYNCworkER RUN")
                    synChrONizationDATA(db)
                } else {
                    Log.d(TAG, "SYNCworkER STOPPED")
                    showWorkERNotification(true,"Служба отправки данных остановлена", "Служба отправки данных НЕ работает")
                }
                delay(DELAY_MS)
                afterCycles()
//            try {


//            } catch (ex: Exception) {
//                Log.e(TAG, "AAAAAAAAAAAAAAAAAAAAAAAAAAAA")
//                db = RealmRepository(Realm.getDefaultInstance())
//            }
            } //todo: while (true) {
        } catch (ex: Throwable) {
            params.isModeSYNChrONize_FoundError = true
            showWorkERROR(true, "ОШИБКА Служба отправки данных НЕ работает", "Служба отправки данных НЕ работает")
            return Result.retry()
        }
//        Realm.init(context)
        return Result.failure()
    }



    private suspend fun synChrONizationDATA(db: RealmRepository) {
        val timeBeforeRequest: Long
        logSentry("SYNCworkER STARTED")
        val lastSynchroTime =App.getAppParaMS().lastSynchroTime
        val platforms: List<PlatformEntity>

        if (lastSynchroTime - MyUtil.timeStamp() > mMinutesInSec) {
            platforms = db.findPlatforms30min()
            timeBeforeRequest = lastSynchroTime + mMinutesInSec
            Log.d(TAG, "SYNCworkER PLATFORMS IN LAST 30 min")
        } else {
            platforms = db.findLastPlatforms()
            timeBeforeRequest = MyUtil.timeStamp()
            Log.d(TAG, "SYNCworkER LAST PLATFORMS")
        }


        val gpsData =App.getAppParaMS().geTLastKnowGPS()
        val synchronizeBody = SynchronizeBody(App.getAppParaMS().wayBillId, gpsData.PointToListDouble(),
            mDeviceId, gpsData.PointTimeToLastKnowTime_SRV(), platforms)

        Log.d(TAG, "platforms.size=${platforms.size}")


//        saveJSON(synchronizeBody)
        val synchronizeResponse = mNetworkRepository.postSynchro(synchronizeBody)
        when (synchronizeResponse.status) {
            Status.SUCCESS -> {
//                throw java.lang.Exception("ex")
                if (platforms.isNotEmpty()) {
                   App.getAppParaMS().lastSynchroTime = timeBeforeRequest
                    db.updatePlatformNetworkStatus(platforms)
                    Log.d(TAG, "SYNCHRONIZE SUCCESS: ${Gson().toJson(synchronizeResponse.data)}")
                } else {
                    Log.d(TAG, "SYNCHRONIZE SUCCESS: GPS SENT")
                }
                val alertMsg = synchronizeResponse.data?.alert
                if (!alertMsg.isNullOrEmpty()) {
                    logSentry("ValertMsgalertMsgalertMsgalertMsgalertMsg!!!!!!!")
//                    App.getAppliCation().showNotification(alertMsg, "Уведомление")
                }
            }
            Status.ERROR -> Log.e(TAG, "SYNCHRONIZE ERROR")
            Status.NETWORK -> Log.w(TAG, "SYNCHRONIZE NO INTERNET")
        }
    }



    fun saveJSON(synchronizeBody: SynchronizeBody) {
        val gson = Gson()
        val bodyInStringFormat = gson.toJson(synchronizeBody)
        deleteOutputDirectory("ttest", null)
        val file: File = File(getOutputDirectory("ttest", null), "synchro.json")

        //This point and below is responsible for the write operation

        //This point and below is responsible for the write operation
        var outputStream: FileOutputStream? = null
        try {

            file.createNewFile()
            //second argument of FileOutputStream constructor indicates whether
            //to append or create new file if one exists
            outputStream = FileOutputStream(file, true)
            outputStream.write(bodyInStringFormat.toByteArray())
            outputStream.flush()
            outputStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun deleteOutputDirectory(platformUuid: String, containerUuid: String?) {
        try {
            val file = getOutputDirectory(platformUuid, containerUuid)
            file.deleteRecursively()
        } catch (e: Exception) {
//            log.error("deleteOutputDirectory", e)
        }
    }

    fun getOutputDirectory(platformUuid: String, containerUuid: String?): File {
        var dirPath = App.getAppliCation().filesDir.absolutePath
        if(containerUuid == null) {
            dirPath = dirPath + File.separator + platformUuid
        } else {
            dirPath = dirPath + File.separator + platformUuid + File.separator + containerUuid
        }

        val file = File(dirPath)
        if (!file.exists()) file.mkdirs()
        return file
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


    private var mMethodName: String? = null
    private val TAG = "SYNCworkER"
    private val TAGLOG = TAG
    protected fun logSentry(text: String) {
        Sentry.addBreadcrumb("${TAG} : $text")
        Log.i(TAG, "text")
    }

    //SYNCworkER
    fun before(method: String, valueName: String = "") {
        mMethodName = method
        Log.w(TAG, ".thread_id=${Thread.currentThread().id}")
        Log.d(TAGLOG, "${mMethodName}.before")
    }

    fun beforeCycles(s: String) {
        mMethodName?.let {
            Log.d(TAGLOG, "${mMethodName}.CYCLes.${s}")
            return@beforeCycles
        }
        Log.d(TAGLOG, "CYCLes.${s}")
    }

    fun afterCycles() {
        mMethodName?.let {
            Log.d(TAGLOG, "${mMethodName}.************-_(:;)")
            return@afterCycles
        }
        Log.d(TAGLOG, ".************-_(:;)")
    }

    //SYNCworkER
    protected fun after(res: String) {
        logAfterResult(res.toStr())
    }

    //    SYNCworkER
    protected fun after(res: Boolean? = null) {
        logAfterResult(res.toStr())
    }

    private fun logAfterResult(result: String) {
        result?.let {
            Log.d(TAGLOG, "${mMethodName}.after result=${result} ")
            return@logAfterResult
        }
        Log.d(TAGLOG, "${mMethodName}.after")
        mMethodName = null
    }


}

