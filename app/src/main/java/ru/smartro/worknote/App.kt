package ru.smartro.worknote

import android.Manifest
import android.content.Context
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
import ru.smartro.worknote.workold.util.MyUtil
import ru.smartro.worknote.log.AApp
import ru.smartro.worknote.abs.FloatCool
import ru.smartro.worknote.log.AAct
import ru.smartro.worknote.log.AppParaMS
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


public const val TIME_OUT = 240000L
private var mAppliCation: App? = null


class App : AApp() {
//    private fun getLocationMAN(): LocationManager {
//        return getLocationService()!!
//    }

    var LASTact: AAct? = null
    private val aPPParamS = AppParaMS.create()

//    todo: r_dos, use isShowForUser :: e.b.a
    var LocationACCURACY = FloatCool("LocationACCURACY", this)
    var LocationLAT: DoubleCool = Dnull
    var LocationLONG: DoubleCool = Dnull
    var LocationPOINT = Point(LocationLAT, LocationLONG)

    var LocationTIME: LongCool = Lnull

    companion object {
        fun getAppliCation(): App = mAppliCation!!
        fun getAppParaMS(): AppParaMS = getAppliCation().aPPParamS
        fun getMethodMan(): String? {
           return getAppliCation().mMethodName
        }
    }



    override fun onCreate() {
        super.onCreate()
        mAppliCation = this
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

