package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AInfoDialog

class BaSeInfoAirplaneModeOnDF : AInfoDialog() {
    override fun onGetContentText(): String {
        return  getString(R.string.warning_airplane_mode)
    }

    override fun onGetNavId(): Int {
        return R.id.InfoAirplaneModeOnDF
    }

}