package ru.smartro.worknote.work.abs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.sentry.Sentry
import ru.smartro.worknote.App
import ru.smartro.worknote.BuildConfig
import ru.smartro.worknote.Inull
import ru.smartro.worknote.Snull
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.util.MyUtil.toStr
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


    protected fun AppliCation() : App {
        return App.getAppliCation()
    }

    protected fun paramS() : App.SharedPref {
        return App.getAppParaMS()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    protected abstract fun onNewGPS()

    public fun newsFROMlocationSERVICE() {
        onNewGPS()
    }
    private var mMethodName: String? = null

    public var TAG : String = "${this::class.simpleName}"
    private val TAGLOG = "AActLOG"
    protected fun LOGbefore(method: String, valueName: String = "") {
        AppliCation().before(method, valueName)
    }

    private fun logAfterResult(result: String) {
        Log.d(TAG, "${mMethodName}.after result=${result} ")
        mMethodName = null

    }

    protected fun LOGafter(res: Boolean? = null) {
        logAfterResult(res.toStr())
    }

    protected fun LOGWork(valueNameAndValue: String) {
        mMethodName?.let {
            Log.i(TAG, "${TAG}:${mMethodName}.${valueNameAndValue}")
            return@LOGWork
        }
        Log.i(TAG, "${TAG}:${valueNameAndValue}")
    }

    protected fun LOGWork(valueName: String, value: Int) {
        LOGWork("${valueName}=$value\"")
    }

    protected fun logSentry(text: String) {
        Sentry.addBreadcrumb("${TAG} : $text")
        Log.i(TAG + "Sent", text)
    }
    protected fun logSentry(data: Int) {
        logSentry(data.toString())
    }


    protected fun isOopsMode(): Boolean{
        // TODO:r_dos не сп роста Вам
//        mIsOopsMode? wft!!
        return mIsOopsMode!!
    }

    public fun isDevelMode() = App.getAppParaMS().isModeDEVEL

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

        AppliCation().runSyncWorkER()
        AppliCation().runLocationService()
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


}


//TODO: find next шагай)_/gotoAdd = Add -> create->ПОТОМ->Save->ПОТОМ Show
/** :r_dos::_1n0w0=переделать, удалить=не то 100% внимание /
:yo-11now0 пудов, но форма странная.
        */