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
import org.slf4j.LoggerFactory
import ru.smartro.worknote.awORKOLDs.service.network.body.PingBody
import ru.smartro.worknote.awORKOLDs.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.awORKOLDs.util.MyUtil.toStr
import ru.smartro.worknote.utils.getActivityProperly
import ru.smartro.worknote.work.*
import ru.smartro.worknote.presentation.ac.StartAct
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
    protected val log = LoggerFactory.getLogger("${this::class.simpleName}")

    private val mNetworkRepository = NetworkRepository(applicationContext)
    // TODO: r_dos....кака код))раз
    private fun showWorkERNotification(isForceMode: Boolean = true,
                                       contentText: String = "Не закрывайте приложение",
                                       titleText: String = "Служба отправки данных работает") {

        val intent = Intent(applicationContext, StartAct::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = getActivityProperly(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (isForceMode) {
            App.getAppliCation().showNotificationForce(pendingIntent, contentText, titleText)
        } else {
            App.getAppliCation().showNotification(pendingIntent, contentText, titleText)
        }
    }
    private var oldThreadId = Lnull
    private var mDb: RealmRepository? = null

    fun db(): RealmRepository {
        val currentThreadId = Thread.currentThread().id
        if (oldThreadId != currentThreadId) {
            LOGWork("SYNCworkER::db:.currentThreadId=${currentThreadId} ")
            LOGWork("SYNCworkER::db:.oldThreadId=${oldThreadId} ")
            oldThreadId = currentThreadId
            mDb = RealmRepository(Realm.getDefaultInstance())
            return mDb!!
        }
        return mDb!!
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
                    ping()
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
            log.error("", eXthr)
//            аккуратней::r_dos
            params.isModeSYNChrONize_FoundError = true
            showWorkERROR()
            throw eXthr
//            return Result.retry()
        }
//        Realm.init(context)
//        return Result.failure()
    }

    private suspend fun ping() {
        beforeLOG("PING STARTED ::::")
        val pingResponse = mNetworkRepository.ping(PingBody("ping"))
        when (pingResponse.status) {
            Status.SUCCESS -> {
                log.debug("PING RESPONSE:")
                log.error( pingResponse.data.toString())
                val message = pingResponse.data?.payload?.message
                if(message != null)
                    (applicationContext as App).showAlertNotification(message)
                else
                    log.error( "Ping EMPTY MESSAGE ${pingResponse.data}")
            }
            Status.ERROR -> log.error( "Ping ERROR ${pingResponse.msg}")
            Status.NETWORK -> log.warn( "Ping NO INTERNET")
        }

        LOGafterLOG()
    }

    private suspend fun synChrONizationDATA() {
        beforeLOG("synChrONizationDATA")
        var timeBeforeRequest: Long = MyUtil.timeStampInSec()
        logSentry("SYNCworkER STARTED")
        val lastSynchroTimeInSec = App.getAppParaMS().lastSynchroTimeInSec
        var platforms: List<PlatformEntity> = emptyList()
        LOGWork("SYNCworkER::synChrONizationDATA:Thread.currentThread().id()=${Thread.currentThread().id}")
        //проблема в секундах синхронизаций
        val m30MinutesInSec = 30 * 60
        if (MyUtil.timeStampInSec() - lastSynchroTimeInSec > m30MinutesInSec) {
            timeBeforeRequest = lastSynchroTimeInSec + m30MinutesInSec
            platforms = db().findPlatforms30min()
            LOGWork( "SYNCworkER PLATFORMS IN LAST 30 min")
        }
        if (platforms.isEmpty()) {
            timeBeforeRequest = MyUtil.timeStampInSec()
            platforms = db().findLastPlatforms()
            LOGWork("SYNCworkER LAST PLATFORMS")
        }


        val gps = App.getAppliCation().gps()
        val synchronizeBody = SynchronizeBody(App.getAppParaMS().wayBillId,
            gps.PointTOBaseData(),
            AppParaMS().deviceId,
            gps.PointTimeToLastKnowTime_SRV(),
            platforms)

        log.debug("platforms.size=${platforms.size}")


//        saveJSON(synchronizeBody)
        val synchronizeResponse = mNetworkRepository.postSynchro(synchronizeBody)
        when (synchronizeResponse.status) {
            Status.SUCCESS -> {
                if (platforms.isNotEmpty()) {
                   App.getAppParaMS().lastSynchroTimeInSec = timeBeforeRequest
                    log.error( Thread.currentThread().getId().toString())
                    db().updatePlatformNetworkStatus(platforms)
                    log.debug("SYNCworkER SUCCESS: ${Gson().toJson(synchronizeResponse.data)}")
                } else {
                    log.debug("SYNCworkER SUCCESS: GPS SENT")
                }
                val alertMsg = synchronizeResponse.data?.alert
                if (!alertMsg.isNullOrEmpty()) {
                    logSentry("ValertMsgalertMsgalertMsgalertMsgalertMsg!!!!!!!")
//                    App.getAppliCation().showNotification(alertMsg, "Уведомление")
                }
            }
            Status.ERROR -> log.error( "SYNCworkER ERROR")
            Status.NETWORK -> log.warn( "SYNCworkER NO INTERNET")
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
        log.info( "text")
    }

    //SYNCworkER
    fun beforeLOG(method: String, valueName: String = "") {
        mMethodName = method
        log.warn( ".thread_id=${Thread.currentThread().id}")
        log.debug("${mMethodName}.before")
    }

    fun INcyclEStart(s: String) {
        mMethodName?.let {
            log.debug("${mMethodName}.CYCLes.${s}")
            return@INcyclEStart
        }
        log.debug("CYCLes.${s}")
    }

    fun INcyclEStop() {
        mMethodName?.let {
            log.debug("${mMethodName}.************-_(:;)")
            return@INcyclEStop
        }
        log.debug(".************-_(:;)")
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
            log.debug("${mMethodName}.after result=${result} ")
            return@logAfterResult
        }
        log.debug("${mMethodName}.after")
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
if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") {
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