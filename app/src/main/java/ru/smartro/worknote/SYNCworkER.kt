package ru.smartro.worknote

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.Settings.Secure
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import io.realm.Realm
import io.sentry.Sentry
import kotlinx.coroutines.delay
import ru.smartro.worknote.service.network.NetworkRepository
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.util.MyUtil.toStr
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
        Log.i(TAG, "onCreate")
    }

    fun before(method: String, valueName: String = "") {
        mMethodName = method
        Log.w(TAG, ".thread_id=${Thread.currentThread().id}")
        Log.d(TAGLOG, "${mMethodName}.before")
    }

    protected fun after(res: String) {
        logAfterResult(res.toStr())
    }

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



    private fun getApp() : App {
        return App.getAppliCation()
    }

    private fun paramS() : App.SharedPref {
        return App.getAppParaMS()
    }

    private val mNetworkRepository = NetworkRepository(applicationContext)
    private val mDeviceId = Secure.getString(p_application.contentResolver, Secure.ANDROID_ID)
    private val mMinutesInSec = 30 * 60



    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        before("doWork")

//        Realm.init(context)
        var db = RealmRepository(Realm.getDefaultInstance())
        val DELAY_MS =  if (paramS().isModeDEVEL) 1000 else 30000 as Long
        while (true) {
            before("delay")
            delay(DELAY_MS)
            after(" while (true) {")
            if (paramS().isModeSYNChrONize) {
                synChrONizationDATA(db)
            }
//            try {


//            } catch (ex: Exception) {
//                Log.e(TAG, "AAAAAAAAAAAAAAAAAAAAAAAAAAAA")
//                db = RealmRepository(Realm.getDefaultInstance())
//            }
        }
    }



    private suspend fun synChrONizationDATA(db: RealmRepository) {
        if (paramS().isModeSYNChrONize) {

            val timeBeforeRequest: Long
            logSentry(" SYNCHRONIZE STARTED")
            val lastSynchroTime = paramS().lastSynchroTime
            val platforms: List<PlatformEntity>

            if (lastSynchroTime - MyUtil.timeStamp() > mMinutesInSec) {
                platforms = db.findPlatforms30min()
                timeBeforeRequest = lastSynchroTime + mMinutesInSec
                Log.d(TAG, " SYNCHRONIZE PLATFORMS IN LAST 30 min")
            } else {
                platforms = db.findLastPlatforms()
                timeBeforeRequest = MyUtil.timeStamp()
                Log.d(TAG, " SYNCHRONIZE LAST PLATFORMS")
            }


            val location = paramS().getCurrentLocation()
            val lastKnownLocationTime = paramS().getLastKnownLocationTime()
            val coords = listOf(location.latitude, location.longitude)
            val synchronizeBody = SynchronizeBody(paramS().wayBillId, coords, mDeviceId, lastKnownLocationTime, platforms)

            Log.d(TAG, "platforms.size=${platforms.size}")


            saveJSON(synchronizeBody)
            val synchronizeResponse = mNetworkRepository.postSynchro(synchronizeBody)
            when (synchronizeResponse.status) {
                Status.SUCCESS -> {
                    if (platforms.isNotEmpty()) {
                        paramS().lastSynchroTime = timeBeforeRequest
                        db.updatePlatformNetworkStatus(platforms)
                        Log.d(TAG, "SYNCHRONIZE SUCCESS: ${Gson().toJson(synchronizeResponse.data)}")
                    } else {
                        Log.d(TAG, "SYNCHRONIZE SUCCESS: GPS SENT")
                    }
                    val alertMsg = synchronizeResponse.data?.alert
                    if (!alertMsg.isNullOrEmpty()) {
                        showNotification(false, alertMsg, "Уведомление")
                    }
                }
                Status.ERROR -> Log.e(TAG, "SYNCHRONIZE ERROR")
                Status.NETWORK -> Log.w(TAG, "SYNCHRONIZE NO INTERNET")
            }
        } else {
            Log.d(TAG, "WORKER STOPPED")
            dismissNotification()
        }
    }

    private fun showNotification(ongoing: Boolean, content: String, title: String) {
        val notificationManager = NotificationManagerCompat.from(getApp())
        if (notificationManager.notificationChannels.size <= 0) {
            val channelId = "M_CH_ID"
            val fullScreenIntent = Intent(getApp(), StartAct::class.java)
            fullScreenIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val fullScreenPendingIntent =
                PendingIntent.getActivity(getApp(), 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(getApp(), channelId)
                .run {
                    setSmallIcon(R.drawable.ic_app)
                    setLargeIcon(BitmapFactory.decodeResource(getApp().resources, R.drawable.ic_app))
                    setContentTitle(title)
                    setContentText(content)
                    setOngoing(ongoing)
                    priority = NotificationCompat.PRIORITY_MAX
                    setDefaults(NotificationCompat.DEFAULT_ALL)
                    setContentIntent(fullScreenPendingIntent)
                    setShowWhen(true)

                }
//        <!--<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />-->
            if (!ongoing) {
                builder.setFullScreenIntent(fullScreenPendingIntent, true)
            }
            val notification: Notification = builder.build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "FACT_SERVICE", NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(1, notification)
        } else {
            logSentry("showNotification. notificationManager.notificationChannels.size = ${notificationManager.notificationChannels.size}")
        }
    }

    private fun dismissNotification() {
        Log.d(TAG, "dismissNotification.before")
        val notificationManager = NotificationManagerCompat.from(getApp())
        notificationManager.cancelAll()
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
        var dirPath = getApp().filesDir.absolutePath
        if(containerUuid == null) {
            dirPath = dirPath + File.separator + platformUuid
        } else {
            dirPath = dirPath + File.separator + platformUuid + File.separator + containerUuid
        }

        val file = File(dirPath)
        if (!file.exists()) file.mkdirs()
        return file
    }



}

