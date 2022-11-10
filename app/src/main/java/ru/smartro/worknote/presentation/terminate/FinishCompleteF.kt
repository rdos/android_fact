package ru.smartro.worknote.presentation.terminate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import ru.smartro.worknote.abs.FragmentA
import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.ac.StartAct

class FinishCompleteF : FragmentA() {
    companion object {
        fun newInstance(workOrderId: Any? = null): FinishCompleteF {
            val fragment = FinishCompleteF()
//            fragment.addArgument(workOrderId)
            return fragment
        }
    }

    override fun onGetLayout(): Int {
        return R.layout.f_finish_complete
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideProgress()
//        App.getAppliCation().getRouter().navigateTo(LIST_SCREEN)
//        val workOrderId = getArgumentID()

        view.findViewById<Button>(R.id.finish_accept_btn).setOnClickListener {
            getAct().startActivity(Intent(getAct(), StartAct::class.java))
            getAct().finish()
        }
        view.findViewById<Button>(R.id.exit_btn).setOnClickListener {
            getAct().logout()
        }
    }
}