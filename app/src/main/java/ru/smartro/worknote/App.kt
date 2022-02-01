package ru.smartro.worknote

import android.app.Application
import android.util.Log
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
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.util.MyUtil
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
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
    }

    private fun initRealm() {
        Realm.init(this@App)
        if (!AppPreferences.isHasTask) {
            Realm.deleteRealm(Realm.getDefaultConfiguration()!!)
        }
        val config = RealmConfiguration.Builder()
        config.allowWritesOnUiThread(true)
        config.name("FACT.realm")
        config.deleteRealmIfMigrationNeeded()
        Realm.setDefaultConfiguration(config.build())
    }


    private fun getModule(): List<Module> {
        return listOf(viewModelModule)
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

const val Snull = "rNull"
const val Inull = -111
const val Lnull = -111222333 as Long
const val Fnull = -11.1
const val Dnull = -111.0
const val ErrorsE = "ErrorsE"

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
