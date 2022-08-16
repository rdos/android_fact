package ru.smartro.worknote.andPOintD

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import ru.smartro.worknote.App
import ru.smartro.worknote.work.RealmRepository
import ru.smartro.worknote.work.NetworkRepository
import kotlin.coroutines.CoroutineContext

abstract class AViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {
    val networkDat = NetworkRepository(application)
    val baseDat = RealmRepository(Realm.getDefaultInstance())
    open val params = App.getAppParaMS()

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