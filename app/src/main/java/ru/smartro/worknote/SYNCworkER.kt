package ru.smartro.worknote

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import ru.smartro.worknote.AppParaMS
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.RealmRepository
import ru.smartro.worknote.work.ac.StartAct
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
    // TODO: r_dos....кака код))раз
    private fun showWorkERNotification(isForceMode: Boolean = true,
                                       contentText: String = "Не закрывайте приложение",
                                       titleText: String = "Служба отправки данных работает") {

        val intent = Intent(applicationContext, StartAct::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent =  PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (isForceMode) {
            App.getAppliCation().showNotificationForce(pendingIntent, contentText, titleText)
        } else {
            App.getAppliCation().showNotification(pendingIntent, contentText, titleText)
        }
    }

    private val db: RealmRepository by lazy {
        RealmRepository(Realm.getDefaultInstance())
    }

    private fun showWorkERROR(contentText: String="ОШИБКА служба ОТПРАВКИ данных НЕ работает",
                              title: String="Попробуйте перезайти в приложение") {
        showWorkERNotification(true, contentText, title)
    }

    // TODO: r_dos.)))кака код...лох это судьба))два
    override suspend fun doWork(): Result {
        beforeLOG("doWork")
        var isFirstRun: Boolean = true
        var isModeSyncOldVal = false

        val params = App.getAppParaMS()
        params.isModeSYNChrONize_FoundError = false

        try {
            val DELAY_MS: Long =  if (App.getAppParaMS().isModeDEVEL) 11_011 else 30_000
            while (true) {
                INcyclEStart("while (true)")

                if (isModeSyncOldVal != params.isModeSYNChrONize) {
                    isFirstRun = true
                }
                isModeSyncOldVal = params.isModeSYNChrONize
                if (params.isModeSYNChrONize) {
                    LOGWork( "SYNCworkER RUN")
                    synChrONizationDATA()
                    if (isFirstRun) {
                        showWorkERNotification(true)
                    }
                } else {
                    LOGWork("SYNCworkER STOPPED")
                    if (isFirstRun) {
                        showWorkERNotification(true, contentText = "Служба отправки данных не работает",
                            titleText = "Отправка Данных ПРИОСТАНОВЛЕНА")
                    }
                }
                isFirstRun = false
                INcyclEStop()
                delay(DELAY_MS)
            } //todo: while (true) {
        } catch (eXthr: Throwable) {
            Log.e(TAG, "eXthr.message", eXthr)
//            аккуратней::r_dos
            params.isModeSYNChrONize_FoundError = true
            showWorkERROR()
            throw eXthr
//            return Result.retry()
        }
//        Realm.init(context)
//        return Result.failure()
    }



    private suspend fun synChrONizationDATA() {
        beforeLOG("synChrONizationDATA")
        val timeBeforeRequest: Long
        logSentry("SYNCworkER STARTED")
        val lastSynchroTime =App.getAppParaMS().lastSynchroTime
        val platforms: List<PlatformEntity>

        //проблема в секундах синхронизаций
        val mMinutesInSec = 30 * 60
        if (lastSynchroTime - MyUtil.timeStamp() > mMinutesInSec) {
            timeBeforeRequest = lastSynchroTime + mMinutesInSec
            platforms = db.findPlatforms30min()
            Log.d(TAG, "SYNCworkER PLATFORMS IN LAST 30 min")
        } else {
            timeBeforeRequest = MyUtil.timeStamp()
            platforms = db.findLastPlatforms()
            LOGWork("SYNCworkER LAST PLATFORMS")
        }


        val gps = App.getAppliCation().gps()
        val synchronizeBody = SynchronizeBody(App.getAppParaMS().wayBillId,
            gps.PointTOBaseData(),
            AppParaMS().deviceId,
            gps.PointTimeToLastKnowTime_SRV(),
            platforms)

        Log.d(TAG, "platforms.size=${platforms.size}")


//        saveJSON(synchronizeBody)
        val synchronizeResponse = mNetworkRepository.postSynchro(synchronizeBody)
        when (synchronizeResponse.status) {
            Status.SUCCESS -> {
                if (platforms.isNotEmpty()) {
                   App.getAppParaMS().lastSynchroTime = timeBeforeRequest
                    db.updatePlatformNetworkStatus(platforms)
                    Log.d(TAG, "SYNCworkER SUCCESS: ${Gson().toJson(synchronizeResponse.data)}")
                } else {
                    Log.d(TAG, "SYNCworkER SUCCESS: GPS SENT")
                }
                val alertMsg = synchronizeResponse.data?.alert
                if (!alertMsg.isNullOrEmpty()) {
                    logSentry("ValertMsgalertMsgalertMsgalertMsgalertMsg!!!!!!!")
//                    App.getAppliCation().showNotification(alertMsg, "Уведомление")
                }
            }
            Status.ERROR -> Log.e(TAG, "SYNCworkER ERROR")
            Status.NETWORK -> Log.w(TAG, "SYNCworkER NO INTERNET")
        }
        LOGafterLOG()

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
    fun beforeLOG(method: String, valueName: String = "") {
        mMethodName = method
        Log.w(TAG, ".thread_id=${Thread.currentThread().id}")
        Log.d(TAGLOG, "${mMethodName}.before")
    }

    fun INcyclEStart(s: String) {
        mMethodName?.let {
            Log.d(TAGLOG, "${mMethodName}.CYCLes.${s}")
            return@INcyclEStart
        }
        Log.d(TAGLOG, "CYCLes.${s}")
    }

    fun INcyclEStop() {
        mMethodName?.let {
            Log.d(TAGLOG, "${mMethodName}.************-_(:;)")
            return@INcyclEStop
        }
        Log.d(TAGLOG, ".************-_(:;)")
    }

    //SYNCworkER
    protected fun LOGafterLOG(res: String) {
        logAfterResult(res.toStr())
    }

    //    SYNCworkER
    protected fun LOGafterLOG(res: Boolean? = null) {
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

/**
 *
 *
 *
SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
if (!prefs.getBoolean("firstTime", false)) {

Intent alarmIntent = new Intent(this, AlarmReceiver.class);
PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

Calendar calendar = Calendar.getInstance();
calendar.setTimeInMillis(System.currentTimeMillis());
calendar.set(Calendar.HOUR_OF_DAY, 7);
calendar.set(Calendar.MINUTE, 0);
calendar.set(Calendar.SECOND, 1);

manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
AlarmManager.INTERVAL_DAY, pendingIntent);

SharedPreferences.Editor editor = prefs.edit();
editor.putBoolean("firstTime", true);
editor.apply();
}

ublic class AlarmReceiver extends BroadcastReceiver {
@Override
public void onReceive(Context context, Intent intent) {
// show toast
Toast.makeText(context, "Alarm running", Toast.LENGTH_SHORT).show();
}
}


public class DeviceBootReceiver extends BroadcastReceiver {
@Override
public void onReceive(Context context, Intent intent) {
if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
// on device boot compelete, reset the alarm
Intent alarmIntent = new Intent(context, AlarmReceiver.class);
PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

Calendar calendar = Calendar.getInstance();
calendar.setTimeInMillis(System.currentTimeMillis());
calendar.set(Calendar.HOUR_OF_DAY, 7);
calendar.set(Calendar.MINUTE, 0);
calendar.set(Calendar.SECOND, 1);

manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
AlarmManager.INTERVAL_DAY, pendingIntent);
}
}
}


<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />



<receiver android:name=".DeviceBootReceiver">
<intent-filter>
<action android:name="android.intent.action.BOOT_COMPLETED" />
</intent-filter>
</receiver>
<receiver android:name=".AlarmReceiver" />

!r_dos * R_dos!!!*/