package ru.smartro.worknote.log.wAlKtHroUgh

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.FragmentA

class WalkthroughStepAF : FragmentA() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onGetLayout(): Int {
        return R.layout.f_pserve_walkthrough_step_a
    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack(R.id.PServeF, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        paramS().isWalkthroughWasShown = true
//

        view.findViewById<AppCompatButton>(R.id.acb_f_pserve_walkthrough_step_a__dismiss).setOnClickListener {
            findNavController().popBackStack(R.id.PServeF, false)
        }

        view.findViewById<AppCompatButton>(R.id.acb_f_pserve_walkthrough_step_a__next).setOnClickListener {
//            findNavController().navigate(R.id.WalkthroughStepBF)
        }
    }
}