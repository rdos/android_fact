package ru.smartro.worknote.service

import android.content.Context
import android.content.SharedPreferences


object AppPreferences {

    private const val NAME = ""
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var accessToken: String?
        get() = preferences.getString("accessToken", "")
        set(value) = preferences.edit {
            it.putString("accessToken", value)
        }

    var currentCoordinate: String?
        get() = preferences.getString("currentCoordinate", " ")
        set(value) = preferences.edit {
            it.putString("currentCoordinate", value)
        }

    var userLogin: String
        get() = preferences.getString("userLogin", "")!!
        set(value) = preferences.edit {
            it.putString("userLogin", value)
        }

    var isLogined: Boolean
        get() = preferences.getBoolean("isLogined", false)
        set(value) = preferences.edit {
            it.putBoolean("isLogined", value)
        }

    var organisationId: Int
        get() = preferences.getInt("organisationId", 0)
        set(value) = preferences.edit {
            it.putInt("organisationId", value)
        }

    var lastSynchroTime: Long
        get() = preferences.getLong("lastSynchronizeTime", 0)
        set(value) = preferences.edit {
            it.putLong("lastSynchronizeTime", value)
        }

    var serviceStartedAt: Long
        get() = preferences.getLong("serviceStartedAt", 0L)
        set(value) = preferences.edit {
            it.putLong("serviceStartedAt", value)
        }

    var vehicleId: Int
        get() = preferences.getInt("vehicleId", 0)
        set(value) = preferences.edit {
            it.putInt("vehicleId", value)
        }

    var wayBillId: Int
        get() = preferences.getInt("wayListId", 0)
        set(value) = preferences.edit {
            it.putInt("wayListId", value)
        }

    var wayTaskId: Int
        get() = preferences.getInt("wayTaskId", 0)
        set(value) = preferences.edit {
            it.putInt("wayTaskId", value)
        }

    var thisUserHasTask: Boolean
        get() = preferences.getBoolean("thisUserHasTask", false)
        set(value) = preferences.edit {
            it.putBoolean("thisUserHasTask", value)
        }

    var workerStatus: Boolean
        get() = preferences.getBoolean("workerStatus", true)
        set(value) = preferences.edit {
            it.putBoolean("workerStatus", value)
        }

    fun clear() {
        isLogined = false
        accessToken = ""
        vehicleId = 0
        organisationId = 0
        wayBillId = 0
        wayTaskId = 0
        thisUserHasTask = false
        workerStatus = false
    }

}