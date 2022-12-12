package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.abs.AInformDF

class DFInfoGpsOff : AInformDF() {
    override fun onGetContentText(): String? {
        return getString(R.string.warning_gps_off)
    }

    override fun onGetNavId(): Int {
        return R.id.DInfoGpsOffF
    }

}