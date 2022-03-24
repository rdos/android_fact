package ru.smartro.worknote

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.yandex.mapkit.MapKitFactory
import io.realm.Realm
import io.realm.RealmConfiguration
import io.sentry.Sentry
import io.sentry.SentryLevel
import io.sentry.SentryOptions.BeforeBreadcrumbCallback
import io.sentry.android.core.SentryAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.smartro.worknote.abs.FloatCool
import ru.smartro.worknote.andPOIntD.AndRoid
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.di.viewModelModule
import ru.smartro.worknote.log.AAct
import ru.smartro.worknote.log.AApp
import ru.smartro.worknote.log.AppParaMS
import ru.smartro.worknote.work.ac.StartAct
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


public const val TIME_OUT = 240000L
private var mAppliCation: App? = null
//todo: !r_dos true-falseTYPE:
//fun isSaveGPS(): Boolean { isDevelMODE и стандарт
//
private const val CHANNEL_ID = "M_CH1_ID"
class App : AApp() {
    companion object {
        fun getAppliCation(): App = mAppliCation!!
        fun getAppParaMS(): AppParaMS = getAppliCation().aPPParamS
        fun getMethodMan(): String? {
            return getAppliCation().mMethodName
        }
//    private fun getLocationMAN(): LocationManager {
//        return getLocationService()!!
//    }
    }
    var LASTact: AAct? = null

//  todo: r_dos, use isShowForUser :: e.b.a
    var LocationLAT: DoubleCool = Dnull
    var LocationLONG: DoubleCool = Dnull

    //todo: !r_dos find fun gpSPoinT()



    private var GPSpoinT: AndRoid.PoinT? = null
        get() {
            field?.let {
                return field
            }
            return getAppParaMS().getAlwaysGPS()
        }
        set(value) { field = value }

    fun GPS(): AndRoid.PoinT {
        return GPS
    }

    private lateinit var GPS: AndRoid.PoinT
    //todo: !r_dos find fun gpSPoinT()
    var LocationTIME: LongCool = System.currentTimeMillis()
                    //todo: !r_dos find fun gpSPoinT()
    var LocationACCURACY = FloatCool("LocationACCURACY", this)
//todo: r_dos, use isShowForUser :: e.b.a
    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey(getString(R.string.yandex_map_key))
        MapKitFactory.initialize(this)
        MapKitFactory.getInstance().createLocationManager()

        mAppliCation = this
        Log.i(TAG, "on App created App.onCreate onAppCreate")
        initSentry()
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


    inner class MyLocationListener() : LocationListener {
        override fun onProviderEnabled(provider: String) {
            before("onProviderEnabled")
            LOGWork("provider", provider)
            after()
        }

        override fun onProviderDisabled(provider: String) {
            before("onProviderDisabled")
            LOGWork("provider", provider)
            after()
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onLocationChanged(location: Location) {
            before("onLocationChanged")

            LocationACCURACY.setDATAing(location.accuracy)
            LOGWork("LocationLAT=${LocationLAT}")
            LocationLAT = location.latitude
            LOGWork("LocationLONG=${LocationLONG}")
            LocationLONG = location.longitude
            LOGWork("LocationTIME=${LocationTIME}")
            LocationTIME = location.time

//            GPS = AndRoid.PoinT(LocationLAT, LocationLONG, LocationTIME, LocationACCURACY)

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
            GPS = AndRoid.PoinT(yandexLAT?:LocationLAT,
                yandexLONG?:LocationLONG,
                yandexTIME?:LocationTIME,
                yandexACCURACYL?:LocationACCURACY.VAL.toDouble())
            if (GPS.isSaveGPS()) {
                getAppParaMS().addParamLast12nowGPS(LocationLAT, LocationLONG, LocationTIME, LocationACCURACY)
                GPSpoinT =  getAppParaMS().getLastGPS()

                LASTact?.onNEWfromGPSSrv()
            }

            after()
        }

    }

    override fun onTerminate() {
        super.onTerminate()
        before("onTerminate")
    }

    private fun initRealm() {
        Realm.init(this@App)
        val config = RealmConfiguration.Builder()
        config.allowWritesOnUiThread(true)
        config.name("FACT.realm")
        config.deleteRealmIfMigrationNeeded()
        Realm.setDefaultConfiguration(config.build())
    }



    public fun showNotification(textContent: String, textTitle: String) {
        val notificationManager = NotificationManagerCompat.from(this)
        if (notificationManager.notificationChannels.size <= 0) {
            showNotificationForce(textContent, textTitle)
        }
    }

    public fun showNotificationForce(textContent: String, textTitle: String, id: Int = 1){
        val notificationManager = NotificationManagerCompat.from(this)

//        val fullScreenIntent = Intent(this, StartAct::class.java)
//        fullScreenIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        val fullScreenPendingIntent =
//            PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .run {
//                setSmallIcon(R.drawable.ic_app)
//                setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_app))
//                setContentTitle(title)
//                setContentText(content)
//                setOngoing(true)
//                priority = NotificationCompat.PRIORITY_MAX
//                setDefaults(NotificationCompat.DEFAULT_ALL)
//                setContentIntent(fullScreenPendingIntent)
//
//                setShowWhen(true)
//            }
////        <!--<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />-->
////        todo: !R_dos
//        builder.setFullScreenIntent(fullScreenPendingIntent, true)
//        builder.setStyle(androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle()
//            .setMediaSession(MediaSessionCompat(this, CHANNEL_ID).getSessionToken()))
//
//        val notification: Notification = builder.build()
//        notificationManager.notify(id, notification)
//



//        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_app)
//            .setContentTitle(textTitle)
//            .setContentText(textContent)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//        notificationManager.notify(id, builder.build())


//        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        val notification = Notification((R.drawable.ic_app, textTitle, textContent)
//        val notificationIntent = Intent(this, StartAct::class.java)
//        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
//                or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        val intent = PendingIntent.getActivity(
//            this, 0,
//            notificationIntent, 0
//        )
//        notification.setLatestEventInfo(context, title, message, intent)
//        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
//        notificationManager.notify(0, notification)

//        val notificationLayout = RemoteViews(packageName, R.layout.app_notification_small)
//        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.app_notification_small)
//
//// Apply the layouts to the notification
//        val customNotification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_app)
//            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
//            .setCustomContentView(notificationLayout)
//            .setCustomBigContentView(notificationLayoutExpanded)
//            .build()

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
    }

    public fun cancelNotification(id: Int? = 1) {
        Log.d(TAG, "cancelNotification.before")
        val notificationManager = NotificationManagerCompat.from(this)
        if (id == null) {
            notificationManager.cancelAll()
            LOGWork("cancelNotification.notificationChannels.size=${notificationManager.notificationChannels.size}")
            return
        }
        notificationManager.cancel(id)
        LOGWork("cancelNotification.notificationChannels.size=${notificationManager.notificationChannels.size}")
    }


    fun startLocationService(isForceMode: Boolean=false) {
        before("runLocationService")
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            LOGWork("ActivityCompat.checkSelfPermission = true")
            return
        }


        if(!isForceMode && getAppParaMS().isModeLOCATION) {
            LOGWork("getAppParaMS().isModeLOCATION=true")
            after()
            return
        }
//todo:        getAp
        val providerName = AndRoid.getProviderName()
        LOGWork(providerName!!)
        AndRoid.getService().requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            300,
            100F,
            // override fun onLocationChanged(location: Location) {
            MyLocationListener()
        ) // здесь можно указать другие более подходящие вам параметры


//        var gps_enabled = false
//        var network_enabled = false
//        val lm = AndRoid.getService()
//        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//        var net_loc: Location? = null
//        var gps_loc: Location? = null
//        var finalLoc: Location? = null
//        if (gps_enabled) gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//        if (network_enabled) net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//        if (gps_loc != null && net_loc != null) {
//            //smaller the number more accurate result will
//            finalLoc = if (gps_loc.accuracy > net_loc.accuracy) net_loc else gps_loc
//            // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)
//        } else {
//            if (gps_loc != null) {
//                finalLoc = gps_loc
//            } else if (net_loc != null) {
//                finalLoc = net_loc
//            }
//        }

//        mLocationManager = mMapKit.createLocationManager()
//      .subscribeForLocationUpdates(0.0, 500, 0.0, true, FilteringMode.OFF, null)

        getAppParaMS().isModeLOCATION = true
        after()
    }


    fun startWorkER() {
        before("runSyncWorkER")
        if(getAppParaMS().isModeWorkER) {
            LOGWork("getAppParaMS().isModeWorkER=true")
            after()
            return
        }
//todo:        getAppParaMS().isModeWorkER = true
        getAppParaMS().isModeWorkER = true

        val uploadDataWorkManager = PeriodicWorkRequestBuilder<SYNCworkER>(1, TimeUnit.MINUTES).build()
//        uploadDataWorkManager.id = UUID
        LOGWork("uploadDataWorkManager.id = ${uploadDataWorkManager.id}")
        val operation =  WorkManager.getInstance(getAppliCation())
            .enqueueUniquePeriodicWork("SYNCworkER", ExistingPeriodicWorkPolicy.REPLACE, uploadDataWorkManager)

        // TODO: THIS!!! after(isModeWorkER)

        after()
    }

    fun stopWorkERS() {
        WorkManager.getInstance(this).cancelAllWork()
        getAppParaMS().isModeWorkER = false

    }



        private fun initSentry() {
            Sentry.init { options ->
                options.dsn = getString(R.string.sentry_url)
            }
            Sentry.configureScope { scope ->
                scope.level = SentryLevel.WARNING
            }
            Sentry.setTag("device", MyUtil.getDeviceName()!!)
            Sentry.setTag("android_api", android.os.Build.VERSION.SDK_INT.toString())

            SentryAndroid.init(this) { options ->
                options.beforeBreadcrumb = BeforeBreadcrumbCallback { breadcrumb, _ ->
                    if ("a.spammy.Logger" == breadcrumb.category) {
                        null
                    } else {
                        breadcrumb
                    }
                }
            }
        }


}


const val A_SLEEP_TIME_1_83__MS = 180000L
const val Snull = "rNull"
const val Inull = -111
const val Lnull = 999222333L
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

