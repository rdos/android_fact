package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.abs.AInformDF

class DFInfoAirplaneModeOn : AInformDF() {

    companion object {
        const val NAV_ID = R.id.DFInfoAirplaneModeOn
    }

    override fun onGetContentText(): String {
        return  getString(R.string.warning_airplane_mode)
    }

}