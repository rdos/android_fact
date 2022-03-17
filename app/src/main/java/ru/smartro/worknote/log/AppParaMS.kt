package ru.smartro.worknote.log

import android.content.Context
import android.content.SharedPreferences
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.LocationManagerUtils
import ru.smartro.worknote.*
import java.lang.Exception

private const val NAME = ""
private const val MODE = Context.MODE_PRIVATE
class AppParaMS {
    private lateinit var mEnv: SharedPreferences

    companion object {
        private var MSINStance: AppParaMS? = null
        fun create(): AppParaMS {
            if (MSINStance == null) {
                MSINStance = AppParaMS()
                MSINStance!!.init()

            }

            return MSINStance!!
        }
    }

    fun init() {
        mEnv = App.getAppliCation().getSharedPreferences(NAME, MODE)
        isModeWorkER = false
        isModeDEVEL = mIsDevelMode
        isModeDEVEL = mIsDevelMode
        isModeDEVEL = mIsDevelMode
    }

    private val mIsDevelMode: Boolean
        get() {
            var result = false
            if (BuildConfig.BUILD_TYPE == "debug") {
                result = BuildConfig.VERSION_NAME == "0.0.0.0-STAGE"
            }
            if (BuildConfig.BUILD_TYPE == "debugRC") {
                result = BuildConfig.VERSION_NAME == "0.0.0.0-STAGE"
            }
            return result
        }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var isCameraSoundEnabled: Boolean
        get() = mEnv.getBoolean("isCameraSoundEnabled", true)
        set(value) = mEnv.edit {
            it.putBoolean("isCameraSoundEnabled", value)
        }

    var lastSynchroTimeQueueTwo: Long
        get() = mEnv.getLong("lastSynchroTimeQueueTwo", 0)
        set(value) = mEnv.edit {
            it.putLong("lastSynchroTimeQueueTwo", value)
        }

    var token: String?
        get() = mEnv.getString("accessToken", "")
        set(value) = mEnv.edit {
            it.putString("accessToken", value)
        }

    var currentCoordinateAccuracy: String
        get() = mEnv.getString("currentCoordinateAccuracy", Snull)!!
        set(value) = mEnv.edit {
            it.putString("currentCoordinateAccuracy", value)
        }
    var currentCoordinate: String
        get() = mEnv.getString("currentCoordinate", " ")!!
        set(value) = mEnv.edit {
            it.putString("currentCoordinate", value)
        }

//    var BoTlogin: String
//        get() = preferences.getString("userLogin", "")!!
//        set(value) = preferences.edit {
//            it.putString("userLogin", value)
//        }

    var isTorchEnabled: Boolean
        get() = mEnv.getBoolean("isTorchEnabled", true)
        set(value) = mEnv.edit {
            it.putBoolean("isTorchEnabled", value)
        }

    var organisationId: Int
        get() = mEnv.getInt("organisationId", 0)
        set(value) = mEnv.edit {
            it.putInt("organisationId", value)
        }

    var lastSynchroTime: Long
        get() = mEnv.getLong("lastSynchronizeTime", 0)
        set(value) = mEnv.edit {
            it.putLong("lastSynchronizeTime", value)
        }

    var serviceStartedAt: Long
        get() = mEnv.getLong("serviceStartedAt", 0L)
        set(value) = mEnv.edit {
            it.putLong("serviceStartedAt", value)
        }

    var vehicleId: Int
        get() = mEnv.getInt("vehicleId", 0)
        set(value) = mEnv.edit {
            it.putInt("vehicleId", value)
        }

    var wayBillId: Int
        get() = mEnv.getInt("wayListId", 0)
        set(value) = mEnv.edit {
            it.putInt("wayListId", value)
        }

    var isModeSYNChrONize: Boolean
        get() = mEnv.getBoolean("isSYNCmode", true)
        set(value) = mEnv.edit {
            it.putBoolean("isSYNCmode", value)
        }

    var isModeWorkER: Boolean
        get() = mEnv.getBoolean("isModeWorkER", true)
        set(value) = mEnv.edit {
            it.putBoolean("isModeWorkER", value)
        }

    var isModeDEVEL: Boolean
        get() = mEnv.getBoolean("isModeDEVEL", true)
        set(value) = mEnv.edit {
            it.putBoolean("isModeDEVEL", value)
        }

    fun dropDatabase() {
        token = null
        vehicleId = 0
        organisationId = 0
        wayBillId = 0
        //TODO:r_Null!
        isModeSYNChrONize = false
    }

    fun getCurrentLocation(): Point {
        var result = Point(Dnull, Dnull)
        try {
            val lat = currentCoordinate.substringBefore("#").toDouble()
            val long = currentCoordinate.substringAfter("#").toDouble()
            result = Point(lat, long)
        } catch (ex: Exception) {
            // TODO: 24.12.2021  /\
            try {
                result = LocationManagerUtils.getLastKnownLocation()!!.position
            } catch (ex: Exception) {

            }
        }
        return result
    }

    fun getLastKnownLocationTime(): Long {
        var result = Lnull
        try {
            val location = LocationManagerUtils.getLastKnownLocation()
            if (location != null) {
                result = System.currentTimeMillis() - location.absoluteTimestamp
            }
        } catch (ex: Exception) {

        }

        return result
    }

    fun SETDevelMode() {
//        accessToken = ""
        vehicleId = 0
        organisationId = 0
        wayBillId = 0
        //TODO: rNull!!
        isModeSYNChrONize = false
    }
}