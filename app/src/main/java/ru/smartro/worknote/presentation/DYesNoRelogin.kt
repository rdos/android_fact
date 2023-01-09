package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.PlatformEntity
import ru.smartro.worknote.presentation.abs.AConfirmYesNoDF

class DYesNoRelogin : AConfirmYesNoDF() {

    companion object {
        const val NAV_ID = R.id.DYesNoRelogin
    }

    override fun onNextFragment(entity: PlatformEntity) {
        getAct().logout()
    }

    override fun onGetContentText(): String {
        return  "Вы точно хотите выйти?"
    }
}