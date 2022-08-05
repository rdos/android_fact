package ru.smartro.worknote.presentation.platform_serve.wAlKtHroUgh

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import ru.smartro.worknote.abs.AFragment
import ru.smartro.worknote.R

class WalkthroughStepBF : AFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TEST: :::", "WALKTHROUGH B CREATE")
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
            findNavController().popBackStack(R.id.PServeF, false)
        }
    }
}