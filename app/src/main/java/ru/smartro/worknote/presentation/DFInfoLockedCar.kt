package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.abs.AInformDF

class DFInfoLockedCar: AInformDF() {
    override fun onGetNavId(): Int {
        return R.id.LockedCarInfoDF
    }

    override fun onGetContentText(): String {
        return getString(R.string.photo_locked)
    }


}