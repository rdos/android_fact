package ru.smartro.worknote.work

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.smartro.worknote.R
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.RealmRepository
import ru.smartro.worknote.service.network.NetworkRepository
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.util.MyUtil
import java.util.*
import kotlin.concurrent.fixedRateTimer


class SynchronizeWorker(
    private val appContext: Context,
    params: WorkerParameters
) : Worker(appContext, params) {

    private val TAG = "UploadDataWorkManager"
    private val network = NetworkRepository(applicationContext)
    private val deviceId = android.provider.Settings.Secure.getString(appContext.contentResolver, android.provider.Settings.Secure.ANDROID_ID)

    override fun doWork(): Result {
        showPushNotif(appContext)
        CoroutineScope(Dispatchers.IO).launch {
            fixedRateTimer("timer", false, 0L, 1.min()) {
                Log.d(TAG, " TIMER ALARM")
                synchronizeData()
            }
        }
        Log.d(TAG, "RETURN SUCCESS")
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        Log.d(TAG, "onStopped: WHEN ${Calendar.getInstance().time}")
    }

    private fun showPushNotif(context: Context) {
        val channelId = "M_CH_ID"
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_app)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_app))
            .setContentTitle("Служба отправки данных работает")
            .setContentText("Не закрывайте приложение")
            .setOngoing(true)

        val notification: Notification = builder.build()
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "FACT_SERVICE", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(1, notification)
    }

    @SuppressLint("MissingPermission")
    private fun synchronizeData() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = RealmRepository(Realm.getDefaultInstance())
            val platforms = db.findPlaFormsSynchronize()
            Log.d(TAG, " entity time > last update ${AppPreferences.lastUpdateTime > AppPreferences.lastSynchroTime} ${AppPreferences.lastUpdateTime}  ${AppPreferences.lastSynchroTime}")
            var long = 0.0
            var lat = 0.0
            if (AppPreferences.currentCoordinate!!.contains("#")) {
                long = AppPreferences.currentCoordinate!!.substringAfter("#").toDouble()
                lat = AppPreferences.currentCoordinate!!.substringAfter("#").toDouble()
            }
            if (AppPreferences.lastUpdateTime > AppPreferences.lastSynchroTime) {
                Log.d(TAG, " SYNCHRONIZE STARTED")
                val synchronizeBody = SynchronizeBody(AppPreferences.wayListId, lat, long, deviceId, platforms)
                val result = network.synchronizeData(synchronizeBody)
                when (result.status) {
                    Status.SUCCESS -> {
                        Log.d(TAG, "SYNCHRONIZE SUCCESS: ${Gson().toJson(result.data)}")
                        AppPreferences.lastSynchroTime = MyUtil.timeStamp()
                    }
                    Status.ERROR -> Log.d(TAG, "NOTHING CHANGED GPS SENT ERROR")
                    Status.EMPTY -> Log.d(TAG, "NOTHING CHANGED GPS SENT EMPTY")
                    Status.NETWORK -> Log.e(TAG, "NOTHING CHANGED GPS SENT NETWORK")
                }
                return@launch
            } else {
                Log.d(TAG, "NOTHING CHANGED GPS SENT")
                val synchronizeBody = SynchronizeBody(AppPreferences.wayListId, lat, long, deviceId, null)
                val result = network.synchronizeData(synchronizeBody)
                when (result.status) {
                    Status.SUCCESS -> Log.d(TAG, "NOTHING CHANGED GPS SENT SUCCESS: ${Gson().toJson(result.data)}")
                    Status.ERROR -> Log.d(TAG, "NOTHING CHANGED GPS SENT ERROR")
                    Status.EMPTY -> Log.d(TAG, "NOTHING CHANGED GPS SENT EMPTY")
                    Status.NETWORK -> Log.e(TAG, "NOTHING CHANGED GPS SENT NETWORK")
                }
                return@launch
            }

        }
    }

    private fun Int.min(): Long {
        return (this * 10) * 1000L
    }

}

