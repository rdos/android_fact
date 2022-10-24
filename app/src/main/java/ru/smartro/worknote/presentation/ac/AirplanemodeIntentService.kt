package ru.smartro.worknote.presentation.ac

import android.app.IntentService
import android.content.Intent
import android.util.Log
import io.realm.Realm
import ru.smartro.worknote.LOG
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
        val isAirplaneModeEnabled = intent?.getBooleanExtra("isAirplaneModeEnabled", false) ?: return
        LOG.warn("AirplaneService: isAirplaneModeEnabled = ${isAirplaneModeEnabled}")
        if (isAirplaneModeEnabled) {
            db.setConfigCntPlusOne(ConfigName.AIRPLANE_MODE_ON_CNT)
        } else {
            db.setConfigCntPlusOne(ConfigName.AIRPLANE_MODE_OFF_CNT)
        }
        db.close()
//        TODO("Not yet implemented")
    }
}