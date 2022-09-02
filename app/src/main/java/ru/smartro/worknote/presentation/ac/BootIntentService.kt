package ru.smartro.worknote.presentation.ac

import android.app.IntentService
import android.content.Intent
import android.util.Log
import io.realm.Realm
import ru.smartro.worknote.work.RealmRepository
import ru.smartro.worknote.work.ConfigName

class BootIntentService() : IntentService("BootIntentService") {
    private val db: RealmRepository by lazy {
        RealmRepository(Realm.getDefaultInstance())
    }
    override fun onDestroy() {
        Log.w("BootIntentService", "onDestroy")
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        Log.w("BootIntentService", "onHandleIntent")
        val configEntity = db.loadConfig(ConfigName.BOOT_CNT)
        configEntity.cntPlusOne()
        configEntity.setShowForUser()
        db.saveConfig(configEntity)
        db.close()
//        TODO("Not yet implemented")
    }
}