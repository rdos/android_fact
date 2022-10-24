package ru.smartro.worknote.abs

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.SmartROllc

object AUFragment {
    fun showLastFragment(fragment: IAFragment, navFragmentId: Int? = null) {
        LOG.debug("before")
        val fm = fragment.getAct().supportFragmentManager
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

    fun onViewCreated(aFragment: IAFragment, view: View, savedInstanceState: Bundle?) {
        LOG.warn("onInitLayoutView")
        if (view is SmartROllc) {
            val result = aFragment.onInitLayoutView(view) //ой пахнет savedInstanceState
            LOG.trace("onInitLayoutView.result=${result}")
        } else {
            throw Throwable("TODO: onViewCreated.if (view is SmartROLinearLayout) ")
            LOG.error("onInitLayoutView.result")
        }
        aFragment.onNewLiveData() //todo:r_dos!!!
        if (savedInstanceState == null) {
            LOG.debug("savedInstanceState == null")
        } else {
            LOG.debug("savedInstanceState HE null")
        }
    }


}