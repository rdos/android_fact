package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.AInfoFD

class DInfoAirplaneModeOn : AInfoFD() {
    override fun onGetContentText(): String {
        return  getString(R.string.warning_airplane_mode)
    }

    override fun onGetNavId(): Int {
        return R.id.InfoAirplaneModeOnDF
    }

}