package ru.smartro.worknote.workold.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import ru.smartro.worknote.work.RealmRepository
import ru.smartro.worknote.workold.service.network.NetworkRepository
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {
    protected var TAG : String = "--Aaa${this::class.simpleName}"
    val networkDat = NetworkRepository(application.applicationContext)
    val baseDat = RealmRepository(Realm.getDefaultInstance())

    /**
    fun getRealm(): RealmRepository {
        return db
    }
    */
    private val job: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}