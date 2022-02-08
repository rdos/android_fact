package ru.smartro.worknote.work

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
import ru.smartro.worknote.R
import ru.smartro.worknote.service.network.NetworkRepository
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.work.ac.StartAct
import java.io.File
import java.io.FileOutputStream


class SynchronizeWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val TAG = "SynchronizeWorker"
    protected fun logSentry(text: String) {
        Sentry.addBreadcrumb("${TAG} : $text")
        Log.i(TAG, "onCreate")
    }
    private val mNetworkRepository = NetworkRepository(applicationContext)
    private val mDeviceId = Secure.getString(context.contentResolver, Secure.ANDROID_ID)
    private val mMinutesInSec = 30 * 60

    override suspend fun doWork(): Result {
        Log.w(TAG, "doWork.before thread_id=${Thread.currentThread().id}")

        showNotification(context, true, "Не закрывайте приложение", "Служба отправки данных работает")
        val db = RealmRepository(Realm.getDefaultInstance())
        while (true) {
            synchronizeData(db)
            delay(30_000)
        }
    }

    private suspend fun synchronizeData(db: RealmRepository) {
        if (AppPreferences.workerStatus) {
            var lat = 0.0
            var long = 0.0
            val currentCoordinate = AppPreferences.currentCoordinate
            if (currentCoordinate.contains("#")) {
                lat = currentCoordinate.substringBefore("#").toDouble()
                long = currentCoordinate.substringAfter("#").toDouble()
            }
            val timeBeforeRequest: Long
            logSentry(" SYNCHRONIZE STARTED")
            val lastSynchroTime = AppPreferences.lastSynchroTime
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

            val synchronizeBody = SynchronizeBody(AppPreferences.wayBillId, listOf(lat, long), mDeviceId, platforms)

            saveJSON(synchronizeBody)
            val synchronizeResponse = mNetworkRepository.synchronizeData(synchronizeBody)
            when (synchronizeResponse.status) {
                Status.SUCCESS -> {
                    if (platforms.isNotEmpty()) {
                        AppPreferences.lastSynchroTime = timeBeforeRequest
                        db.updatePlatformNetworkStatus(platforms)
                        Log.d(TAG, "SYNCHRONIZE SUCCESS: ${Gson().toJson(synchronizeResponse.data)}")
                    } else {
                        Log.d(TAG, "SYNCHRONIZE SUCCESS: GPS SENT")
                    }
                    val alertMsg = synchronizeResponse.data?.alert
                    if (!alertMsg.isNullOrEmpty()) {
                        showNotification(context, false, alertMsg, "Уведомление")
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
        var dirPath = context.filesDir.absolutePath
        if(containerUuid == null) {
            dirPath = dirPath + File.separator + platformUuid
        } else {
            dirPath = dirPath + File.separator + platformUuid + File.separator + containerUuid
        }

        val file = File(dirPath)
        if (!file.exists()) file.mkdirs()
        return file
    }

    private fun showNotification(context: Context, ongoing: Boolean, content: String, title: String) {
        val channelId = "M_CH_ID"
        val fullScreenIntent = Intent(context, StartAct::class.java)
        fullScreenIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val fullScreenPendingIntent =
            PendingIntent.getActivity(context, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .run {
                setSmallIcon(R.drawable.ic_app)
                setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_app))
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
        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "FACT_SERVICE", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(1, notification)
    }

    private fun dismissNotification() {
        Log.d(TAG, "dismissNotification.before")
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(1)
    }

}

