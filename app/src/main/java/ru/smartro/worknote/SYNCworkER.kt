package ru.smartro.worknote

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.realm.Realm
import kotlinx.coroutines.delay
import ru.smartro.worknote.presentation.RPOSTSynchro
import ru.smartro.worknote.presentation.ActStart
import ru.smartro.worknote.work.work.RealmRepository

//private var App.LocationLAT: Double
//    get() {
//        TODO("Not yet implemented")
//    }
//    set() {}


class SYNCworkER(
    p_application: Context,
    params: WorkerParameters
) : CoroutineWorker(p_application, params) {



    // TODO: r_dos....кака код))раз
    private fun showWorkERNotification(isForceMode: Boolean = true,
                                       contentText: String = "Не закрывайте приложение",
                                       titleText: String = "Служба отправки данных работает") {

        val intent = Intent(applicationContext, ActStart::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = getActivityProperly(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (isForceMode) {
            App.getAppliCation().showNotificationForce(pendingIntent, contentText, titleText)
        } else {
            App.getAppliCation().showNotification(pendingIntent, contentText, titleText)
        }
    }
    private var oldThreadId = Lnull
    private var mDb: RealmRepository? = null

    fun db(): RealmRepository {
        val currentThreadId = Thread.currentThread().id
        if (oldThreadId != currentThreadId) {
            LOG.debug("SYNCworkER::db:.currentThreadId=${currentThreadId} ")
            LOG.debug("SYNCworkER::db:.oldThreadId=${oldThreadId} ")
            oldThreadId = currentThreadId
            mDb = RealmRepository(Realm.getDefaultInstance())
            return mDb!!
        }
        return mDb!!
    }

    private fun showWorkERROR(contentText: String="ОШИБКА служба ОТПРАВКИ данных НЕ работает",
                              title: String="Попробуйте перезайти в приложение") {
        showWorkERNotification(true, contentText, title)
    }

    // TODO: r_dos.)))кака код...лох это судьба))два
    override suspend fun doWork(): Result {
        LOG.debug("before")
        var isFirstRun: Boolean = true
        var isModeSyncOldVal = false

        val params = App.getAppParaMS()
        params.isModeSYNChrONize_FoundError = false

        try {
            val DELAY_MS: Long =  if (App.getAppParaMS().isModeDEVEL) 11_011 else 30_000
            while (true) {
                LOGinCYCLEStart("while (true)")

                if (isModeSyncOldVal != params.isModeSYNChrONize) {
                    isFirstRun = true
                }
                isModeSyncOldVal = params.isModeSYNChrONize
                if (params.isModeSYNChrONize) {
                    LOG.debug( "SYNCworkER RUN")
                    synChrONizationDATA()
//                    ping()
                    if (isFirstRun) {
                        showWorkERNotification(true)
                    }
                } else {
                    LOG.debug("SYNCworkER STOPPED")
                    if (isFirstRun) {
                        showWorkERNotification(true, contentText = "Служба отправки данных не работает",
                            titleText = "Отправка Данных ПРИОСТАНОВЛЕНА")
                    }
                }
                isFirstRun = false
                LOGINcyclEStop()
                delay(DELAY_MS)
            } //todo: while (true) {
        } catch (eXthr: Throwable) {
            LOG.error("eXthr", eXthr)
//            аккуратней::r_dos
            params.isModeSYNChrONize_FoundError = true
            showWorkERROR()
            throw eXthr
        }
    }

    private fun synChrONizationDATA() {
        LOG.debug("before")
        App.getAppliCation().sentryLog("SYNCworkER STARTED")

        val synchroRequest = RPOSTSynchro()

        App.oKRESTman().put(synchroRequest)

    }

}
/**   //SYNCworkER

//    fun INcyclEStart(s: String) {
//        mMethodName?.let {
//            LOG.debug("${mMethodName}.CYCLes.${s}")
//            return@INcyclEStart
//        }
//        LOG.debug("CYCLes.${s}")
//    }
//
//    fun INcyclEStop() {
//        mMethodName?.let {
//            LOG.debug("${mMethodName}.************-_(:;)")
//            return@INcyclEStop
//        }
//        LOG.debug(".************-_(:;)")
//    }

//    //SYNCworkER
//    protected fun LOGafterLOG.debug(res: String) {
//        logAfterResult(res.toStr())
//    }
//
//    //    SYNCworkER
//    protected fun LOGafterLOG.debug(res: Boolean? = null) {
//        logAfterResult(res.toStr())
//    }

//    private fun logAfterResult(result: String) {
//        result?.let {
//            LOG.debug("${mMethodName}.after result=${result} ")
//            return@logAfterResult
//        }
//        LOG.debug("${mMethodName}.after")
//        mMethodName = null
//    }
!r_dos * R_dos!!!**/
