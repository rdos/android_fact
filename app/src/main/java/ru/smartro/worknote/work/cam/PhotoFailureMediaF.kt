package ru.smartro.worknote.work.cam

import ru.smartro.worknote.R
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoFailureMediaF : APhotoFragment() {
    private var mPlatformEntity: PlatformEntity? = null

    override fun onSaveFoto() {
//        TODO("Not yet implemented")
    }

    override fun onClickBtnCancel() {
        //тут нужно очистить    mPlatformEntity.volumePickup
    }

    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + "failureMedia"
    }

    override fun onBeforeUSE() {
        val platformId = getArgumentID()
        mPlatformEntity = viewModel.baseDat.getPlatformEntity(platformId)
        mPlatformEntity?.failureMedia?.size
    }

    override fun onAfterUSE() {
//        navigateClose(R.id.PServeF, mPlatformEntity?.platformId)
        navigateClose()
    }

    override fun onGetTextLabelFor() = getString(R.string.problem_on_point)

    override fun onGetIsVisibleBtnCancel() = false

    override fun onmThumbNailClick() {
//        TODO("Not yet implemented")
    }

    override fun onBtnAcceptPhoto_know1() {
//        TODO("Not yet implemented")
    }

}