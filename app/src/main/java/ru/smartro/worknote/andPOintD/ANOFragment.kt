package ru.smartro.worknote.andPOintD

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.*
import ru.smartro.worknote.abs.AAct
import ru.smartro.worknote.abs.AUFragment
import ru.smartro.worknote.abs.IAFragment
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.presentation.ac.MainAct
import ru.smartro.worknote.work.PlatformEntity

const val ARGUMENT_NAME___PARAM_ID = "ARGUMENT_NAME___PARAM_ID"
const val ARGUMENT_NAME___PARAM_NAME = "ARGUMENT_NAME___PARAM_NAME"
abstract class ANOFragment : Fragment(), IAFragment {



    override fun onDestroy() {
        super.onDestroy()
        LOG.debug("before")
    }

    override fun onDetach() {
        super.onDetach()
        LOG.debug("before")
    }

    protected fun paramS() = App.getAppParaMS()

    protected fun showingProgress(text: String? = null, isEmptyOldText: Boolean=false){
        //todo:ActAbstract
        LOG.debug("FRAG NAME: ${this::class.java.simpleName}")
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
        LOG.debug("before")
        val view = AUFragment.onSetItemLayout(this, inflater, container, savedInstanceState)
        //        try {
//          это провал!!!
//        } try в имени это тру! VT, слышь)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LOG.trace("onInitLayoutView")
        LOG.trace("onViewCreated")
//        onCreate()
    }

    protected fun navigateBack() {
        LOG.debug("before")
        AUFragment.showLastFragment(this)
    }

    protected fun navigateBack(navFragmentId: Int) {
        LOG.debug("before")
        AUFragment.showLastFragment(this, navFragmentId)
    }


    protected fun navigateClose() {
        LOG.debug("before")
        getAct().finish()
        LOG.trace("after")
    }

    protected fun navigateSMallDeep(navFragmentId: Int, method: (PlatformEntity) -> Unit,argumentId: Int?=null, argumentName: String?=null) {
//        navigateMain(R.id.MapPlatformClickedDtlF, ::this.startPlatformService)
//        method.invoke()
    }
    protected fun navigateMain(navFragmentId: Int, argumentId: Int?=null, argumentName: String?=null) {
       LOG.trace("navigateMain.before")
        LOG.debug("navigateMain .argumentId=${argumentId}, argumentName=${argumentName}")

        AUFragment.showLastFragment(this, navFragmentId, argumentId, argumentName)
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

    override fun getArgSBundle(argumentId: Int, argumentName: String?): Bundle {
        LOG.debug("before")
        val bundle = AUFragment.setFragmentVar(argumentId, argumentName)
        return bundle
    }


    protected fun getArgumentID(): Int {
        val result = requireArguments().getInt(ARGUMENT_NAME___PARAM_ID, Inull)
        LOG.info("getArgumentID .result = ${result}")
        return result
    }

    protected fun getArgumentName(): String? {
        val result = requireArguments().getString(ARGUMENT_NAME___PARAM_NAME)
        LOG.info("getArgumentName .result = ${result}")
        return result
    }

    final override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (getAct() is MainAct) {
            (getAct() as MainAct).onNewfromAFragment(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LOG.debug("onDestroyView")
        //  displayManager.unregisterDisplayListener(displayListener)
    }


    open fun onNewGPS() {
       LOG.warn("onNewGPS")
    }

    override fun onResume() {
        LOG.info("before")
        super.onResume()
    }
    //    companion object {
                                    //        private const val TAG = "CameraXBasic"
                                    //        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
                                    //        private const val PHOTO_EXTENSION = ".jpg"
                                    //        private const val RATIO_4_3_VALUE = 4.0 / 3.0
                                    //        private const val RATIO_16_9_VALUE = 16.0 / 9.0
                                    //
    final override fun getAct() = requireActivity() as AAct

    override fun onBackPressed() {
        LOG.debug("onBackPressed")
    }

    //todo: ???onCreate
    // TODO: abstract fun onInitLayoutView(view: View): Boolean
    override fun onInitLayoutView(sview: SmartROllc): Boolean {
        LOG.warn(" LoG.todo()")
        return false
    }
    override fun onNewLiveData() {
        LOG.warn(" LoG.todo()")
    }

    //    // TODO:  abstract fun onBindLayoutState(): Boolean
//     open fun onBindLayoutState(): Boolean {
//        LOG.warn(" LoG.todo()")
//        return false
//    }
    override fun onBindLayoutState(): Boolean {
        return false
    }

}
