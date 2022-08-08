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
import ru.smartro.worknote.App
import ru.smartro.worknote.Inull
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.toast

const val ARGUMENT_NAME___PARAM_ID = "ARGUMENT_NAME___PARAM_ID"
const val ARGUMENT_NAME___PARAM_NAME = "ARGUMENT_NAME___PARAM_NAME"
abstract class AFragment : Fragment(){
    protected var TAG : String = this::class.java.simpleName
    protected val log = LoggerFactory.getLogger("${this::class.simpleName}")

    //        get() {
//            val rootClazz =
//            rootClazz.getClass
//            while ()
//        }
    protected fun paramS() = App.getAppParaMS()
    fun getAct() = requireActivity() as AAct
    protected fun showingProgress(text: String? = null, isEmptyOldText: Boolean=false){
        //todo:ActAbstract
        log.debug("FRAG NAME: ${this::class.java.simpleName}")
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
        Sentry.addBreadcrumb("${TAG} : $text")
        // TODO: )))
        log.info( "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        try {
//          это провал!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        }
        log.debug("onCreateView onGetLayout.before")
        val view = inflater.inflate(onGetLayout(), container, false)
        log.debug("onCreateView onGetLayout.after")
        return view
    }

    protected fun navigateBack() {
        log.debug("navigateBack.before")

        val navHost = (getAct().supportFragmentManager.findFragmentById(R.id.fcv_container) as NavHostFragment)
        val navController = navHost.navController
        navController.navigateUp()
        log.trace("navigateBack.after")

    }

    protected fun navigateBack(navFragmentId: Int) {
        log.debug("navigateBack.before")
        log.debug("navigateBack .navFragmentId=${navFragmentId}")
        val navHost = (getAct().supportFragmentManager.findFragmentById(R.id.fcv_container) as NavHostFragment)
        val navController = navHost.navController
        navController.popBackStack(navFragmentId, false)
        log.trace("navigateBack.after")
    }


    protected fun navigateClose() {
        log.debug("navigateClose.before")
        getAct().finish()
        log.trace("navigateClose.after")
    }

    protected fun navigateMain(navFragmentId: Int, argumentId: Int?=null, argumentName: String?=null) {
        log.trace("navigateMain.before")
        log.debug("navigateMain .argumentId=${argumentId}, argumentName=${argumentName}")
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
        log.debug("getArgumentID.before")
        val result = requireArguments().getInt(ARGUMENT_NAME___PARAM_ID, Inull)
        log.info("getArgumentID .result = ${result}")
        return result
    }

    protected fun getArgumentName(): String? {
        log.debug("getArgumentName.before")
        val result = requireArguments().getString(ARGUMENT_NAME___PARAM_NAME)
        log.info("getArgumentName .result = ${result}")
        return result
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log.info("onViewCreated")
//        onCreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        log.debug("onDestroyView")

    }

    protected fun log(valueNameAndValue: String) {
        log.info( "${valueNameAndValue}")
    }

    open fun onBackPressed() {
        log.debug("onBackPressed")
    }

    open fun onNewGPS() {
        log.warn("onNewGPS")

    }
    //    companion object {
                                    //        private const val TAG = "CameraXBasic"
                                    //        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
                                    //        private const val PHOTO_EXTENSION = ".jpg"
                                    //        private const val RATIO_4_3_VALUE = 4.0 / 3.0
                                    //        private const val RATIO_16_9_VALUE = 16.0 / 9.0
                                    //
                                    //        private fun createFile(baseFolder: File, format: String, extension: String) =
                                    //            File(baseFolder, SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis()) + extension)
                                    //    }
}
