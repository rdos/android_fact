package ru.smartro.worknote.abs

import android.os.Bundle
import android.view.View
import ru.smartro.worknote.LOG
import ru.smartro.worknote.andPOintD.ANOFragment
import ru.smartro.worknote.andPOintD.SmartROLinearLayout
import ru.smartro.worknote.log


abstract class AFragment : ANOFragment(){
    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LOG.warn("onInitLayoutView")
        if (view is SmartROLinearLayout) {
            val result = onInitLayoutView(view) //ой пахнет savedInstanceState
            LOG.trace("onInitLayoutView.result=${result}")
        } else {
            throw Throwable("TODO: onViewCreated.if (view is SmartROLinearLayout) ")
            LOG.error("onInitLayoutView.result")
        }
        onNewLiveData() //todo:r_dos!!!
        if (savedInstanceState == null) {
            log("savedInstanceState == null")
        } else {
            log("savedInstanceState HE null")
        }

//        onCreate()
    }

}
