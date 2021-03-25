package ru.smartro.worknote.work

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
import ru.smartro.worknote.util.MyUtil
import java.util.*
import kotlin.concurrent.fixedRateTimer


class UploadDataWorkManager(
    private val appContext: Context,
    params: WorkerParameters
) : Worker(appContext, params) {

    private val TAG = "UploadDataWorkManager"
    private val network = NetworkRepository(applicationContext)

    override fun doWork(): Result {
        showPushNotif(appContext)
        CoroutineScope(Dispatchers.IO).launch {
            // Таймер который работает каждую минуту
            fixedRateTimer("timer", false, 0L, 1.min()) {
                AppPreferences.lastSynchronizeTime = MyUtil.timeStamp()

            }
        }
        Log.d("WORK_MANAGER", "RETURN SUCCESS")
        return Result.success()
    }


    private fun Int.min(): Long {
        return (this * 60) * 1000L
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

    private fun sendServedPoint() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = RealmRepository(Realm.getDefaultInstance())

        }
    }

    private fun sendPointProblem() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = RealmRepository(Realm.getDefaultInstance())

        }
    }

    private fun sendContainerProblem() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = RealmRepository(Realm.getDefaultInstance())

            val result = network.getCancelWayReasonNoLV()
            when (result.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "RESPONSE SUCCESS: ${Gson().toJson(result.data)}")
                }
                Status.ERROR -> {
                    Log.d(TAG, "RESPONSE ERROR")
                }
                Status.EMPTY -> {
                    Log.d(TAG, "RESPONSE EMPTY")
                }
                Status.NETWORK -> TODO()
            }
        }
    }
}

