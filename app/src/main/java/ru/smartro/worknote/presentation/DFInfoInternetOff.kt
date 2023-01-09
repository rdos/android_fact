package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.abs.AInformDF

class DFInfoInternetOff : AInformDF() {

    companion object {
        const val NAV_ID = R.id.DFInfoInternetOff
    }

    override fun onGetContentText(): String? {
        return  getString(R.string.warning_connection_lost)
    }

}