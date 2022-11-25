package ru.smartro.worknote.presentation

import ru.smartro.worknote.R

class DInfoPointsUploadF : AFragmentInfoDialog() {
    override fun onGetContentText(): String? {
        return  getString(R.string.different_unload_points)
    }

    override fun onGetNavId(): Int {
        return R.id.DInfoPointsUploadF
    }

}