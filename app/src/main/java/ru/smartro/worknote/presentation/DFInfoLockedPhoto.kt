package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.abs.AInformDF

class DFInfoLockedPhoto: AInformDF() {

    companion object {
        const val NAV_ID = R.id.DFInfoLockedPhoto
    }

    override fun onGetContentText(): String {
        return getString(R.string.photo_locked)
    }


}