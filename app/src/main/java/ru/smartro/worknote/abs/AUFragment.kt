
package ru.smartro.worknote.abs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.ARGUMENT_NAME___PARAM_ID
import ru.smartro.worknote.andPOintD.ARGUMENT_NAME___PARAM_NAME
import ru.smartro.worknote.andPOintD.SmartROllc

object AUFragment {

    fun showNextFragment(frag: IAFragment, navFragmentId: Int, argumentId: Int?, argumentName: String?=null) {
        LOG.debug("frag.javaClass.name:${frag.javaClass.name}")
        val fm = frag.getAct().supportFragmentManager
        val navHostFragment = fm.findFragmentById(R.id.fcv_container) as NavHostFragment
        val navController = navHostFragment.navController
        //        val navOptions = NavOptions.Builder().setPopUpTo(navFragmentId, true).build()
        if (argumentId == null) {
            // TODO: !~r_dos
            //            navController.popBackStack(navFragmentId, true)
            navController.navigate(navFragmentId, null)
            return
        }
        val argSBundle = frag.getArgSBundle(argumentId, argumentName)

        navController.navigate(navFragmentId, argSBundle)
    }

    fun showFragment(frag: IAFragment, navFragmentId: Int? = null) {
        LOG.debug("frag.javaClass.name:${frag.javaClass.name}")
        val fm = frag.getAct().supportFragmentManager
        val navHostFragment = fm.findFragmentById(R.id.fcv_container) as NavHostFragment
        val navController = navHostFragment.navController

        // TODO: ???R_dos!!!
        if (navFragmentId == null) {
            navController.navigateUp()
        } else {
            navController.popBackStack(navFragmentId, false)
        }
        LOG.debug("::")
    }

    fun onViewCreated(frag: IAFragment, view: View, savedInstanceState: Bundle?) {
        LOG.warn("onInitLayoutView")
        if (view is SmartROllc) {
            val result = frag.onInitLayoutView(view) //ой пахнет savedInstanceState
            LOG.trace("onInitLayoutView.result=${result}")
        } else {
            throw Throwable("TODO: onViewCreated.if (view is SmartROllc) ")
            LOG.error("onInitLayoutView.result")
        }
        frag.onNewLiveData() //todo:r_dos!!!
        if (savedInstanceState == null) {
            LOG.debug("savedInstanceState == null")
        } else {
            LOG.debug("savedInstanceState HE null")
        }
    }

    fun onSetItemLayout(frag: IAFragment, inflater: LayoutInflater, cont: ViewGroup?, sIS: Bundle?): View {
        LOG.warn("onGetLayout:before")
        val sview = inflater.inflate(frag.onGetLayout(), cont, false)
        LOG.warn("after:onGetLayout")
        return sview
    }

    fun setFragmentVar(argumentId: Int, argumentName: String?): Bundle {
        val bundle = Bundle(2)
        bundle.putInt(ARGUMENT_NAME___PARAM_ID, argumentId)
        // TODO: 10.12.2021 let на всякий П???
        argumentName?.let {
            bundle.putString(ARGUMENT_NAME___PARAM_NAME, argumentName)
        }
        return bundle
    }


}