package ru.smartro.worknote.abs

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.sentry.Sentry
import org.slf4j.LoggerFactory
import ru.smartro.worknote.R

abstract class AbstractBottomDialog : BottomSheetDialogFragment() {
    protected val log = LoggerFactory.getLogger("${this::class.simpleName}")
    protected var TAG : String = "${this::class.simpleName}"
    protected fun getAct() = requireActivity() as AAct
    protected fun logSentry(text: String) {
        Sentry.addBreadcrumb("${TAG} : $text")
        log.info( "onCreate")
    }

    init {
        log.info( "init AbstractBottomDialog")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log.debug("onViewCreated")

    }

    protected fun navigateMain(navFragmentId: Int, argumentId: Int?, argumentName: String?=null) {
        val navHost = (getAct().supportFragmentManager.findFragmentById(R.id.fcv_container) as NavHostFragment)
        val navController = navHost.navController

        if (argumentId == null) {
            // TODO: !~r_dos
            navController.navigate(navFragmentId)
            return
        }
        val argSBundle = getArgSBundle(argumentId, argumentName)
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