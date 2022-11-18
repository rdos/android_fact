package ru.smartro.worknote.presentation.ac

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.os.UserManagerCompat
import io.realm.Realm
import ru.smartro.worknote.presentation.work.ConfigName
import ru.smartro.worknote.presentation.work.RealmRepository

class BootBroadcastReceiver : BroadcastReceiver() {

    private val db: RealmRepository by lazy {
        RealmRepository(Realm.getDefaultInstance())
    }

    override fun onReceive(context: Context, intent: Intent) {
        val bootCompleted: Boolean
        val action = intent.action
        Log.i(TAG, "Received action: $action, user unlocked: " + UserManagerCompat.isUserUnlocked(context))
        bootCompleted = Intent.ACTION_BOOT_COMPLETED == action
        if (!bootCompleted) {
            return
        }
        Log.w(TAG, "onReceive")
        db.setConfigCntPlusOne(ConfigName.BOOT_CNT)
        db.close()
//        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "BootBroadcastReceiver"
    }
}
