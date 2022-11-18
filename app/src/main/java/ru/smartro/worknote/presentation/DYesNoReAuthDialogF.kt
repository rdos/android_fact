package ru.smartro.worknote.presentation

import android.content.Intent
import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.ac.MainAct
import ru.smartro.worknote.presentation.work.PlatformEntity

class DYesNoReAuthDialogF : AFragmentYesNoDialog() {
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