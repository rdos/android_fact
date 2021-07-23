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
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import io.realm.Realm
import kotlinx.coroutines.delay
import ru.smartro.worknote.R
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.RealmRepository
import ru.smartro.worknote.service.network.NetworkRepository
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.ui.map.MapActivity
import ru.smartro.worknote.util.MyUtil


class SynchronizeWorker(
    private val context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val TAG = "UploadDataWorkManager"
    private val network = NetworkRepository(applicationContext)
    private val deviceId = Secure.getString(context.contentResolver, Secure.ANDROID_ID)

    override suspend fun doWork(): Result {
        showNotification(context, true, "Не закрывайте приложение", "Служба отправки данных работает")
            val db = RealmRepository(Realm.getDefaultInstance())
            while (true) {
                synchronizeData(db)
                delay(30_000)
            }
        }

    private suspend fun synchronizeData(db: RealmRepository) {
        if (AppPreferences.workerStatus) {
            var long = 0.0
            var lat = 0.0
            val currentCoordinate = AppPreferences.currentCoordinate!!
            if (currentCoordinate.contains("#")) {
                long = currentCoordinate.substringAfter("#").toDouble()
                lat = currentCoordinate.substringBefore("#").toDouble()
            }
            val timeBeforeRequest = MyUtil.timeStamp()
            Log.d(TAG, " SYNCHRONIZE STARTED")

            val platforms = db.findLastPlatforms()
            val synchronizeBody = SynchronizeBody(AppPreferences.wayBillId, listOf(lat, long), deviceId, platforms)
            val synchronizeRequest = network.synchronizeData(synchronizeBody)
            when (synchronizeRequest.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "SYNCHRONIZE SUCCESS: ${Gson().toJson(synchronizeRequest.data)}")
                    AppPreferences.lastSynchroTime = timeBeforeRequest
                    db.updatePlatformNetworkStatus(platforms)
                    val alertMsg = synchronizeRequest.data?.alert
                    if (!alertMsg.isNullOrEmpty()) {
                        showNotification(context, false, alertMsg, "Уведомление")
                    }
                }
                Status.ERROR -> Log.d(TAG, "SYNCHRONIZE GPS SENT ERROR")
                Status.NETWORK -> Log.e(TAG, "SYNCHRONIZE  SENT NO INTERNET")
            }
        } else {
            Log.d(TAG, "WORKER STOPPED")
            dismissNotification()
        }
    }

    private fun showNotification(context: Context, ongoing: Boolean, content: String, title: String) {
        val channelId = "M_CH_ID"
        val fullScreenIntent = Intent(context, MapActivity::class.java)
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
                setPriority(NotificationCompat.PRIORITY_MAX)
                setDefaults(NotificationCompat.DEFAULT_ALL)
            }
        if (!ongoing) {
            builder.setFullScreenIntent(fullScreenPendingIntent, true)
        }
        val notification: Notification = builder.build()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "FACT_SERVICE", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(1, notification)
    }

    private fun dismissNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
    }

}

