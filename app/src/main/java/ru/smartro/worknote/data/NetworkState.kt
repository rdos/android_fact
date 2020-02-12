package ru.smartro.worknote.data

import android.app.Activity
import android.content.Context
import ru.smartro.worknote.utils.TimeConsts

class NetworkState(activity: Activity) {
    private var sharedPreferences = activity
        .getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)

    var isErrorCoolDown: Boolean
        get() {
            return sharedPreferences.getLong(
                KEY_NETWORK_ERROR_COODDOWN_EXPIRED,
                0L
            ) < System.currentTimeMillis()
        }
        set(value) {
            when (value) {
                true -> sharedPreferences.edit().putLong(
                    KEY_NETWORK_ERROR_COODDOWN_EXPIRED,
                    System.currentTimeMillis() + NETWORK_ERROR_COOLDOWN_MILLIS
                )
                    .apply()
                false -> sharedPreferences.edit().putLong(
                    KEY_NETWORK_ERROR_COODDOWN_EXPIRED,
                    0L
                )
                    .apply()
            }

        }

    fun requestIsNotNeed(key: String): Boolean {
        return isErrorCoolDown || DTOIsCoolDown(key)
    }

    fun getLastRefreshOf(key: String): Long {
        return sharedPreferences.getLong(key, 0L)
    }

    fun setRefreshedNowOf(key: String) {
        sharedPreferences.edit().putLong(key, System.currentTimeMillis())
            .apply()
    }

    fun DTOIsCoolDown(key: String): Boolean {
        return (
                getLastRefreshOf(key) + NETWORK_REFRESH_COOLDOWN_MILLIS
                ) > System.currentTimeMillis()
    }

    companion object {

        val NETWORK_ERROR_COOLDOWN_MILLIS = TimeConsts.FIVE_MINUTES
        val NETWORK_REFRESH_COOLDOWN_MILLIS = TimeConsts.ONE_MINUTE
        val PREFERENCES_FILE = "wn_mobile_network_state_preferences"
        val KEY_NETWORK_ERROR_COODDOWN_EXPIRED = "network_err_long_expired_utc_cd"
    }
}