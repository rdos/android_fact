package ru.smartro.worknote.presentation.platform_serve.walkthrough

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