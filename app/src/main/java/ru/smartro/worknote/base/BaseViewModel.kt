package ru.smartro.worknote.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import ru.smartro.worknote.service.db.RealmRepository
import ru.smartro.worknote.service.network.NetworkRepository
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(application: Application) : AndroidViewModel(application),
    CoroutineScope {
    protected val network = NetworkRepository(application.applicationContext)
    protected val db = RealmRepository(application.applicationContext)

    private val job: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}