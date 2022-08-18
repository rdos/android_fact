package ru.smartro.worknote.andPOintD

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AAct
import ru.smartro.worknote.andPOintD.SmartROLinearLayout
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.presentation.ac.MainAct

const val ARGUMENT_NAME___PARAM_ID = "ARGUMENT_NAME___PARAM_ID"
const val ARGUMENT_NAME___PARAM_NAME = "ARGUMENT_NAME___PARAM_NAME"
abstract class ANOFragment : Fragment(){
     abstract fun onGetLayout(): Int

    //todo: ???onCreate
    // TODO: abstract fun onInitLayoutView(view: View): Boolean
    protected open fun onInitLayoutView(view: SmartROLinearLayout): Boolean {
        LoG.warn(" LoG.todo()")
        return true
    }

    // TODO:  abstract fun onBindLayoutState(): Boolean
    protected open fun onBindLayoutState(): Boolean {
        LoG.warn(" LoG.todo()")
        return false
    }

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
        LoG.debug("onInitLayoutView")
        if (view is SmartROLinearLayout) {
            val result = onInitLayoutView(view) //ой пахнет savedInstanceState
            LoG.trace("onInitLayoutView.result=${result}")
        } else {
            throw Throwable("TODO: onViewCreated.if (view is SmartROLinearLayout) ")
            LoG.error("onInitLayoutView.result")
        }
        onNewLiveData() //todo:r_dos!!!
        if (savedInstanceState == null) {
            log("savedInstanceState == null")
        } else {
            log("savedInstanceState HE null")
        }

//        onCreate()
    }

    open fun onNewLiveData() {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (getAct() is MainAct) {
            (getAct() as MainAct).onNewfromAFragment(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        log("onDestroyView")
        //  displayManager.unregisterDisplayListener(displayListener)
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
