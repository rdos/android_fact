package ru.smartro.worknote.presentation.work

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.AActionDialogF
import ru.smartro.worknote.presentation.AFragmentYesNoDialog
import ru.smartro.worknote.presentation.ac.MainAct

class ReAuthWarningDialogF : AFragmentYesNoDialog() {
    override fun onNextFragment(entity: PlatformEntity) {
        startActivity(Intent(requireActivity(), MainAct::class.java))
        requireActivity().finish()
    }

    override fun onGetNextText(): String {
        return "Да, продолжить"
    }

    override fun onBackFragment(entity: PlatformEntity) {
        navigateNext(R.id.ReAuthConfirmationDialogF)
    }

    override fun onGetBackText(): String {
        return "Стереть и начать заново"
    }

    override fun onGetContentText(): String {
        return "У вас есть незавершенный маршрут.\nПродолжить?"
    }

    override fun onGetNavId(): Int {
        return R.id.ReAuthWarningDialogF
    }
}