package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.abs.AInformDF

class DFInfoLockedCar: AInformDF() {

    companion object {
        const val NAV_ID = R.id.DFInfoLockedCar
    }

    override fun onGetContentText(): String {
        return getString(R.string.car_locked)
    }

}