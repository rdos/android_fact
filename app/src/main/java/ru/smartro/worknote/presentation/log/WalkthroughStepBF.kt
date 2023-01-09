package ru.smartro.worknote.presentation.log

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AF
//wAlKtHroUgh
class WalkthroughStepBF : AF() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onGetLayout(): Int {
        return R.layout.f_pserve_walkthrough_step_b
    }

    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<AppCompatButton>(R.id.acb_f_pserve_walkthrough_step_b__done).setOnClickListener {
            findNavController().popBackStack(R.id.FPServe, false)
        }
    }
}