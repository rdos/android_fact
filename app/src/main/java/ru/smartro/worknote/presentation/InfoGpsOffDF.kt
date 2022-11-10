package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AInfoDialog

class InfoGpsOffDF : AInfoDialog() {
    override fun onGetContentText(): String? {
        return getString(R.string.warning_gps_off)
    }

    override fun onGetNavId(): Int {
        return R.id.InfoGpsOffDF
    }

}