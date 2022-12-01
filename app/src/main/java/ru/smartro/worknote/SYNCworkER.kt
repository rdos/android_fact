package ru.smartro.worknote

//import ru.smartro.worknote.utils.DispatcherInfoMessageTypes
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.realm.Realm
import kotlinx.coroutines.delay
import ru.smartro.worknote.presentation.RPOSTSynchro
import ru.smartro.worknote.presentation.ActStart
import ru.smartro.worknote.log.todo.NetworkRepository
import ru.smartro.worknote.work.work.RealmRepository
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

        val intent = Intent(applicationContext, ActStart::class.java)
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
            LOG.debug("SYNCworkER::db:.currentThreadId=${currentThreadId} ")
            LOG.debug("SYNCworkER::db:.oldThreadId=${oldThreadId} ")
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
        LOG.debug("before")
        var isFirstRun: Boolean = true
        var isModeSyncOldVal = false

        val params = App.getAppParaMS()
        params.isModeSYNChrONize_FoundError = false

        try {
            val DELAY_MS: Long =  if (App.getAppParaMS().isModeDEVEL) 11_011 else 30_000
            while (true) {
                LOGinCYCLEStart("while (true)")

                if (isModeSyncOldVal != params.isModeSYNChrONize) {
                    isFirstRun = true
                }
                isModeSyncOldVal = params.isModeSYNChrONize
                if (params.isModeSYNChrONize) {
                    LOG.debug( "SYNCworkER RUN")
                    synChrONizationDATA()
//                    ping()
                    if (isFirstRun) {
                        showWorkERNotification(true)
                    }
                } else {
                    LOG.debug("SYNCworkER STOPPED")
                    if (isFirstRun) {
                        showWorkERNotification(true, contentText = "Служба отправки данных не работает",
                            titleText = "Отправка Данных ПРИОСТАНОВЛЕНА")
                    }
                }
                isFirstRun = false
                LOGINcyclEStop()
                delay(DELAY_MS)
            } //todo: while (true) {
        } catch (eXthr: Throwable) {
            LOG.error("eXthr", eXthr)
//            аккуратней::r_dos
            params.isModeSYNChrONize_FoundError = true
            showWorkERROR()
            throw eXthr
//            return Result.retry()
        }
//        Realm.init(context)
//        return Result.failure()
    }

    private fun ping() {
//        val rpcPing = RCPping()
//        rpcPing.getLiveDate().observe(viewLifecycleOwner) { result ->
//            LOG.debug("${result}")
//            hideProgress()
//            if (result.isSent) {
//                gotoNextAct()
//            }
//        }
//        App.oKRESTman().add(rpcPing)
//        
                ////        LOG.debug("PING STARTED ::::")
                //        val pingResponse = mNetworkRepository.ping(PingBody("ping"))
                //        when (pingResponse.status) {
                //            Status.SUCCESS -> {
                ////                LOG.debug("PING RESPONSE:")
                ////                LoG.error( pingResponse.data.toString())
                //                val message = pingResponse.data?.payload?.message
                //                if(message != null)
                //                    (applicationContext as App).showAlertNotification(message)
                //                else {
                ////                    LoG.error("Ping EMPTY MESSAGE ${pingResponse.data}")
                //                }
                //            }
                ////            Status.ERROR -> LoG.error( "Ping ERROR ${pingResponse.msg}")
                ////            Status.NETWORK -> LoG.warn( "Ping NO INTERNET")
                //        }
                //
                ////        LOGafterLOG.debug()
    }

    private fun synChrONizationDATA() {
        LOG.debug("before")
        logSentry("SYNCworkER STARTED")

        val synchroRequest = RPOSTSynchro()

        App.oKRESTman().put(synchroRequest)
        


//        LOG.info("platforms.size=${platforms.size}")
////        val gson = Gson()
////        val bodyInStringFormat = gson.toJson(synchronizeBody)
////        saveJSON(bodyInStringFormat, "postSynchro")
//        val synchronizeResponse = mNetworkRepository.postSynchro(synchronizeBody)
//        when (synchronizeResponse.status) {
//            Status.SUCCESS -> {
///
//            }
//            Status.ERROR -> LOG.error("SYNCworkER ERROR")
//            Status.NETWORK -> {
//                LOG.warn("SYNCworkER NO INTERNET")
//                db().setConfigCntPlusOne(ConfigName.NOINTERNET_CNT)
//            }
//            Status.ERROR -> LOG.error("Status.ERROR")
//            Status.NETWORK -> LOG.warn("Status.NETWORK==NO INTERNET")
//        }
//
//        LOG.debug("after")
        
    }


//    protected fun LOG.debug(valueNameAndValue: String) {
//        mMethodName?.let {
//            Log.i(TAGLOG, "${TAGLOG}:${mMethodName}.${valueNameAndValue}")
//            return@LOGWork
//        }
//        Log.i(TAGLOG, "${TAGLOG}:${valueNameAndValue}")
//    }
//
//    protected fun LOG.debug(valueName: String, value: Int) {
//        LOG.debug("${valueName}=$value")
//    }


    fun saveJSON(bodyInStringFormat: String, p_jsonName: String) {
        fun getOutputDirectory(platformUuid: String, containerUuid: String?): File {
            var dirPath = App.getAppliCation().dataDir.absolutePath
            if(containerUuid == null) {
                dirPath = dirPath + File.separator + platformUuid
            } else {
                dirPath = dirPath + File.separator + platformUuid + File.separator + containerUuid
            }

            val file = File(dirPath)
            if (!file.exists()) file.mkdirs()
            return file
        }
        val file: File = File(getOutputDirectory("saveJSON", null), "${p_jsonName}.json")

        //This point and below is responsible for the write operation

        try {
            file.delete()
        } catch (ex: Exception) {
            LOG.error("file.delete()", ex)
        }

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

    protected fun logSentry(text: String) {
        App.getAppliCation().logSentry(text)
    }
}
/**   //SYNCworkER

//    fun INcyclEStart(s: String) {
//        mMethodName?.let {
//            LOG.debug("${mMethodName}.CYCLes.${s}")
//            return@INcyclEStart
//        }
//        LOG.debug("CYCLes.${s}")
//    }
//
//    fun INcyclEStop() {
//        mMethodName?.let {
//            LOG.debug("${mMethodName}.************-_(:;)")
//            return@INcyclEStop
//        }
//        LOG.debug(".************-_(:;)")
//    }

//    //SYNCworkER
//    protected fun LOGafterLOG.debug(res: String) {
//        logAfterResult(res.toStr())
//    }
//
//    //    SYNCworkER
//    protected fun LOGafterLOG.debug(res: Boolean? = null) {
//        logAfterResult(res.toStr())
//    }

//    private fun logAfterResult(result: String) {
//        result?.let {
//            LOG.debug("${mMethodName}.after result=${result} ")
//            return@logAfterResult
//        }
//        LOG.debug("${mMethodName}.after")
//        mMethodName = null
//    }
**/

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
