package ru.smartro.worknote.abs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.SmartROllc

object AUFragment {
    fun showLastFragment(frag: IAFragment, navFragmentId: Int? = null) {
        LOG.debug("before")
        val fm = frag.getAct().supportFragmentManager
        val navHostFragment = fm.findFragmentById(R.id.fcv_container) as NavHostFragment
        val navController = navHostFragment.navController

        // TODO: ???R_dos!!!
        if (navFragmentId == null) {
            navController.navigateUp()
        } else {
            navController.popBackStack(navFragmentId, false)
        }
        LOG.debug("after")
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


}