package ru.smartro.worknote.ac

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import ru.smartro.worknote.App
import ru.smartro.worknote.work.work.RealmRepository
import kotlin.coroutines.CoroutineContext

abstract class AViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {
    val database = RealmRepository(Realm.getDefaultInstance())
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