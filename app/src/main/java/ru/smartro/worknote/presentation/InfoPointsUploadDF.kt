package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.abs.AInfoDialog

class InfoPointsUploadDF : AInfoDialog() {
    override fun onGetContentText(): String? {
        return  getString(R.string.warning_connection_lost)
    }

    override fun onGetNavId(): Int {
        return R.id.InfoPointsUploadDF
    }

}