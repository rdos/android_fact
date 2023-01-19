package ru.smartro.worknote.presentation

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import io.realm.Realm
import ru.smartro.worknote.App
import ru.smartro.worknote.AppParaMS
import ru.smartro.worknote.LOG
import ru.smartro.worknote.log.todo.PlatformEntity
import ru.smartro.worknote.work.work.RealmRepository
import ru.smartro.worknote.presentation.ac.AbsRequest
import ru.smartro.worknote.presentation.ac.NetObject
import ru.smartro.worknote.todo
import kotlin.reflect.KClass

class RPOSTSynchro: AbsRequest<SynchroBodyIn, SynchroBodyOut>() {

    private var mTimeBeforeRequest: Long = App.getAppliCation().timeStampInSec()
    // TODO ::: Разбить на два запроса!!!
    private var mPlatformS: List<PlatformEntity> = listOf()


    override fun onGetSRVName(): String {
       return "synchro" 
    }

    override fun onGetRequestBodyIn(): SynchroBodyIn {
        LOG.todo("db.before")
        val db = RealmRepository(Realm.getDefaultInstance())
        LOG.todo("db.after")

        val lastSynchroTimeInSec = App.getAppParaMS().lastSynchroAttemptTimeInSec

        //проблема в секундах синхронизаций
        val m30MinutesInSec = 30 * 60

        if (App.getAppliCation().timeStampInSec() - lastSynchroTimeInSec > m30MinutesInSec) {
            mTimeBeforeRequest = lastSynchroTimeInSec + m30MinutesInSec
            mPlatformS = db.findPlatforms30min()
            LOG.debug( "SYNCworkER PLATFORMS IN LAST 30 min")
        }
        if (mPlatformS.isEmpty()) {
            mTimeBeforeRequest = App.getAppliCation().timeStampInSec()
            mPlatformS = db.findLastPlatforms()
            LOG.debug("SYNCworkER LAST PLATFORMS")
        }


        val gps = App.getAppliCation().gps()
        val synchronizeBodyIn = SynchroBodyIn(
            wb_id = App.getAppParaMS().wayBillId,
            hardware_ts = App.getAppliCation().timeStampInMS(),
            coords = gps.PointTOBaseData(),
            device = AppParaMS().deviceId, // val deviceId = Settings.Secure.getString(getAct().contentResolver, Settings.Secure.ANDROID_ID)
            lastKnownLocationTime = gps.PointTimeToLastKnowTime_SRV(),
            data = mPlatformS
        )
        
        return synchronizeBodyIn
    }

    override fun onSetQueryParameter(queryParamMap: HashMap<String, String>) {
        
    }

    override fun onBefore() {
        
    }

    override fun onAfter(bodyOut: SynchroBodyOut) {

        LOG.todo("db.before")
        val db = RealmRepository(Realm.getDefaultInstance())
        LOG.todo("db.after")

//                TODO :::  0:)
//                db().setConfig(ConfigName.AAPP__LAST_SYNCHROTIME_IN_SEC, timeBeforeRequest)
        App.getAppParaMS().lastSynchroAttemptTimeInSec = mTimeBeforeRequest

        if (mPlatformS.isNotEmpty()) {
            App.getAppParaMS().lastSynchroTimeInSec = mTimeBeforeRequest.toString()
            LOG.error( Thread.currentThread().getId().toString())
            db.updatePlatformNetworkStatus(mPlatformS)
            LOG.info("SUCCESS: ${Gson().toJson(bodyOut)}")
        } else {
            LOG.info("SUCCESS: GPS SENT")
        }
        val alertMsg = bodyOut.alert
        if (!alertMsg.isNullOrEmpty()) {
            App.getAppliCation().sentryLog("ValertMsgalertMsgalertMsgalertMsgalertMsg!!!!!!!")
        }
    }

    override fun onGetResponseClazz(): KClass<SynchroBodyOut> {
        return SynchroBodyOut::class
    }

}

class SynchroBodyIn(
    @Expose
    val wb_id: Int,
    @Expose
    val hardware_ts: Long,
    @Expose
    val coords: List<Double>,
    @Expose
    val device: String,
    @Expose
    val lastKnownLocationTime: Long,
    @Expose
    val data: List<PlatformEntity>?
): NetObject()

class SynchroBodyOut(
    @Expose
    val success: Boolean,
    @Expose
    val alert: String,
    @Expose
    val message: String
): NetObject()
