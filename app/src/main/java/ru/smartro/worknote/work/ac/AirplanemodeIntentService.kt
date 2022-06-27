package ru.smartro.worknote.work.ac

import android.app.IntentService
import android.content.Intent
import android.util.Log
import io.realm.Realm
import ru.smartro.worknote.work.RealmRepository
import ru.smartro.worknote.work.ConfigName

class AirplanemodeIntentService() : IntentService("AirplanemodeIntentService") {
    private val db: RealmRepository by lazy {
        RealmRepository(Realm.getDefaultInstance())
    }
    override fun onDestroy() {
        Log.w("AirplanemodeIntentService", "onDestroy")
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        Log.w("AirplanemodeIntentService", "onHandleIntent")
        val configEntity = db.loadConfig(ConfigName.AIRPLANEMODE_CNT)
        configEntity.cntPlusOne()
        db.saveConfig(configEntity)
        db.close()
//        TODO("Not yet implemented")
    }
}