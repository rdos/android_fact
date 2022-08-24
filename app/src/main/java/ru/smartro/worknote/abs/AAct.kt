package ru.smartro.worknote.abs

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.*
import ru.smartro.worknote.andPOintD.ANOFragment

import ru.smartro.worknote.presentation.ac.StartAct
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


    //todo !r_dos onNEW-_Service(Srv) 
    public fun onNewGPS() {
        LOGbefore()
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.fcv_container) as NavHostFragment)
        (navHostFragment.childFragmentManager.fragments[0] as ANOFragment).onNewGPS()
        LOGafterLOG()
    }
    // TODO: !r_dos feed(stuff)




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
        log("onCreate")
    }


    val PUT_EXTRA_PARAM_ID: String = "PUT_EXTRA_PARAM_ID"
    fun getPutExtraParam_ID(): Int {
        val res = intent.getIntExtra(PUT_EXTRA_PARAM_ID, Inull)
        if (res == Inull) {
            log("res == Inull")
        }
        return res
    }

    protected val PUT_EXTRA_PARAM_NAME: String = "PUT_EXTRA_PARAM_TEXT"
    fun getPutExtraParam_NAME(): String {
        var res = intent.getStringExtra(PUT_EXTRA_PARAM_NAME)
        if (res.isNullOrEmpty()) {
            log("resres.isNullOrEmpty()")
            res = Snull
        }
        return res
    }

    override fun onPause() {
        super.onPause()
        log("onPause")
    }

    override fun onResume() {
        super.onResume()
        AppliCation().LASTact = this
        log("onResume")
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onStop() {
        super.onStop()
        log("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        AppliCation().LASTact = null
        log("onDestroy")

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
