package ru.smartro.worknote.presentation.platform_serve.walkthrough

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.util.StatusEnum
import ru.smartro.worknote.toast

class WalkthroughStepAF : AFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TEST: :::", "walkt A CREATE")
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
        paramS().isWalkthroughWasShown = true

        view.findViewById<AppCompatButton>(R.id.acb_f_pserve_walkthrough_step_a__dismiss).setOnClickListener {
            findNavController().popBackStack(R.id.PServeF, false)
        }

        view.findViewById<AppCompatButton>(R.id.acb_f_pserve_walkthrough_step_a__next).setOnClickListener {
            findNavController().navigate(R.id.fragment_walkthrough_b)
        }
    }
}