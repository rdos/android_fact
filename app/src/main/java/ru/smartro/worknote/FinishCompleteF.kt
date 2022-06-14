package ru.smartro.worknote

import android.os.Bundle
import android.view.View

class FinishCompleteF : AFragment() {
    companion object {
        fun newInstance(workOrderId: Any): FinishCompleteF {
            workOrderId as Int
            val fragment = FinishCompleteF()
            fragment.addArgument(workOrderId)
            return fragment
        }
    }

    override fun onGetLayout(): Int {
        return R.layout.start_act__rv_item
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideProgress()
//        App.getAppliCation().getRouter().navigateTo(LIST_SCREEN)
        val workOrderId = getArgumentID()

        }
}