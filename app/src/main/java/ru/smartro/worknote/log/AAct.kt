package ru.smartro.worknote.log

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.sentry.Sentry
import ru.smartro.worknote.*
import ru.smartro.worknote.andPOintD.IActTooltip

import ru.smartro.worknote.awORKOLDs.util.MyUtil.toStr
import ru.smartro.worknote.work.ac.StartAct
import java.lang.Exception

//        try {
//            throw Exception("This is a devel.")
//        } catch (e: Exception) {
//            Sentry.captureException(e)
//        }
abstract class AAct : AppCompatActivity() {
//abstract class AAct : AppCompatActivity(), IActTooltip {
//    override fun onNewfromAFragment() {
//
//    }

    protected fun AppliCation() : App {
        return App.getAppliCation()
    }

    protected fun oops(){
        toast("Простите, Произошёл сбой. Inc:)oops!")
    }

    protected fun paramS() = paramS
    protected val paramS: AppParaMS by lazy {
        App.getAppParaMS()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    //todo: modeSyNChrON_off(false)="читается так="-mode off is enabled? -нет
    fun modeSyNChrON_off(isEnabled: Boolean = true){
        if (isEnabled) {
            paramS.isModeSYNChrONize = false
        } else{
            if (!paramS.isModeSYNChrONize) {
                AppliCation().cancelNotification(null)
            }
            paramS.isModeSYNChrONize = true
        }
    }

    protected abstract fun onNewGPS()

    //todo !r_dos onNEW-_Service(Srv) 
    public fun onNEWfromGPSSrv() {
        beforeLOG("onNewGPS")
        onNewGPS()
        LOGafter()
    }
    // TODO: !r_dos feed(stuff)
    
    private var mMethodName: String? = null

    public var TAG : String = "${this::class.simpleName}"
    private val TAGLOG = "AActLOG"
    protected fun beforeLOG(method: String, valueName: String = "") {
        AppliCation().beforeLOG(method, valueName)
    }

    private fun logAfterResult(result: String) {
        log("${mMethodName}.after result=${result} ")
        mMethodName = null

    }

    protected fun LOGafter(res: Boolean? = null) {
        logAfterResult(res.toStr())
    }

    protected fun log(valueNameAndValue: String) {
        mMethodName?.let {
            Log.i(TAG, "${TAG}:${mMethodName}.${valueNameAndValue}")
            return@log
        }
        Log.i(TAG, "${TAG}:${valueNameAndValue}")
    }

    protected fun log(valueName: String, value: Int) {
        log("${valueName}=$value\"")
    }

    fun logSentry(text: String) {
        Sentry.addBreadcrumb("${TAG} : $text")
        Log.i(TAG + "Sent", text)
    }
    protected fun logSentry(data: Int) {
        logSentry(data.toString())
    }

    fun INcyclEStart(s: String) {
        mMethodName?.let {
            Log.d(TAGLOG, "${mMethodName}.CYCLes.${s}")
            return@INcyclEStart
        }
        Log.d(TAGLOG, "CYCLes.${s}")
    }

    fun INcyclEStop() {
        mMethodName?.let {
            Log.d(TAGLOG, "${mMethodName}.************-_(:;)")
            return@INcyclEStop
        }
        Log.d(TAGLOG, ".************-_(:;)")
    }


    public fun isDevelMode(): Boolean{
        return AppliCation().isDevelMode()
    }

    protected fun setAntiErrorClick(itemView: View) {
        itemView.isEnabled = false
        itemView.postDelayed({
            try {
                itemView.isEnabled = true
            } catch (ex: Exception) {
                // TODO: r_dos
            }
        }, 333)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }


    val PUT_EXTRA_PARAM_ID: String = "PUT_EXTRA_PARAM_ID"
    fun getPutExtraParam_ID(): Int {
        val res = intent.getIntExtra(PUT_EXTRA_PARAM_ID, Inull)
        if (res == Inull) {
            logSentry("res == Inull")
        }
        return res
    }

    protected val PUT_EXTRA_PARAM_NAME: String = "PUT_EXTRA_PARAM_TEXT"
    fun getPutExtraParam_NAME(): String {
        var res = intent.getStringExtra(PUT_EXTRA_PARAM_NAME)
        if (res.isNullOrEmpty()) {
            logSentry("resres.isNullOrEmpty()")
            res = Snull
        }
        return res
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onResume() {
        super.onResume()
        AppliCation().LASTact = this
        Log.d(TAG, "onResume")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        AppliCation().LASTact = null
        Log.d(TAG, "onDestroy")

    }

    protected fun restartApp() {
        val intent = Intent(this, StartAct::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        this.startActivity(intent)
    }

    fun logout() {
        App.getAppParaMS().setLogoutParams()
        val intent = Intent(this, StartAct::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
