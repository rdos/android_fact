package ru.smartro.worknote

import android.app.Application
import io.realm.Realm
import io.sentry.Sentry
import io.sentry.SentryLevel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import ru.smartro.worknote.di.viewModelModule
import ru.smartro.worknote.service.AppPreferences

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(getModule())
        }
        Sentry.init { options ->
            options.dsn = "https://f52d405267944551b65123facccf3d2c@sentry.soyuz317.ru/17"
        }
        Sentry.configureScope { scope ->
            scope.level = SentryLevel.WARNING
        }

        Realm.init(this@App)

    }

    private fun getModule(): List<Module> {
        return listOf(viewModelModule)
    }
}