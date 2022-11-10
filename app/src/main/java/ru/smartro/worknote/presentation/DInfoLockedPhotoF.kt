package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.andPOintD.AInfoFD

class LockedPhotoInfo: AInfoFD() {
    override fun onGetNavId(): Int {
        return R.id.LockedPhotoInfoDF
    }

    override fun onGetContentText(): String {
        return getString(R.string.gas_locked)
    }


}