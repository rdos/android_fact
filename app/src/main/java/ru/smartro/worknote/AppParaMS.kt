package ru.smartro.worknote

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import ru.smartro.worknote.*
import ru.smartro.worknote.App.ScreenMode
import ru.smartro.worknote.andPOintD.PoinT
import ru.smartro.worknote.awORKOLDs.util.MyUtil

private const val NAME = "AppParaMS"
private const val MODE = Context.MODE_PRIVATE
class AppParaMS {


    public var isRestartApp: Boolean = false
    //todo: mEnv? что за ЗАЯЦ/?)
    private val sharedPref__env: SharedPreferences by lazy {
        App.getAppliCation().getSharedPreferences(NAME, MODE)
    }

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


        AppRestarted()
        isRestartApp = false
        isModeDEVEL = app.setDevelMODE()

    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var isCameraSoundEnabled: Boolean
        get() = sharedPref__env.getBoolean("isCameraSoundEnabled", true)
        set(value) = sharedPref__env.edit {
            it.putBoolean("isCameraSoundEnabled", value)
        }

    var lastSynchroTimeQueueTwo: Long
        get() = sharedPref__env.getLong("lastSynchroTimeQueueTwo", 0)
        set(value) = sharedPref__env.edit {
            it.putLong("lastSynchroTimeQueueTwo", value)
        }

    var token: String?
        get() = sharedPref__env.getString("accessToken", "")
        set(value) = sharedPref__env.edit {
            it.putString("accessToken", value)
        }

    var lastScreenMode: Boolean
        get() = sharedPref__env.getBoolean("lastScreenMode", ScreenMode.EXTENDED)
        set(value) = sharedPref__env.edit {
            it.putBoolean("lastScreenMode", value)
        }

    var userName: String
        get() = sharedPref__env.getString("userName", Snull)!!
        set(value) = sharedPref__env.edit {
            it.putString("userName", value)
        }

    var deviceId: String
        get() {
            var devId = sharedPref__env.getString("deviceId", Snull)!!
            if (devId == Snull) {
                devId = App.getAppliCation().getDeviceId()
                sharedPref__env.edit{
                    it.putString("deviceId", devId)
                }
            }
            return devId
        }
        set(value) {
            Log.e("deviceId", "deviceIdset(value)")
            Log.e("deviceId", "deviceIdset(value)")
            Log.e("deviceId", "deviceIdset(value)")
            Log.w("deviceId", "deviceIdset(value)")
            Log.e("deviceId", "deviceIdset(value)")
            Log.e("deviceId", "deviceIdset(value)")
            Log.e("deviceId", "deviceIdset(value)")
        }

//    base = device ОКРУЖЕНИЕ...
    fun isOldGPSbaseDate(time: Long): Boolean = isTimeForSaveData(time)

    private fun isTimeForSaveData(time: Long?=null): Boolean {
        val currentTimeMS = System.currentTimeMillis()
        val diff = currentTimeMS - (time?:gpsTIME)
        val res =  diff >= 30_000
        Log.w("AppParaMS", "res=${res} time=${time} gpsTIME=${gpsTIME} currentTimeMS=${currentTimeMS} diff=${diff}")
        return res
    }
    fun iSoldGPSdataSaved(): Boolean {
           return isTimeForSaveData()
    }
//    fun getAlwaysGPS(): AndRoid.PoinT {
//        return geTLastKnowGPS()
//    }
//    fun geTLastKnowGPS(): AndRoid.PoinT {
//        return
//    }
    fun getSaveGPS(): PoinT {
        val res = PoinT(gpsLAT, gpsLONG, gpsTIME, gpsACCURACY)
//        if (iSoldGPSdataSaved()) {
//            return res
//        }
//        Log.e("getSaveGPS", "if (isLastGPSSaved()) == false")
//        Log.w("getSaveGPS", "if (isLastGPSSaved()) == false")
//        Log.i("getSaveGPS", "if (isLastGPSSaved()) == false")
//        Log.w("getSaveGPS", "if (isLastGPSSaved()) == false")
//        Log.e("getSaveGPS", "if (isLastGPSSaved()) == false")
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

    private var gpsLAT: Float
        get() = sharedPref__env.getFloat("gpsLAT", Fnull)
        private set(value) = sharedPref__env.edit {
            it.putFloat("gpsLAT", value)
        }

    private var gpsLONG: Float
        get() = sharedPref__env.getFloat("gpsLONG", Fnull)
        private set(value) = sharedPref__env.edit {
            it.putFloat("gpsLONG", value)
        }

    private var gpsTIME: Long
        get() = sharedPref__env.getLong("gpsTIME", LTIMEnull)
        set(value) = sharedPref__env.edit {
            it.putLong("gpsTIME", value)
        }

    private var gpsACCURACY: Float
        get() = sharedPref__env.getFloat("gpsACCURACY", Fnull)
        private set(value) = sharedPref__env.edit {
            it.putFloat("gpsACCURACY", value)
        }
//    var BoTlogin: String
//        get() = preferences.getString("userLogin", "")!!
//        set(value) = preferences.edit {
//            it.putString("userLogin", value)
//        }

    var isTorchEnabled: Boolean
        get() = sharedPref__env.getBoolean("isTorchEnabled", true)
        set(value) = sharedPref__env.edit {
            it.putBoolean("isTorchEnabled", value)
        }

    fun getOwnerId(): Int {
        return ownerId?: Inull
    }

    var ownerId: Int?
        get() {
            val tmp = sharedPref__env.getInt("ownerId", Inull)
            if (tmp == Inull) {
                return null
            }
            return tmp
        }
        set(value) = sharedPref__env.edit {
            if (value == null) {
                return@edit
            }
            it.putInt("ownerId", value)
        }

    var ownerName: String?
        get() = sharedPref__env.getString("ownerName", "")
        set(value) = sharedPref__env.edit {
            it.putString("ownerName", value)
        }


    var lastSynchroTimeInSec: Long
        get() = sharedPref__env.getLong("lastSynchronizeTime", 0)
        set(value) = sharedPref__env.edit {
            it.putLong("lastSynchronizeTime", value)
        }

    fun getVehicleId(): Int {
        return vehicleId?: Inull
    }

    var vehicleId: Int?
        get() {
            val tmp = sharedPref__env.getInt("vehicleId", Inull)
            if (tmp == Inull) {
                return null
            }
            return tmp
        }
        set(value) = sharedPref__env.edit {
            if (value == null) {
                return@edit
            }
            it.putInt("vehicleId", value)
        }

    var vehicleName: String?
        get() = sharedPref__env.getString("vehicleName", "")
        set(value) = sharedPref__env.edit {
            it.putString("vehicleName", value)
        }


    var wayBillId: Int
        get() = sharedPref__env.getInt("wayBillId", 0)
        set(value) = sharedPref__env.edit {
            it.putInt("wayBillId", value)
        }
    var wayBillNumber: String?
        get() = sharedPref__env.getString("wayBillNumber", "")
        set(value) = sharedPref__env.edit {
            it.putString("wayBillNumber", value)
        }

    var isShowTooltipInNextTime: Boolean
        get() = sharedPref__env.getBoolean("isShownTooltip", true)
        set(value) = sharedPref__env.edit {
            it.putBoolean("isShownTooltip", value)
        }

    var cntTooltipShow: Int
        get() = sharedPref__env.getInt("walkthroughWasShownCnt", 0)
        set(value) = sharedPref__env.edit {
            it.putInt("walkthroughWasShownCnt", value)
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

    fun setLogoutParams() {
        token = null
        isModeSYNChrONize = false
    }

    fun setAppRestartParams() {
        ownerId = Inull
        vehicleId = Inull
        wayBillId = Inull
        //TODO:r_Null!
        isModeSYNChrONize = false
        lastSynchroTimeInSec = MyUtil.timeStampInSec()
    }


    fun AppRestarted() {
        isModeSYNChrONize_FoundError = false
        isModeSYNChrONize = false
        isModeWorkER = false
        isModeLOCATION = false
        isRestartApp = true
    }
}