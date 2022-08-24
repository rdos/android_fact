package ru.smartro.worknote.abs

import android.os.Bundle
import android.view.View
import ru.smartro.worknote.LoG
import ru.smartro.worknote.andPOintD.ANOFragment
import ru.smartro.worknote.andPOintD.SmartROLinearLayout
import ru.smartro.worknote.log


abstract class AFragment : ANOFragment(){
    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LoG.warn("onInitLayoutView")
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

}
