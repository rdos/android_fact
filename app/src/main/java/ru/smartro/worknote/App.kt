package ru.smartro.worknote

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


public const val TIME_OUT = 240000L
private var mAppliCation: App? = null
//todo: !r_dos true-falseTYPE:
//fun isSaveGPS(): Boolean { isDevelMODE и стандарт
//

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
        mAppliCation = this
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

            GPS = AndRoid.PoinT(LocationLAT, LocationLONG, LocationTIME, LocationACCURACY)
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
        val providerName = AndRoid.getProviderName()
        LOGWork(providerName!!)
        AndRoid.getService().requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            30_000,
            10000F,
            // override fun onLocationChanged(location: Location) {
        MyLocationListener()
        ) // здесь можно указать другие более подходящие вам параметры
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

