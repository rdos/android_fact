package ru.smartro.worknote.presentation

import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.abs.AInformDF

class DFInfoLockedGas: AInformDF() {

    companion object {
        const val NAV_ID = R.id.DFInfoLockedGas
    }

    override fun onGetContentText(): String {
        return getString(R.string.gas_locked)
    }

}