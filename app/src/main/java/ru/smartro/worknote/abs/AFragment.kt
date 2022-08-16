package ru.smartro.worknote.abs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import io.sentry.Sentry
import org.slf4j.LoggerFactory
import ru.smartro.worknote.*
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress

const val ARGUMENT_NAME___PARAM_ID = "ARGUMENT_NAME___PARAM_ID"
const val ARGUMENT_NAME___PARAM_NAME = "ARGUMENT_NAME___PARAM_NAME"
abstract class AFragment : Fragment(){

    //        get() {
//            val rootClazz =
//            rootClazz.getClass
//            while ()
//        }
    protected fun paramS() = App.getAppParaMS()
    fun getAct() = requireActivity() as AAct
    protected fun showingProgress(text: String? = null, isEmptyOldText: Boolean=false){
        //todo:ActAbstract
        log("FRAG NAME: ${this::class.java.simpleName}")
        getAct().showingProgress(text, isEmptyOldText)
    }

    protected fun AppliCation() : App {
        return App.getAppliCation()
    }

    protected fun oops(){
        toast("Простите, Произошёл сбой. Inc:)oops!")
    }

    protected fun hideProgress(){
        getAct().hideProgress()
    }

    protected fun logSentry(text: String) {
        AppliCation().logSentry(text)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        try {
//          это провал!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        }
        log("onGetLayout.before")
        val view = inflater.inflate(onGetLayout(), container, false)
        log("onGetLayout.after")
        return view
    }

    protected fun navigateBack() {
        LOGbefore()
        val navHost = (getAct().supportFragmentManager.findFragmentById(R.id.fcv_container) as NavHostFragment)
        val navController = navHost.navController
        navController.navigateUp()
        LOGafterLOG()
    }

    protected fun navigateBack(navFragmentId: Int) {
        log("navigateBack.before")
        log("navigateBack .navFragmentId=${navFragmentId}")
        val navHost = (getAct().supportFragmentManager.findFragmentById(R.id.fcv_container) as NavHostFragment)
        val navController = navHost.navController
        navController.popBackStack(navFragmentId, false)
       LoG.trace("navigateBack.after")
    }


    protected fun navigateClose() {
        log("navigateClose.before")
        getAct().finish()
       LoG.trace("navigateClose.after")
    }

    protected fun navigateMain(navFragmentId: Int, argumentId: Int?=null, argumentName: String?=null) {
       LoG.trace("navigateMain.before")
        log("navigateMain .argumentId=${argumentId}, argumentName=${argumentName}")
        val navHost = (getAct().supportFragmentManager.findFragmentById(R.id.fcv_container) as NavHostFragment)
        val navController = navHost.navController

        if (argumentId == null) {
            // TODO: !~r_dos
//            navController.popBackStack(navFragmentId, true)
            navController.navigate(navFragmentId)
            return
        }
        val argSBundle = getArgSBundle(argumentId, argumentName)
        navController.navigate(navFragmentId, argSBundle)

    }

    protected fun navigateMainChecklist(navFragmentId: Int, argumentId: Int?, argumentName: String? = null) {
        val navHost = (getAct().supportFragmentManager.findFragmentById(R.id.fcv_container) as NavHostFragment)
        val navController = navHost.navController

        if (argumentId == null) {
            // TODO: !~r_dos
//            navController.popBackStack(navFragmentId, true)
            navController.navigate(navFragmentId)
            return
        }
        val argSBundle = getArgSBundle(argumentId, argumentName)
        navController.navigate(navFragmentId, argSBundle)

    }

    abstract fun onGetLayout(): Int

    fun getArgSBundle(argumentId: Int, argumentName: String ?= null): Bundle {
        val bundle = Bundle(2)
        bundle.putInt(ARGUMENT_NAME___PARAM_ID, argumentId)
        // TODO: 10.12.2021 let на всякий П???
        argumentName?.let {
            bundle.putString(ARGUMENT_NAME___PARAM_NAME, argumentName)
        }
        return bundle
    }


    protected fun getArgumentID(): Int {
        log("getArgumentID.before")
        val result = requireArguments().getInt(ARGUMENT_NAME___PARAM_ID, Inull)
       LoG.info("getArgumentID .result = ${result}")
        return result
    }

    protected fun getArgumentName(): String? {
        log("getArgumentName.before")
        val result = requireArguments().getString(ARGUMENT_NAME___PARAM_NAME)
       LoG.info("getArgumentName .result = ${result}")
        return result
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       LoG.info("onViewCreated")
//        onCreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        log("onDestroyView")

    }

    open fun onBackPressed() {
        log("onBackPressed")
    }

    open fun onNewGPS() {
       LoG.warn("onNewGPS")

    }
    //    companion object {
                                    //        private const val TAG = "CameraXBasic"
                                    //        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
                                    //        private const val PHOTO_EXTENSION = ".jpg"
                                    //        private const val RATIO_4_3_VALUE = 4.0 / 3.0
                                    //        private const val RATIO_16_9_VALUE = 16.0 / 9.0
                                    //

}
