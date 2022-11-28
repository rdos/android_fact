package ru.smartro.worknote.awORKOLDs

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import io.realm.Realm
import ru.smartro.worknote.App
import ru.smartro.worknote.AppParaMS
import ru.smartro.worknote.LOG
import ru.smartro.worknote.awORKOLDs.service.NetObject
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.presentation.work.PlatformEntity
import ru.smartro.worknote.presentation.work.RealmRepository
import ru.smartro.worknote.todo
import kotlin.reflect.KClass

class SynchroRequestPOST: AbsRequest<SynchroBodyIn, SynchroBodyOut>() {

    private var mTimeBeforeRequest: Long = MyUtil.timeStampInSec()
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

        if (MyUtil.timeStampInSec() - lastSynchroTimeInSec > m30MinutesInSec) {
            mTimeBeforeRequest = lastSynchroTimeInSec + m30MinutesInSec
            mPlatformS = db.findPlatforms30min()
            LOG.debug( "SYNCworkER PLATFORMS IN LAST 30 min")
        }
        if (mPlatformS.isEmpty()) {
            mTimeBeforeRequest = MyUtil.timeStampInSec()
            mPlatformS = db.findLastPlatforms()
            LOG.debug("SYNCworkER LAST PLATFORMS")
        }

        val gps = App.getAppliCation().gps()
        val synchronizeBodyIn = SynchroBodyIn(
            wb_id = App.getAppParaMS().wayBillId,
            coords = gps.PointTOBaseData(),
            device = AppParaMS().deviceId, // val deviceId = Settings.Secure.getString(getAct().contentResolver, Settings.Secure.ANDROID_ID)
            lastKnownLocationTime = gps.PointTimeToLastKnowTime_SRV(),
            data = PlatformEntity.toSRV(mPlatformS, db)
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
            App.getAppliCation().logSentry("ValertMsgalertMsgalertMsgalertMsgalertMsg!!!!!!!")
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