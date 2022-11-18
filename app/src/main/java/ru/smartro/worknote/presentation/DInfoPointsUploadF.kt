package ru.smartro.worknote.presentation

import ru.smartro.worknote.R

class DInfoPointsUploadF : AFragmentInfoDialog() {
    override fun onGetContentText(): String? {
        return  getString(R.string.warning_connection_lost)
    }

    override fun onGetNavId(): Int {
        return R.id.InfoPointsUploadFD
    }

}