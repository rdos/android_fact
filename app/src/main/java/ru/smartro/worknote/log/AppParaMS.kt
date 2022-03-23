package ru.smartro.worknote.log

import android.content.Context
import android.content.SharedPreferences
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.FloatCool
import ru.smartro.worknote.andPOIntD.AndRoid

private const val NAME = "AppParaMS"
private const val MODE = Context.MODE_PRIVATE
class AppParaMS {


    public var isRestartApp: Boolean = false
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

    private fun init() {
        val app = App.getAppliCation()
        mEnv = app.getSharedPreferences(NAME, MODE)

        AppRestarted()
        isRestartApp = false
        isModeDEVEL = app.isDevelMODE()

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



    fun isLastGPS(time: Long): Boolean = isLastGPS_priv(time)

    private fun isLastGPS_priv(time: Long?=null): Boolean {
        val diff = System.currentTimeMillis() - (time?:gpsTIME)
        return diff <= 3_000
    }

    fun isLastGPSSaved(): Boolean {
       return isLastGPS_priv()
    }


    //Any
    fun getAlwaysGPS(): AndRoid.PoinT {
        return geTLastKnowGPS()
    }

    fun geTLastKnowGPS(): AndRoid.PoinT {
        return AndRoid.PoinT(gpsLAT, gpsLONG, gpsTIME, gpsACCURACY)
    }

    fun getLastGPS(): AndRoid.PoinT? {
        var res:AndRoid.PoinT? = null
        if (isLastGPSSaved()) {
            res= geTLastKnowGPS()
        }
        return res
    }



    fun addParamLast12nowGPS(lat: DoubleCool, long: DoubleCool, time: LongCool, accuracy: FloatCool) {
        //база
        gpsLAT = lat.toFloat()
        gpsLONG = long.toFloat()
        //базовая основа
        gpsTIME = time
        //доп.олни!тельное)
        gpsACCURACY = accuracy.VAL
    }


    var gpsLAT: Float
        get() = mEnv.getFloat("gpsLAT", Fnull)
        private set(value) = mEnv.edit {
            it.putFloat("gpsLAT", value)
        }

    var gpsLONG: Float
        get() = mEnv.getFloat("gpsLONG", Fnull)
        private set(value) = mEnv.edit {
            it.putFloat("gpsLONG", value)
        }

    var gpsTIME: Long
        get() = mEnv.getLong("gpsTIME", 0)
        set(value) = mEnv.edit {
            it.putLong("gpsTIME", value)
        }

    var gpsACCURACY: Float
        get() = mEnv.getFloat("gpsACCURACY", Fnull)
        private set(value) = mEnv.edit {
            it.putFloat("gpsACCURACY", value)
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


    var isModeSYNChrONize_FoundError: Boolean = false
        get() = field
        set(value) {
            if (value == field) {
                return
            }
            if (value) {
                isModeSYNChrONize = false
                isModeWorkER = false
            } else {
                if (field) {
                    isModeWorkER = true
                    isModeSYNChrONize = true
                }
            }
            field = value
        }

    var isModeSYNChrONize: Boolean=false
    var isModeWorkER: Boolean=false
    var isModeLOCATION: Boolean=false

    var isModeDEVEL: Boolean = false

    fun logout() {
        token = null
        vehicleId = Inull
        wayBillId = Inull
        //TODO:r_Null!
        isModeSYNChrONize = false
    }

    fun logoutDEVEL() {
//        accessToken = ""
        vehicleId = Inull
        wayBillId = Inull
        isModeSYNChrONize = false
        //TODO: rNull!!
    }

    fun AppRestarted() {
        isModeSYNChrONize_FoundError = false
        isModeSYNChrONize = false
        isModeWorkER = false
        isModeLOCATION = false
        isRestartApp = true
    }
}