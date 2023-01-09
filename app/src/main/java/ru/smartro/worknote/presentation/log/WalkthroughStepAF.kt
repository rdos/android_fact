package ru.smartro.worknote.presentation.log

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AF
//wAlKtHroUgh
class WalkthroughStepAF : AF() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onGetLayout(): Int {
        return R.layout.f_pserve_walkthrough_step_a
    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack(R.id.FPServe, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        paramS().isWalkthroughWasShown = true
//

        view.findViewById<AppCompatButton>(R.id.acb_f_pserve_walkthrough_step_a__dismiss).setOnClickListener {
            findNavController().popBackStack(R.id.FPServe, false)
        }

        view.findViewById<AppCompatButton>(R.id.acb_f_pserve_walkthrough_step_a__next).setOnClickListener {
//            findNavController().navigate(WalkthroughStepBF.NAV_ID)
        }
    }
}