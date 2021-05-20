package ru.smartro.worknote.work

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
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.smartro.worknote.R
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.RealmRepository
import ru.smartro.worknote.service.network.NetworkRepository
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.ui.map.MapActivity
import ru.smartro.worknote.util.MyUtil
import java.util.*
import kotlin.concurrent.fixedRateTimer


class SynchronizeWorker(
    private val appContext: Context,
    params: WorkerParameters
) : Worker(appContext, params) {

    private val TAG = "UploadDataWorkManager"
    private val network = NetworkRepository(applicationContext)
    private val deviceId = Secure.getString(appContext.contentResolver, Secure.ANDROID_ID)


    override fun doWork(): Result {
        showNotification(appContext, true, "Не закрывайте приложение", "Служба отправки данных работает")
        fixedRateTimer("timer", false, 0L, 1.min()) {
            Log.d(TAG, " TIMER ALARM")
            synchronizeData()
        }
        Log.d(TAG, "RETURN SUCCESS")
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        Log.d(TAG, "onStopped: WHEN ${Calendar.getInstance().time}")
    }

    @SuppressLint("MissingPermission")
    private fun synchronizeData() {
        CoroutineScope(Dispatchers.IO).launch {
            var long = 0.0
            var lat = 0.0
            if (AppPreferences.currentCoordinate!!.contains("#")) {
                long = AppPreferences.currentCoordinate!!.substringAfter("#").toDouble()
                lat = AppPreferences.currentCoordinate!!.substringBefore("#").toDouble()
            }
            val timeBeforeRequest = MyUtil.timeStamp()
            Log.d(TAG, " SYNCHRONIZE STARTED")
            val db = RealmRepository(Realm.getDefaultInstance())
            val platforms = db.findAllPlatforms()
            val synchronizeBody = SynchronizeBody(AppPreferences.wayListId, listOf(lat, long), deviceId, platforms)
            val result = network.synchronizeData(synchronizeBody)
            when (result.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "SYNCHRONIZE SUCCESS: ${Gson().toJson(result.data)}")
                    AppPreferences.lastSynchroTime = timeBeforeRequest
                    showNotification(appContext, false, "ТЕСТ", "ТЕСТОВИЧ")
                }
                Status.ERROR -> Log.d(TAG, "SYNCHRONIZE GPS SENT ERROR")
                Status.EMPTY -> Log.d(TAG, "SYNCHRONIZE SENT EMPTY")
                Status.NETWORK -> Log.e(TAG, "SYNCHRONIZE  SENT NO INTERNET")
            }
            this.cancel()
            return@launch
        }
    }

    private fun Int.min(): Long {
        return (this * 30) * 1000L
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
                setFullScreenIntent(fullScreenPendingIntent, true)
            }
        val notification: Notification = builder.build()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "FACT_SERVICE", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(1, notification)
    }

}

