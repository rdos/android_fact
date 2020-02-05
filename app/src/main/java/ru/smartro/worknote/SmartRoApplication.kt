package ru.smartro.worknote

import android.app.Application
import timber.log.Timber

class SmartRoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}