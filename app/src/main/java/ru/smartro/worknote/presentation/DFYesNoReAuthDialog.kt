package ru.smartro.worknote.presentation

import android.content.Intent
import ru.smartro.worknote.App
import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.PlatformEntity
import ru.smartro.worknote.presentation.abs.AConfirmYesNoDF

class DFYesNoReAuthDialog : AConfirmYesNoDF() {
    override fun onNextFragment(entity: PlatformEntity) {
        App.getAppParaMS().showClearCurrentTasks = false
        startActivity(Intent(requireActivity(), ActMain::class.java))
        requireActivity().finish()
    }

    override fun onGetNextText(): String {
        return "Да, продолжить"
    }

    override fun onBackFragment(entity: PlatformEntity) {
        navigateNext(R.id.DFConfirmReAuth)
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