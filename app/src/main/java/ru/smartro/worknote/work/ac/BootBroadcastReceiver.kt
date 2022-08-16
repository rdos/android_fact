package ru.smartro.worknote.work.ac

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.os.UserManagerCompat
import ru.smartro.worknote.App

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bootCompleted: Boolean
        val action = intent.action
        Log.i(TAG, "Received action: $action, user unlocked: " + UserManagerCompat.isUserUnlocked(context))
        bootCompleted = Intent.ACTION_BOOT_COMPLETED == action
        if (!bootCompleted) {
            return
        }

        val serviceIntent = Intent(context, BootIntentService::class.java)
        context.startService(serviceIntent)
    }

    companion object {
        private const val TAG = "BootBroadcastReceiver"
    }
}
