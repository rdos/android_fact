package ru.smartro.worknote.presentation

import ru.smartro.worknote.R

class DInfoLockedGasF: AFragmentInfoDialog() {
    override fun onGetNavId(): Int {
        return R.id.DInfoLockedGasF
    }

    override fun onGetContentText(): String {
        return getString(R.string.gas_locked)
    }


}