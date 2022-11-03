package ru.smartro.worknote

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.*
import android.os.StrictMode.ThreadPolicy
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.android.LogcatAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.FileAppender
import com.yandex.mapkit.MapKitFactory
import io.realm.Realm
import io.realm.RealmConfiguration
import io.sentry.Sentry
import io.sentry.SentryLevel
import io.sentry.SentryOptions.BeforeBreadcrumbCallback
import io.sentry.android.core.SentryAndroid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.slf4j.LoggerFactory
import ru.smartro.worknote.abs.AAct
import ru.smartro.worknote.andPOintD.AViewModel
import ru.smartro.worknote.andPOintD.AndRoid
import ru.smartro.worknote.andPOintD.FloatCool
import ru.smartro.worknote.andPOintD.PoinT
import ru.smartro.worknote.awORKOLDs.extensions.WarningType
import ru.smartro.worknote.awORKOLDs.extensions.showDlgWarning
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.log.AApp
import ru.smartro.worknote.presentation.ac.AirplanemodeIntentService
import ru.smartro.worknote.presentation.ac.MainAct
import ru.smartro.worknote.presentation.ac.StartAct
import ru.smartro.worknote.work.ConfigName
import ru.smartro.worknote.work.NetworkRepository
import ru.smartro.worknote.work.RealmRepository
import ru.smartro.worknote.work.RegionEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet


//INSTANCE
// TODO: service locator паттерн альтернатива DI
private var INSTANCE: App? = null
const val FN__REALM = "FACT.realm"
const val FN__REALM_VERSION = 1L
const val D__LOGS = "logs"
const val D__R_DOS = "r_dos"
const val D__FILES = "files"

class ConnectionLostLiveData(context: Context) : LiveData<Boolean>(), AndroidNet.CallBack {
    private var mNetworkCallback: ConnectivityManager.NetworkCallback? = null
    private val cm = context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager

    override fun onActive() {
        LOG.debug("before")
        mNetworkCallback = AndroidNet(cm, this)

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        if(mNetworkCallback == null) {
            LOG.warn(" if(mNetworkCallback == null) {")
            return
        }
        cm.registerNetworkCallback(networkRequest, mNetworkCallback!!)
    }

    override fun onInactive() {
        LOG.debug("before")
        if(mNetworkCallback != null) {
            LOG.info("if(mNetworkCallback != null)")
            cm.unregisterNetworkCallback(mNetworkCallback!!)
        }
    }

    override fun onLostInternet() {
        LOG.debug("before")
        postValue(true)
    }

}

class AndroidNet(val p_cm: ConnectivityManager, val p_callback: CallBack) : ConnectivityManager.NetworkCallback() {
    private val validNetworks: MutableSet<Network> = HashSet()
    override fun onAvailable(network: Network) {
        LOG.debug("before: network=${network}, validNetworks size=${validNetworks.size}")
        val networkCapabilities = p_cm.getNetworkCapabilities(network)
        val isInternet = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        if(isInternet == true) {
            LOG.info("if(isInternet == true)")
            validNetworks.add(network)
        }
        checkValidNetworks()
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        LOG.debug("before::: validNetworks size=${validNetworks.size}")
        validNetworks.remove(network)
        checkValidNetworks()
    }

    private fun checkValidNetworks() {
        LOG.debug("before::: validNetworks size=${validNetworks.size}")
        if (validNetworks.size <= 0) {
            LOG.trace("if (validNetworks.size <= 0) {")
            LOG.info("onLostInternet")
            p_callback.onLostInternet()
            LOG.debug("onLostInternet")
        }
    }
    interface  CallBack {
        fun onLostInternet()
    }
}

class App : AApp() {

    private var mCurrentAct: AAct? = null
    private lateinit var connectionLiveData: ConnectionLostLiveData

    fun setCurrentAct(aAct: AAct?) {
        mCurrentAct = aAct
    }

    fun getCurrentAct(): AAct? {
        return mCurrentAct
    }

    companion object {
//        internal lateinit var INSTANCE: App
//            private set
        fun getAppliCation(): App = INSTANCE!!

        fun getAppParaMS(): AppParaMS = getAppliCation().aPPParamS
        fun getMethodMan(): String? {
            return "MethodMan"
//            return getAppliCation().mMethodName
        }
//    private fun getLocationMAN(): LocationManager {
//        return getLocationService()!!
//    }
    }

    private var mNetworkDat: NetworkRepository? = null
    private var mDB: RealmRepository? = null

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onLowMemory() {
        super.onLowMemory()
        applicationScope.cancel()
    }

    fun restartApp() {
        val mStartActivity = Intent(baseContext, StartAct::class.java)
        val mPendingIntentId = BuildConfig.VERSION_CODE
        val mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = baseContext.getSystemService(ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, mPendingIntent);
        System.exit(0)
    }

    fun gps(): PoinT {
        var gps_enabled = false
        var network_enabled = false
        val lm = AndRoid.getService()
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var net_loc: Location? = null
        var gps_loc: Location? = null
        var finalLoc: Location? = null
        try {
            if (gps_enabled) {
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            if (network_enabled) {
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
        } catch (ex: Exception) {

        }

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
    private var mSystemUncaughtHandler: Thread.UncaughtExceptionHandler? = null

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        mSystemUncaughtHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            LOG.warn("exTHR")
            LOG.error("exTHR", throwable)
//            todo: start THRActivity (oops for user)))
            mSystemUncaughtHandler?.uncaughtException(thread, throwable)
            throw throwable
        }

        connectionLiveData = ConnectionLostLiveData(applicationContext)
        LOG.debug("connectionLiveData = ConnectionLiveData(applicationContext)")

        connectionLiveData.observeForever { isConnectionLost ->

            if(isConnectionLost == true) {
                mCurrentAct?.showDlgWarning(WarningType.CONNECTION_LOST)
            }
        }

//        val context = LoggerFactory.getILoggerFactory() as LoggerContext
//        for (logger in context.loggerList) {
//            val index = logger.iteratorForAppenders()
//            while (index.hasNext()) {
//                val appender = index.next()
//                if (appender is FileAppender<*>) {
//                    val file = getF(D__LOGS, "file.log")
//                    (appender as FileAppender<*>).file = "/data/data/ru.smartro.worknote/logs/log.log"
//                    appender.start()
//                }
//            }
//        }

        logbackInit()
        MapKitFactory.setApiKey(getString(R.string.yandex_map_key))
        MapKitFactory.initialize(this)
//        MapKitFactory.getInstance().createLocationManager()

        LOG.info("on App created App.onCreate onAppCreate")
        sentryInit()
        realmInit()
//        try {    // Add a breadcrumb that will be sent with the next event(s)//            throw Exception("This is a devel.")//        } catch (e: Exception) {
//            Sentry.captureException(e) //        }                                             //        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat( //            mLlcMap, "alpha", 0f
//        ) //        objectAnimator.setDuration(4000);
//        objectAnimator.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator?) {//                super.onAnimationEnd(animation)//                mLlcMap.isVisible=false//            }
//        })//        val animatorSet = AnimatorSet()//        animatorSet.playTogether(objectAnimator)//        animatorSet.start()
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val configEntity = getDB().loadConfig(ConfigName.RUNAPP_CNT)
        configEntity.cntPlusOne()
        getDB().saveConfig(configEntity)

        LOG.info("DEBUG::: Current Realm Schema Version : ${Realm.getDefaultInstance().version}")

        registerReceiver(mAirplaneModeStateReceiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))
    }

    private fun clearLogbackDirectory(maxHistoryFileCount: Int = 5){
        val logsFilesArray = this.getD(D__LOGS).listFiles()

        if (logsFilesArray != null) {
            val delCount = logsFilesArray.size - maxHistoryFileCount-1
            if (delCount <= 0) {
                return
            }
            val logsFilesList = mutableListOf<File>()
            for (idx in logsFilesArray.indices) {
                logsFilesList.add(logsFilesArray[idx])
            }

            logsFilesList.sortedBy { it.name }

            for (idx in 0 until delCount) {
                logsFilesList[idx].delete()
            }
        }
    }

    private fun logbackInit() {
        clearLogbackDirectory()
        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        val lc = LoggerFactory.getILoggerFactory() as LoggerContext
        lc.stop()

        // setup FileAppender
        val encoder1 = PatternLayoutEncoder()
        encoder1.context = lc
        encoder1.pattern = "VT---%d{HH:mm:ss.SSS}[%thread]%-5level[%relative] %logger{41}[%line]:%method:: %msg%n"
        encoder1.start()
        val fileAppender = FileAppender<ILoggingEvent>()
        fileAppender.context = lc

        val file = getF(D__LOGS, "${MyUtil.currentTime()}.log")
        fileAppender.file = file.absolutePath
        fileAppender.encoder = encoder1
        fileAppender.start()

        val logcatAppender = LogcatAppender()
        logcatAppender.context = lc
        logcatAppender.encoder = encoder1
        logcatAppender.start()

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        val root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        root.level = Level.TRACE
        root.addAppender(fileAppender)
        root.addAppender(logcatAppender)
    }


    inner class MyLocationListener() : LocationListener {
        override fun onProviderEnabled(provider: String) {
            LOG.debug("onProviderEnabled")
            LOG.debug("provider=${provider}")
            LOG.debug("after")
        }

        override fun onProviderDisabled(provider: String) {
            LOG.debug("onProviderDisabled")
            LOG.debug("provider=${provider}")
            LOG.debug("after")
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            LOG.debug("onProviderStatusChanged")
            LOG.debug("provider=${provider}, status=${status}")
            LOG.debug("after")
        }

        override fun onLocationChanged(location: Location) {
            LOG.debug("onLocationChanged")

            val LocationACCURACY = FloatCool("LocationACCURACY", this@App)
            LocationACCURACY.setDATAing(location.accuracy)


            val LocationLAT = location.latitude
            LOG.debug("LocationLAT=${LocationLAT}")
            val LocationLONG = location.longitude
            LOG.debug("LocationLONG=${LocationLONG}")
            val LocationTIME = location.time
            LOG.debug("LocationTIME=${LocationTIME}")

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
                        if (mCurrentAct is MainAct) {
                            mCurrentAct?.onNewGPS()
                        }
                    } catch (ex: Exception) {
                        logSentry("Exception!!! mCurrentAct?.onNEWfromGPSSrv()")
                        LOG.debug("Exception!!! mCurrentAct?.onNEWfromGPSSrv()")
                    }
//                }
            }
            LOG.debug("after")
        }

    }

    override fun onTerminate() {
        super.onTerminate()
        LOG.debug("onTerminate")
    }
    //Реплику: gjпох
    public fun getDB(): RealmRepository {
        if(mDB == null) {
//            initRealm()
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


    private fun realmInit() {
        Realm.init(this@App)
        val config = RealmConfiguration.Builder()
        config.allowWritesOnUiThread(true)
        config.name(FN__REALM)
        config.deleteRealmIfMigrationNeeded()

        config.initialData {
            LOG.debug("::: INITIAL before")

            try {
                val jsonInputStream = applicationContext.resources.openRawResource(R.raw.regions)

                it.createAllFromJson(RegionEntity::class.java, jsonInputStream)
            } catch(e : Exception) {
                LOG.error("::: INITIAL EXCEPTION: ${e.stackTraceToString()}")
            }

            LOG.debug("::: INITIAL after")
        }

        Realm.setDefaultConfiguration(config.build())
        //gjпох
    }

    fun startVibrateService(ms: Long = 80, amplitude: Int = 160) {
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator

        // Vibrate for 500 milliseconds
        //private var vibrationEffect = VibrationEffect.createOneShot(100, 128)
        val ve = VibrationEffect.createOneShot(ms, amplitude)
        v.vibrate(ve)
    }

    fun startVibrateServicePredefined(predefined: Int) {
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
        v.vibrate(VibrationEffect.createPredefined(predefined))
    }

    fun startVibrateServiceHaptic() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            startVibrateServicePredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
        else
            startVibrateService()
    }

    fun showNotification(pendingIntent: PendingIntent, contentText: String, title: String) {
        val notificationManager = NotificationManagerCompat.from(this)
        if (notificationManager.notificationChannels.size <= 0) {
            LOG.debug("showNotification.textContent={$contentText}")
            showNotificationForce(pendingIntent, contentText, title)
        } else {
//            logSentry("showNotification. notificationManager.notificationChannels.size = ${notificationManager.notificationChannels.size}")
        }
    }

    fun logSentry(text: String) {
        Sentry.addBreadcrumb("${TAG} : $text")
        LOG.debug("${text}")
    }

    fun showAlertNotification(message: String) {
        LOG.debug("showAlertNotification.textContent={$message}")
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
        LOG.debug("showNotificationForce.textContent={$textContent}")

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
        LOG.debug("notifyId=${notifyId}")
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
        LOG.debug("cancelNotification.before")
        val notificationManager = NotificationManagerCompat.from(this)
        if (id == null) {
            notificationManager.cancelAll()
            LOG.debug("cancelNotification.notificationChannels.size=${notificationManager.notificationChannels.size}")
            return
        }
        notificationManager.cancel(id)
        LOG.debug("cancelNotification.notificationChannels.size=${notificationManager.notificationChannels.size}")
    }


    fun startLocationService(isForceMode: Boolean=false) {
        LOG.debug("runLocationService")
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            LOG.debug("ActivityCompat.checkSelfPermission = true")
            return
        }


        if(!isForceMode && getAppParaMS().isModeLOCATION) {
            LOG.debug("getAppParaMS().isModeLOCATION=true")
            LOG.debug("after")
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
        LOG.debug("after")
    }


    fun startWorkER() {
        LOG.debug("runSyncWorkER")
        if(getAppParaMS().isModeWorkER) {
            LOG.debug("getAppParaMS().isModeWorkER=true")
            LOG.debug("after")
            return
        }
//todo:        getAppParaMS().isModeWorkER = true
        getAppParaMS().isModeWorkER = true

        val uploadDataWorkManager = PeriodicWorkRequestBuilder<SYNCworkER>(1, TimeUnit.MINUTES).build()
//        uploadDataWorkManager.id = UUID
        LOG.debug("uploadDataWorkManager.id = ${uploadDataWorkManager.id}")
        val operation =  WorkManager.getInstance(getAppliCation())
            .enqueueUniquePeriodicWork("SYNCworkER", ExistingPeriodicWorkPolicy.REPLACE, uploadDataWorkManager)

        // TODO: THIS!!! after(isModeWorkER)

        LOG.debug("after")
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
    private val mAirplaneModeStateReceiver by lazy { getAirplaneModeBroadcastReceiver() }

    private fun getAirplaneModeBroadcastReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                if (intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                    val isAirplaneModeEnabled = intent.getBooleanExtra("state", false)
                    LOG.debug("isAirplaneModeEnabled=${isAirplaneModeEnabled}")
                    val serviceIntent = Intent(context, AirplanemodeIntentService::class.java)
                    serviceIntent.putExtra("isAirplaneModeEnabled", isAirplaneModeEnabled)
                    context.startService(serviceIntent)

                    if (isAirplaneModeEnabled) {
                        mCurrentAct?.showDlgWarning(WarningType.AIRPLANE_MODE)
                    }
                }
            }
        }
    }
}

const val TIME_OUT = 240000L
private const val NOTIFICATION_CHANNEL_ID__DEFAULT = "FACT_CH_ID"
const val NOTIFICATION_CHANNEL_ID__MAP_ACT = "FACT_APP_CH_ID"

//DownloadManager
//WorkManager

//AlarmMan
val PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
//    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.READ_PHONE_STATE,
    Manifest.permission.LOCATION_HARDWARE,
    Manifest.permission.ACCESS_NETWORK_STATE,
    Manifest.permission.CAMERA,
    Manifest.permission.SYSTEM_ALERT_WINDOW,
    Manifest.permission.RECORD_AUDIO

)

//todo:const val A_SLEEP_TIME_1MIN__MS = 60000L
const val Snull = "rNull"
const val Inull = -111
const val LTIMEnull = 999222333L
const val Lnull = -111111L
const val Fnull = 0.0f
const val Dnull = -222.0
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

fun Any.toast(text: String? = "") {
    try {
       Toast.makeText(App.getAppliCation().applicationContext, text, Toast.LENGTH_SHORT).show()
    } catch (ex: Exception) {
        LOG.error("eXthr", ex)
    }
}

fun AViewModel.saveJSON(bodyInStringFormat: String, p_jsonName: String) {
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


//fun App.toast(text: String? = "") {
//    try {
//        Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show()
//    } catch (e: Exception) {
//
//    }
//
//}

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

val Any.TAG: String
    get() = "${this::class.simpleName}"
val Any.LOG: org.slf4j.Logger
    get() = LoggerFactory.getLogger(TAG)

fun org.slf4j.Logger.todo(text: String? = null) {
    this.trace("TODO")
    this.info("TODO")
    this.debug("TODO:${text}")
    this.warn("TODO")
    this.error("TODO")
}


fun Any.getLogger(): org.slf4j.Logger {
   return LoggerFactory.getLogger( "${this::class.simpleName}")
}


//
//    protected fun LOG.debug(valueName: String, value: Int) {
//        LOG.debug("${valueName}=$value\"")
//    }

fun  Any.LOGinCYCLEStart(s: String) {
//        mMethodName?.let {
//            LOG.debug("${mMethodName}.CYCLes.${s}")
//            return@INcyclEStart
//        }
    LOG.debug("CYCLes.${s}")
}

fun  Any.LOGINcyclEStop() {
//        mMethodName?.let {
//            LOG.debug("${mMethodName}.************-_(:;)")
//            return@INcyclEStop
//        }
    LOG.debug(".************-_(:;)")
}