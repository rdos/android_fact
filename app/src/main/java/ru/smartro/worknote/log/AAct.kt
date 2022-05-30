package ru.smartro.worknote.log

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.sentry.Sentry
import ru.smartro.worknote.*
import ru.smartro.worknote.awORKOLDs.extensions.toast
import ru.smartro.worknote.awORKOLDs.util.MyUtil.toStr
import ru.smartro.worknote.work.ac.StartAct
import ru.smartro.worknote.work.ac.checklist.StartVehicleAct
import java.lang.Exception

//        try {
//            throw Exception("This is a devel.")
//        } catch (e: Exception) {
//            Sentry.captureException(e)
//        }
//todo:r_dos::_know1=знаешь точно, что ОСТАВИТЬ И НУЖНО ВСЕМУ коГду(а))
//       ::_know0=а это не ОСТАВЛЯТЬ
abstract class AAct : AppCompatActivity() {
    private var mIsOopsMode: Boolean? = false
//скольский пол
    protected fun AppliCation() : App {
        return App.getAppliCation()
    }

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

    protected fun logSentry(text: String) {
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


    protected fun isOopsMode(): Boolean{
        // TODO:r_dos не сп роста Вам
//        mIsOopsMode? wft!!
        return mIsOopsMode!!
    }

    public fun isDevelMode(): Boolean{
        return AppliCation().isDevelMode()
    }

    fun oopsTestqA() {

    }

    //oops долежн быть один на Act, но на Activity
    protected fun oops(){
        if (mIsOopsMode == true) {
            //CiДУ
            logSentry("oops")
        }
        mIsOopsMode = true

        toast("Простите, Произошёл сбой. Inc:)oops!")
    }

    //:))
    private fun setSpyPutExtraParamId(intent: Intent) {
//        workorderId?.let {
//            intent.putExtra(PUT_EXTRA_PARAM_ID, workorderId)
//        }
    }

    protected fun sendMessage(clazz: Class<StartVehicleAct>) {
        //fragment в помощь!
//        protected fun sendMessage(clazz: Class<out AbstractAct>) {
        val intent = Intent(this, clazz::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        setSpyPutExtraParamId(intent)
        startActivity(intent)
/**   intent: ей я сообщениею.
 * AppPreferences: ей это же она сообщение(эй это я сообщение)ей это же она сообщение
        savedInstanceState: ей ей, Пацаны, вы е попутали? это она сообщение.)):()
    } */}

    // TODO: )
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


    protected val PUT_EXTRA_PARAM_ID: String = "PUT_EXTRA_PARAM_ID"
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


//TODO: find next шагай)_/gotoAdd = Add -> create->ПОТОМ->Save->ПОТОМ Show
/** :r_dos::_1n0w0=переделать, удалить=не то 100% внимание /
:yo-11now0 пудов, но форма странная.
        */