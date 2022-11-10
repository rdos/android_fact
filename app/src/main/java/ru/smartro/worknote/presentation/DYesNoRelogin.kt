package ru.smartro.worknote.presentation

import ru.smartro.worknote.R

class DYesNoRelogin : AFragmentInfoDialog() {
    override fun onGetContentText(): String {
        return  "Вы точно хотите выйти?"
    }

    override fun onGetNavId(): Int {
        return R.id.InfoAirplaneModeOnDF
    }

}