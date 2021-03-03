package ru.smartro.worknote

import android.app.Application
import io.realm.Realm
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
        Realm.init(this@App)
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(getModule())
        }
    }

    private fun getModule(): List<Module> {
        return listOf(viewModelModule)
    }
}