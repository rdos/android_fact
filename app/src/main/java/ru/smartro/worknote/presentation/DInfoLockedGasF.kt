package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.AInfoFD

class DInfoLockedGasF: AInfoFD() {
    override fun onGetNavId(): Int {
        return R.id.LockedGasInfoDF
    }

    override fun onGetContentText(): String {
        return getString(R.string.gas_locked)
    }


}