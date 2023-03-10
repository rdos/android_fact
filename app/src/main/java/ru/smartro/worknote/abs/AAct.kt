package ru.smartro.worknote.abs

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.*


import ru.smartro.worknote.presentation.ActStart
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
        LOG.debug("before")
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.fcv_container) as NavHostFragment)
        (navHostFragment.childFragmentManager.fragments[0] as AF).onNewGPS()
        LOG.debug("after")
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
        App.getAppliCation().setCurrentAct(this)
        LOG.debug("onCreate")
    }


    val PUT_EXTRA_PARAM_ID: String = "PUT_EXTRA_PARAM_ID"
    fun getPutExtraParam_ID(): Int {
        val res = intent.getIntExtra(PUT_EXTRA_PARAM_ID, Inull)
        if (res == Inull) {
            LOG.debug("res == Inull")
        }
        return res
    }

    protected val PUT_EXTRA_PARAM_NAME: String = "PUT_EXTRA_PARAM_TEXT"
    fun getPutExtraParam_NAME(): String {
        var res = intent.getStringExtra(PUT_EXTRA_PARAM_NAME)
        if (res.isNullOrEmpty()) {
            LOG.debug("resres.isNullOrEmpty()")
            res = Snull
        }
        return res
    }

    private fun clearReferences() {
        LOG.debug("before")
        val currentAct = App.getAppliCation().getCurrentAct()
        if (this == currentAct) {
            LOG.debug("if (this == currentAct) {")
            App.getAppliCation().setCurrentAct(null)
        }
    }

    override fun onPause() {
        LOG.debug("before")
        clearReferences()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        App.getAppliCation().setCurrentAct(this)
        LOG.debug("before")
    }

    override fun onStart() {
        super.onStart()
        LOG.debug("before")
    }

    override fun onStop() {
        super.onStop()
        LOG.debug("before")
    }

    override fun onDestroy() {
        super.onDestroy()
        clearReferences()
        LOG.debug("before")

    }

    protected fun restartApp() {
        val intent = Intent(this, ActStart::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        this.startActivity(intent)
    }

    fun logout() {
        App.getAppParaMS().setLogoutParams()
        val intent = Intent(this, ActStart::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun showNextFragment(navFragmentId: Int, argumentId: Int? = null, argumentName: String?=null) {
        LOG.debug("before")
        val fm = this.supportFragmentManager
        val navHostFragment = fm.findFragmentById(R.id.fcv_container) as NavHostFragment
        val navController = navHostFragment.navController
        //        val navOptions = NavOptions.Builder().setPopUpTo(navFragmentId, true).build()
        if (argumentId == null) {
            // TODO: !~r_dos
            //            navController.popBackStack(navFragmentId, true)
            navController.navigate(navFragmentId, null)
            return
        }
        val argSBundle = this.getArgSBundle(argumentId, argumentName)

        navController.navigate(navFragmentId, argSBundle)
    }

    fun getArgSBundle(argumentId: Int, argumentName: String?=null): Bundle {
        val bundle = Bundle(2)
        bundle.putInt(ARGUMENT_NAME___PARAM_ID, argumentId)
        // TODO: 10.12.2021 let на всякий П???
        argumentName?.let {
            bundle.putString(ARGUMENT_NAME___PARAM_NAME, argumentName)
        }
        return bundle
    }
}
