package ru.smartro.worknote.presentation

import android.content.Intent
import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.PlatformEntity
import ru.smartro.worknote.presentation.abs.AConfirmYesNoDF

class DFConfirmYesVechicle : AConfirmYesNoDF() {
    override fun onNextFragment(entity: PlatformEntity) {
        startActivity(Intent(requireActivity(), ActMain::class.java))
        requireActivity().finish()
    }

    override fun onGetNextText(): String {
        return "Пропустить"
    }

    override fun onBackFragment(entity: PlatformEntity) {
        navigateClose()
    }

    override fun onGetBackText(): String {
        return "Стереть и начать заново"
    }

    override fun onGetContentText(): String {
        return "сделайте фото мусоровоза"
    }

    override fun onGetNavId(): Int {
        return R.id.ReAuthWarningDialogF
    }
}