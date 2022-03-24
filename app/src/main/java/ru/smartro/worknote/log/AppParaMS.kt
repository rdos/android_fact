package ru.smartro.worknote.log

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import ru.smartro.worknote.*
import ru.smartro.worknote.andPOintD.PoinT

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
    var deviceId: String
        get() {
            var devId = mEnv.getString("deviceId", Snull)!!
            if (devId == Snull) {
                devId = App.getAppliCation().getDeviceId()
                mEnv.edit{
                    it.putString("deviceId", devId)
                }
            }
            return devId
        }
        set(value) {
            Log.e("deviceId", "deviceIdset(value)")
            Log.e("deviceId", "deviceIdset(value)")
            Log.e("deviceId", "deviceIdset(value)")
            Log.e("deviceId", "deviceIdset(value)")
            Log.e("deviceId", "deviceIdset(value)")
            Log.e("deviceId", "deviceIdset(value)")
            Log.e("deviceId", "deviceIdset(value)")
        }
    fun isLastGPS(time: Long): Boolean = isLastGPS_priv(time)
    private fun isLastGPS_priv(time: Long?=null): Boolean {
        val diff = System.currentTimeMillis() - (time?:gpsTIME)
        return diff <= 3_000
    }
    fun isLastGPSSaved(): Boolean {
       return isLastGPS_priv()
    }
//    fun getAlwaysGPS(): AndRoid.PoinT {
//        return geTLastKnowGPS()
//    }
//    fun geTLastKnowGPS(): AndRoid.PoinT {
//        return
//    }
    fun getSaveGPS(): PoinT {
        val res = PoinT(gpsLAT, gpsLONG, gpsTIME, gpsACCURACY)
        if (isLastGPSSaved()) {
            return res
        }
        Log.e("getSaveGPS", "if (isLastGPSSaved()) == false")
        Log.w("getSaveGPS", "if (isLastGPSSaved()) == false")
        Log.i("getSaveGPS", "if (isLastGPSSaved()) == false")
        Log.w("getSaveGPS", "if (isLastGPSSaved()) == false")
        Log.e("getSaveGPS", "if (isLastGPSSaved()) == false")
        return res
    }

    fun saveLastGPS(lat: DoubleCool, long: DoubleCool, time: LongCool, accuracy: Float) {
        //база
        gpsLAT = lat.toFloat()
        gpsLONG = long.toFloat()
        //базовая основа
        gpsTIME = time
        //доп.олни!тельное)
        gpsACCURACY = accuracy
    }


    fun saveLastGPS(point: PoinT) {
        //база
        saveLastGPS(point.latitude, point.longitude, point.getTime(), point.getAccuracy())
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

    fun getOwnerId(): Int {
        return ownerId?: Inull
    }

    var ownerId: Int?
        get() {
            val tmp = mEnv.getInt("ownerId", Inull)
            if (tmp == Inull) {
                return null
            }
            return tmp
        }
        set(value) = mEnv.edit {
            if (value == null) {
                return@edit
            }
            it.putInt("ownerId", value)
        }

    var ownerName: String?
        get() = mEnv.getString("ownerName", "")
        set(value) = mEnv.edit {
            it.putString("ownerName", value)
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

    fun getVehicleId(): Int {
        return vehicleId?: Inull
    }

    var vehicleId: Int?
        get() {
            val tmp = mEnv.getInt("vehicleId", Inull)
            if (tmp == Inull) {
                return null
            }
            return tmp
        }
        set(value) = mEnv.edit {
            if (value == null) {
                return@edit
            }
            it.putInt("vehicleId", value)
        }

    var vehicleName: String?
        get() = mEnv.getString("vehicleName", "")
        set(value) = mEnv.edit {
            it.putString("vehicleName", value)
        }


    var wayBillId: Int
        get() = mEnv.getInt("wayBillId", 0)
        set(value) = mEnv.edit {
            it.putInt("wayBillId", value)
        }
    var wayBillNumber: String?
        get() = mEnv.getString("wayBillNumber", "")
        set(value) = mEnv.edit {
            it.putString("wayBillNumber", value)
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


    fun AppRestarted() {
        isModeSYNChrONize_FoundError = false
        isModeSYNChrONize = false
        isModeWorkER = false
        isModeLOCATION = false
        isRestartApp = true
    }
}