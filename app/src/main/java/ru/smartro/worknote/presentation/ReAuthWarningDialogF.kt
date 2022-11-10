package ru.smartro.worknote.presentation

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.AActionDialogF
import ru.smartro.worknote.presentation.ac.MainAct

class ReAuthWarningDialogF: AActionDialogF() {

    private val vm: ServePlatformVM by activityViewModels()

    override fun onLayoutInitialized() {
        actvTitle?.text = "Предупреждение"
        actvContent?.text = "У вас есть незавершенный маршрут.\nПродолжить?"

        acbAccept?.text = "Да, продолжить"
        acbAccept?.setOnClickListener {
            // TODO :::
//            findNavController().navigate(R.id.MapPlatformsF)

            startActivity(Intent(requireActivity(), MainAct::class.java))

            requireActivity().finish()
        }

        acbDecline?.text = "Стереть и начать заново"
        acbDecline?.setOnClickListener {
            findNavController().navigate(R.id.ReAuthConfirmationDialogF)
        }
    }
}