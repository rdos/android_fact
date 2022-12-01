package ru.smartro.worknote.presentation.andPOintD

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.os.UserManagerCompat
import io.realm.Realm
import ru.smartro.worknote.log.todo.ConfigName
import ru.smartro.worknote.work.work.RealmRepository

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
