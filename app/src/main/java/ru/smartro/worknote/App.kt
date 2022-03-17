package ru.smartro.worknote

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.LocationManagerUtils
import io.realm.Realm
import io.realm.RealmConfiguration
import io.sentry.Sentry
import io.sentry.SentryLevel
import io.sentry.SentryOptions.BeforeBreadcrumbCallback
import io.sentry.android.core.SentryAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import ru.smartro.worknote.di.viewModelModule
import ru.smartro.worknote.util.MyUtil
import ru.smartro.worknote.work.abs.AAct
import ru.smartro.worknote.work.abs.AApp
import ru.smartro.worknote.work.abs.FloatCool
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


public const val TIME_OUT = 240000L
private var mAppliCation: App? = null

private const val NAME = ""
private const val MODE = Context.MODE_PRIVATE
class App : AApp() {
//    private fun getLocationMAN(): LocationManager {
//        return getLocationService()!!
//    }

    var LASTact: AAct? = null
    private var mParaMS: SharedPref? = null

//    todo: r_dos, use isShowForUser :: e.b.a
    var LocationACCURACY = FloatCool("LocationACCURACY", this)
    var LocationLAT: DoubleCool = Dnull
    var LocationLONG: DoubleCool = Dnull
    var LocationPOINT = Point(LocationLAT, LocationLONG)

    var LocationTIME: LongCool = Lnull

    companion object {
        fun getAppliCation(): App = mAppliCation!!
        fun getAppParaMS(): SharedPref = getAppliCation().mParaMS!!
        fun getMethodMan(): String? {
           return getAppliCation().mMethodName
        }
    }



    override fun onCreate() {
        super.onCreate()
        mAppliCation = this
        mParaMS = SharedPref.create()
        initSentry()
        initRealm()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(getModule())
        }

        // Add a breadcrumb that will be sent with the next event(s)

//
//        try {
//            throw Exception("This is a devel.")
//        } catch (e: Exception) {
//            Sentry.captureException(e)
//        }

//        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(
//            mLlcMap, "alpha", 0f
//        )
//        objectAnimator.setDuration(4000);
//
//        objectAnimator.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator?) {
//                super.onAnimationEnd(animation)
//                mLlcMap.isVisible=false
//            }
//        })
//        val animatorSet = AnimatorSet()
//        animatorSet.playTogether(objectAnimator)
//        animatorSet.start()
//
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


    }

    override fun onTerminate() {
        super.onTerminate()
        LOGWork("onTerminate")
    }


//
//    override fun onLocationUpdated(p0: Location) {
//
//    }
//
//    override fun onLocationStatusUpdated(p0: LocationStatus) {
//      
//    }

    private fun initRealm() {
        Realm.init(this@App)
        val config = RealmConfiguration.Builder()
        config.allowWritesOnUiThread(true)
        config.name("FACT.realm")
        config.deleteRealmIfMigrationNeeded()
        Realm.setDefaultConfiguration(config.build())
    }


    private fun getModule(): List<Module> {
        return listOf(viewModelModule)
    }

    fun runSyncWorkER() {
        before("runSyncWorkER")
        if(getAppParaMS().isModeWorkER) {
            return
        }
        getAppParaMS().isModeWorkER = true
        val uploadDataWorkManager = PeriodicWorkRequestBuilder<SYNCworkER>(99, TimeUnit.MINUTES).build()
//        uploadDataWorkManager.id = UUID
        LOGWork("uploadDataWorkManager.id = ${uploadDataWorkManager.id}")
        val operation =  WorkManager.getInstance(getAppliCation())
            .enqueueUniquePeriodicWork("SYNCworkER", ExistingPeriodicWorkPolicy.REPLACE, uploadDataWorkManager)
        after()
    }

    fun stopWorkERS() {
        WorkManager.getInstance(this).cancelAllWork()
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


    private var mLocationManagerSystem: LocationManager? = null
    private fun getLocationService(): LocationManager {
        //todo: ПЕРЕключатель.ключ.вкл.ON_выкл-OFF
        if (mLocationManagerSystem == null) {
            mLocationManagerSystem = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        return mLocationManagerSystem!!

    }


    inner class MyLocationListener() : LocationListener {
        override fun onLocationChanged(location: Location) {

        }

    }

    fun runLocationService() {
        before("runLocationService")
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val providerName = getProviderName()
        LOGWork(providerName!!)
        getLocationService().requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            3000,
            50F
        // override fun onLocationChanged(location: Location) {
        ) { location ->
            before("onLocationChanged")
            LocationACCURACY.setDATAing(location.accuracy)
            LocationLAT = location.latitude
            LocationLONG = location.longitude
            LocationTIME = location.time
            LocationPOINT = Point(LocationLAT, LocationLONG)
            LOGWork("LocationLAT=${LocationLAT}")
            LOGWork("LocationLONG=${LocationLONG}")
            LOGWork("LocationTIME=${LocationTIME}")
            LASTact?.newsFROMlocationSERVICE()
            after()
        } // здесь можно указать другие более подходящие вам параметры
    }

    fun getProviderName(): String? {
        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.powerRequirement = Criteria.POWER_LOW
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.isSpeedRequired = true
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false
        criteria.isCostAllowed = false
        return locationManager.getBestProvider(criteria, true)
    }

    class SharedPref {

        private lateinit var mEnv: SharedPreferences
        companion object {
            private var mSharedPref: SharedPref? = null
            fun create(): SharedPref {
                if (mSharedPref == null) {
                    mSharedPref = SharedPref()
                    mSharedPref!!.init()

                }

                return mSharedPref!!
            }
        }
        fun init() {
            mEnv = getAppliCation().getSharedPreferences(NAME, MODE)
            isModeWorkER = false
            isModeDEVEL = mIsDevelMode
            isModeDEVEL = mIsDevelMode
            isModeDEVEL = mIsDevelMode
        }

        private val mIsDevelMode: Boolean
            get() {
                var result = false
                if (BuildConfig.BUILD_TYPE == "debug") {
                    result = BuildConfig.VERSION_NAME == "0.0.0.0-STAGE"
                }
                if (BuildConfig.BUILD_TYPE == "debugRC") {
                    result = BuildConfig.VERSION_NAME == "0.0.0.0-STAGE"
                }
                return result
            }

        private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
            val editor = edit()
            operation(editor)
            editor.apply()
        }

        var isCameraSoundEnabled: Boolean
            get() = mEnv.getBoolean("isCameraSoundEnabled", true)
            set(value) = mEnv.edit {
                it.putBoolean("isCameraSoundEnabled", value)
            }

        var lastSynchroTimeQueueTwo: Long
            get() = mEnv.getLong("lastSynchroTimeQueueTwo", 0)
            set(value) = mEnv.edit {
                it.putLong("lastSynchroTimeQueueTwo", value)
            }

        var token: String?
            get() = mEnv.getString("accessToken", "")
            set(value) = mEnv.edit {
                it.putString("accessToken", value)
            }

        var currentCoordinateAccuracy: String
            get() = mEnv.getString("currentCoordinateAccuracy", Snull)!!
            set(value) = mEnv.edit {
                it.putString("currentCoordinateAccuracy", value)
            }
        var currentCoordinate: String
            get() = mEnv.getString("currentCoordinate", " ")!!
            set(value) = mEnv.edit {
                it.putString("currentCoordinate", value)
            }

//    var BoTlogin: String
//        get() = preferences.getString("userLogin", "")!!
//        set(value) = preferences.edit {
//            it.putString("userLogin", value)
//        }

        var isTorchEnabled: Boolean
            get() = mEnv.getBoolean("isTorchEnabled", true)
            set(value) = mEnv.edit {
                it.putBoolean("isTorchEnabled", value)
            }

        var organisationId: Int
            get() = mEnv.getInt("organisationId", 0)
            set(value) = mEnv.edit {
                it.putInt("organisationId", value)
            }

        var lastSynchroTime: Long
            get() = mEnv.getLong("lastSynchronizeTime", 0)
            set(value) = mEnv.edit {
                it.putLong("lastSynchronizeTime", value)
            }

        var serviceStartedAt: Long
            get() = mEnv.getLong("serviceStartedAt", 0L)
            set(value) = mEnv.edit {
                it.putLong("serviceStartedAt", value)
            }

        var vehicleId: Int
            get() = mEnv.getInt("vehicleId", 0)
            set(value) = mEnv.edit {
                it.putInt("vehicleId", value)
            }

        var wayBillId: Int
            get() = mEnv.getInt("wayListId", 0)
            set(value) = mEnv.edit {
                it.putInt("wayListId", value)
            }

        var isModeSYNChrONize: Boolean
            get() = mEnv.getBoolean("isSYNCmode", true)
            set(value) = mEnv.edit {
                it.putBoolean("isSYNCmode", value)
            }

        var isModeWorkER: Boolean
            get() = mEnv.getBoolean("isModeWorkER", true)
            set(value) = mEnv.edit {
                it.putBoolean("isModeWorkER", value)
            }

        var isModeDEVEL: Boolean
            get() = mEnv.getBoolean("isModeDEVEL", true)
            set(value) = mEnv.edit {
                it.putBoolean("isModeDEVEL", value)
            }

        fun dropDatabase() {
            token = null
            vehicleId = 0
            organisationId = 0
            wayBillId = 0
            //TODO:r_Null!
            isModeSYNChrONize = false
        }

        fun getCurrentLocation(): Point {
            var result =  Point(Dnull, Dnull)
            try {
                val lat = currentCoordinate.substringBefore("#").toDouble()
                val long = currentCoordinate.substringAfter("#").toDouble()
                result = Point(lat, long)
            } catch (ex: Exception) {
                // TODO: 24.12.2021  /\
                try {
                    result = LocationManagerUtils.getLastKnownLocation()!!.position
                } catch (ex: Exception) {

                }
            }
            return result
        }

        fun getLastKnownLocationTime(): Long {
            var result = Lnull
            try {
                val location = LocationManagerUtils.getLastKnownLocation()
                if (location != null) {
                    result = System.currentTimeMillis() - location.absoluteTimestamp
                }
            } catch (ex: Exception) {

            }

            return result
        }

        fun SETDevelMode() {
//        accessToken = ""
            vehicleId = 0
            organisationId = 0
            wayBillId = 0
            //TODO: rNull!!
            isModeSYNChrONize = false
        }



    }

}


fun AAct.offSyNChrON(){
    App.getAppParaMS().isModeSYNChrONize = false
}

fun AAct.onSyNChrON(){
    App.getAppParaMS().isModeSYNChrONize = true
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

