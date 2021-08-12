package ru.smartro.worknote

import android.app.Application
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
    }

    private fun initRealm() {
        Realm.init(this@App)
        if (!AppPreferences.isHasTask) {
            Realm.deleteRealm(Realm.getDefaultConfiguration()!!)
        }
        val config = RealmConfiguration.Builder()
        config.allowWritesOnUiThread(true)
        config.name("FactRealmBase")
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