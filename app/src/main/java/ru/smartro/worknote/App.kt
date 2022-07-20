package ru.smartro.worknote

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.yandex.mapkit.MapKitFactory
import io.realm.*
import io.sentry.Sentry
import io.sentry.SentryLevel
import io.sentry.SentryOptions.BeforeBreadcrumbCallback
import io.sentry.android.core.SentryAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.smartro.worknote.andPOintD.FloatCool
import ru.smartro.worknote.andPOintD.AndRoid
import ru.smartro.worknote.andPOintD.PoinT
import ru.smartro.worknote.work.NetworkRepository
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.awORKOLDs.adapter.viewModelModule
import ru.smartro.worknote.log.AAct
import ru.smartro.worknote.log.AApp
import ru.smartro.worknote.work.RealmRepository
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit



//INSTANCE
private var INSTANCE: App? = null

class App : AApp() {
    companion object {
//        internal lateinit var INSTANCE: App
//            private set
        fun getAppliCation(): App = INSTANCE!!

        fun getAppParaMS(): AppParaMS = getAppliCation().aPPParamS
        fun getMethodMan(): String? {
            return getAppliCation().mMethodName
        }
//    private fun getLocationMAN(): LocationManager {
//        return getLocationService()!!
//    }
    }

    private var mNetworkDat: NetworkRepository? = null
    private var mDB: RealmRepository? = null
    var LASTact: AAct? = null




    fun gps(): PoinT {
        var gps_enabled = false
        var network_enabled = false
        val lm = AndRoid.getService()
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var net_loc: Location? = null
        var gps_loc: Location? = null
        var finalLoc: Location? = null

        if (gps_enabled) gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (network_enabled) net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (gps_loc == null && net_loc == null) {
            return getAppParaMS().getSaveGPS()
        }

        if (gps_loc != null && net_loc != null) {
            //smaller the number more accurate result will
            finalLoc = if (gps_loc.accuracy > net_loc.accuracy) net_loc else gps_loc
            // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)
        } else {
            if (gps_loc != null) {
                finalLoc = gps_loc
            } else if (net_loc != null) {
                finalLoc = net_loc
            }
        }

        mGPS = PoinT.fromLocation(finalLoc)
        return mGPS
    }

    private var mGPS: PoinT = PoinT()

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        MapKitFactory.setApiKey(getString(R.string.yandex_map_key))
        MapKitFactory.initialize(this)
//        MapKitFactory.getInstance().createLocationManager()

        Log.i(TAG, "on App created App.onCreate onAppCreate")
        sentryInit()
        initRealm()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(viewModelModule))
        }
//        try {    // Add a breadcrumb that will be sent with the next event(s)//            throw Exception("This is a devel.")//        } catch (e: Exception) {
//            Sentry.captureException(e) //        }                                             //        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat( //            mLlcMap, "alpha", 0f
//        ) //        objectAnimator.setDuration(4000);
//        objectAnimator.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator?) {//                super.onAnimationEnd(animation)//                mLlcMap.isVisible=false//            }
//        })//        val animatorSet = AnimatorSet()//        animatorSet.playTogether(objectAnimator)//        animatorSet.start()
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

    }

    private var mCicerone: Cicerone<Router>? = null
    private fun getCicerone(): Cicerone<Router> {
        if (mCicerone == null) {
            mCicerone = Cicerone.create()
        }
        return mCicerone!!
    }
    fun getNavigatorHolder(): NavigatorHolder {
        return getCicerone().navigatorHolder
    }

    fun getRouter(): Router {
        return getCicerone().router
    }

    inner class MyLocationListener() : LocationListener {
        override fun onProviderEnabled(provider: String) {
            beforeLOG("onProviderEnabled")
            log("provider=${provider}")
            LOGafterLOG()
        }

        override fun onProviderDisabled(provider: String) {
            beforeLOG("onProviderDisabled")
            log("provider=${provider}")
            LOGafterLOG()
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            beforeLOG("onProviderStatusChanged")
            log("provider=${provider}, status=${status}")
            LOGafterLOG()
        }

        override fun onLocationChanged(location: Location) {
            beforeLOG("onLocationChanged")

            val LocationACCURACY = FloatCool("LocationACCURACY", this@App)
            LocationACCURACY.setDATAing(location.accuracy)


            val LocationLAT = location.latitude
            log("LocationLAT=${LocationLAT}")
            val LocationLONG = location.longitude
            log("LocationLONG=${LocationLONG}")
            val LocationTIME = location.time
            log("LocationTIME=${LocationTIME}")

            var yandexLAT: Double? = null
            var yandexLONG: Double? = null
            var yandexTIME: Long? = null
            var yandexACCURACYL: Double? = null

//            try {
//                yandexLAT = LocationManagerUtils.getLastKnownLocation()?.position?.latitude
//                yandexLONG = LocationManagerUtils.getLastKnownLocation()?.position?.longitude
//                yandexTIME = LocationManagerUtils.getLastKnownLocation()?.absoluteTimestamp
//                yandexACCURACYL = LocationManagerUtils.getLastKnownLocation()?.accuracy
//            } catch (ex: Throwable) {
//
//            }
//            GPS = AndRoid.PoinT(yandexLAT?:LocationLAT,
//                yandexLONG?:LocationLONG,
//                yandexTIME?:LocationTIME,
//                yandexACCURACYL?:LocationACCURACY.VAL.toDouble())

            //OLD DATA Принцип разделения интерфейсов говорит о том, что слишком «толстые» интерфейсы необходимо разделять на более маленькие и специфические, чтобы программные сущности маленьких интерфейсов знали только о методах, которые необходимы им в работе.
            // В итоге, ПО НЕ ИМЕЕТ ШНАСА УЗНАТЬ О коллеках)) И КАК СЛЕД.СТВИЕ)) yes при изменении метода интерфейса не должны меняться СОпрограммные сущности
            // , которые этот метод не используют.
            if (getAppParaMS().iSoldGPSdataSaved()) {
//                if (getAppParaMS().isOldGPSbaseDate(LocationTIME)) {
                    getAppParaMS().saveLastGPS(LocationLAT, LocationLONG, LocationTIME, LocationACCURACY.LET)
                    try {
                        LASTact?.onNEWfromGPSSrv()
                    } catch (ex: Exception) {
                        logSentry("Exception!!! LASTact?.onNEWfromGPSSrv()")
                        log("Exception!!! LASTact?.onNEWfromGPSSrv()")
                    }
//                }
            }
            LOGafterLOG()
        }

    }

    override fun onTerminate() {
        super.onTerminate()
        beforeLOG("onTerminate")
    }
    //Реплику: gjпох
    public fun getDB(): RealmRepository {
        if(mDB == null) {
            initRealm()
            mDB = RealmRepository(Realm.getDefaultInstance())
        }
      return mDB!!
    }
    //getNetWork
    public fun getNetwork(): NetworkRepository {
        if(mNetworkDat == null) {
            mNetworkDat = NetworkRepository(this)
        }
        return mNetworkDat!!
    }


    private fun initRealm() {
        Realm.init(this@App)
        val config = RealmConfiguration.Builder()
        config.allowWritesOnUiThread(true)
        config.name("FACT.realm")
        config.deleteRealmIfMigrationNeeded()
        Realm.setDefaultConfiguration(config.build())
        //gjпох
    }


    fun showNotification(pendingIntent: PendingIntent, contentText: String, title: String) {
        val notificationManager = NotificationManagerCompat.from(this)
        if (notificationManager.notificationChannels.size <= 0) {
            log("showNotification.textContent={$contentText}")
            showNotificationForce(pendingIntent, contentText, title)
        } else {
//            logSentry("showNotification. notificationManager.notificationChannels.size = ${notificationManager.notificationChannels.size}")
        }
    }

    protected fun logSentry(text: String) {
        Sentry.addBreadcrumb("${TAG} : $text")
        log("${text}")
    }

    fun showAlertNotification(message: String) {
        log("showAlertNotification.textContent={$message}")
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID__MAP_ACT)

        val customView = RemoteViews(packageName, R.layout.notification_alert).apply {
            setTextViewText(R.id.alert_title, "Оповещение")
            setTextViewText(R.id.alert_message, message)
        }

        builder.setSmallIcon(R.drawable.ic_app)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_app))
            .setContent(customView)
            .setCustomHeadsUpContentView(customView)
            .setCustomContentView(customView)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        val notification: Notification = builder.build()
        createNotificationChannel(NOTIFICATION_CHANNEL_ID__MAP_ACT).notify(8, notification)
        Handler(Looper.getMainLooper()).postDelayed({
            (applicationContext.getSystemService(NOTIFICATION_SERVICE)
                    as NotificationManager).cancel(8)
        }, 10000)

    }

//    var alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
    fun showNotificationForce(pendingIntent: PendingIntent, textContent: String, textTitle: String,
                              actionName: String? = null,
                              notifyId: Int = 1,
                              channelId: String = NOTIFICATION_CHANNEL_ID__DEFAULT){
        log("showNotificationForce.textContent={$textContent}")

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)

        builder.setSmallIcon(R.drawable.ic_container)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_container))
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setShowWhen(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        /** Get the layouts to use in the custom notification */

//        val notificationLayout = RemoteViews(packageName, R.layout.app_notification_small)
//        notificationLayout.setTextViewText(R.id.notification_title, "Custom notification text");
//        notificationLayout.setOnClickPendingIntent(R.id.notification_title, pendingIntent);
//        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.app_notification_small)
//        builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
//        builder.setCustomContentView(notificationLayout)
//        builder.setCustomBigContentView(notificationLayoutExpanded)
        if (actionName !=null) {
            builder.addAction(R.drawable.ic_arrow_top, actionName, pendingIntent)
            builder.setAutoCancel(true)  // автоматически закрыть уведомление после нажатия
            builder.setFullScreenIntent(pendingIntent, true)
            builder.setOngoing(false)
        } else {
            builder.setOngoing(true)
        }
        val notification: Notification = builder.build()
        log("notifyId=${notifyId}")
        createNotificationChannel(channelId).notify(notifyId, notification)
    }

    private fun createNotificationChannel(channelId: String = NOTIFICATION_CHANNEL_ID__DEFAULT): NotificationManagerCompat {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManagerCompat.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager = NotificationManagerCompat.from(this)
        for (notifyChannel in notificationManager.notificationChannels) {
            if (notifyChannel.id == channelId) {
                return notificationManager
            }
        }
        notificationManager.createNotificationChannel(channel)
        return notificationManager
    }

    fun cancelNotification(id: Int? = 1) {
        Log.d(TAG, "cancelNotification.before")
        val notificationManager = NotificationManagerCompat.from(this)
        if (id == null) {
            notificationManager.cancelAll()
            log("cancelNotification.notificationChannels.size=${notificationManager.notificationChannels.size}")
            return
        }
        notificationManager.cancel(id)
        log("cancelNotification.notificationChannels.size=${notificationManager.notificationChannels.size}")
    }


    fun startLocationService(isForceMode: Boolean=false) {
        beforeLOG("runLocationService")
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            log("ActivityCompat.checkSelfPermission = true")
            return
        }


        if(!isForceMode && getAppParaMS().isModeLOCATION) {
            log("getAppParaMS().isModeLOCATION=true")
            LOGafterLOG()
            return
        }
//todo:        getAp
//        val providerName = AndRoid.getProviderName()
//        if (providerName.isNullOrEmpty()) {
//
//        }
        AndRoid.getService().requestLocationUpdates(
            AndRoid.getProviderName(),
            300,
            30F,
            // override fun onLocationChanged(location: Location) {
            MyLocationListener()
        ) // здесь можно указать другие более подходящие вам параметры




//        mLocationManager = mMapKit.createLocationManager()
//      .subscribeForLocationUpdates(0.0, 500, 0.0, true, FilteringMode.OFF, null)

        getAppParaMS().isModeLOCATION = true
        LOGafterLOG()
    }


    fun startWorkER() {
        beforeLOG("runSyncWorkER")
        if(getAppParaMS().isModeWorkER) {
            log("getAppParaMS().isModeWorkER=true")
            LOGafterLOG()
            return
        }
//todo:        getAppParaMS().isModeWorkER = true
        getAppParaMS().isModeWorkER = true

        val uploadDataWorkManager = PeriodicWorkRequestBuilder<SYNCworkER>(1, TimeUnit.MINUTES).build()
//        uploadDataWorkManager.id = UUID
        log("uploadDataWorkManager.id = ${uploadDataWorkManager.id}")
        val operation =  WorkManager.getInstance(getAppliCation())
            .enqueueUniquePeriodicWork("SYNCworkER", ExistingPeriodicWorkPolicy.REPLACE, uploadDataWorkManager)

        // TODO: THIS!!! after(isModeWorkER)

        LOGafterLOG()
    }

    fun stopWorkERS() {
        WorkManager.getInstance(this).cancelAllWork()
        getAppParaMS().isModeWorkER = false

    }


    private fun getSentryEnvironment(): String {
        var res = BuildConfig.BUILD_TYPE
        if (isDevelMode()) {
            res = "${res}_isDevelMode"
        }
        logSentry(res)
        return res
    }

    public fun sentryAddTag(key: String, value: String) {
        Sentry.configureScope { scope ->
            scope.setTag(key, value)
        }
    }

    private fun sentryInit() {
        Sentry.init { options ->
            options.dsn = getString(R.string.sentry_url)
        }

        SentryAndroid.init(this) { options ->
            options.beforeBreadcrumb = BeforeBreadcrumbCallback { breadcrumb, _ ->
                if ("a.spammy.Logger" == breadcrumb.category) {
                    null
                } else {
                    breadcrumb
                }
            }

            options.environment = getSentryEnvironment()
        }

        Sentry.configureScope { scope ->
            scope.level = SentryLevel.WARNING
        }
        sentryAddTag("user_name", getAppParaMS().userName);
        sentryAddTag("android_api", android.os.Build.VERSION.SDK_INT.toString())
        sentryAddTag("device_name", MyUtil.getDeviceName()!!)
    }

    fun isDevelMode(): Boolean {
        if (BuildConfig.BUILD_TYPE == "release") {
            return false
        }
        return App.getAppParaMS().isModeDEVEL
    }


}

const val TIME_OUT = 240000L
private const val NOTIFICATION_CHANNEL_ID__DEFAULT = "FACT_CH_ID"
const val NOTIFICATION_CHANNEL_ID__MAP_ACT = "FACT_APP_CH_ID"


val PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.READ_PHONE_STATE,
    Manifest.permission.LOCATION_HARDWARE,
    Manifest.permission.ACCESS_NETWORK_STATE,
    Manifest.permission.CAMERA
)

//todo:const val A_SLEEP_TIME_1MIN__MS = 60000L
const val Snull = "rNull"
const val Inull = -111
const val LTIMEnull = 999222333L
const val Lnull = -111111L
const val Fnull = 0.0f
const val Dnull = 0.0
const val ErrorsE = "ErrorsE"

typealias DoubleCool = Double
typealias LongCool = Long

fun String?.isShowForUser(): Boolean {
    var result = this != Snull
    if (result) {
        result = !this.isNullOrEmpty()
        if (result) return true
    }
    return false
}

fun Any.getDeviceTime(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(getDeviceDateTime())
}

fun Any.getDeviceDateTime(): Date {
    return Date()
}

fun Fragment.toast(text: String? = "") {
    try {
        Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {

    }

}

//fun App.toast(text: String? = "") {
//    try {
//        Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show()
//    } catch (e: Exception) {
//
//    }
//
//}

fun AppCompatActivity.toast(text: String? = "") {
    try {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    } catch (e: Exception) {

    }
}

/** Milliseconds used for UI animations */
fun AppCompatButton.simulateClick(delayBefore: Long = 1000L, delayAfter: Long = 1050L) {
    postDelayed({
        try {
            isPressed = true
            //                performClick()
            invalidate()
        } catch (e: Exception) {
            print(e)
        }
    }, delayBefore)
    postDelayed({
        try {
            invalidate()
            isPressed = false
        } catch (e: Exception) {
            print(e)
        }
    }, delayAfter)
}

