package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.abs.AInformDF

class DFInfoGpsOff : AInformDF() {

    companion object {
        const val NAV_ID = R.id.DFInfoGpsOff
    }

    override fun onGetContentText(): String? {
        return getString(R.string.warning_gps_off)
    }

}