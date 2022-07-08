package ru.smartro.worknote.work.cam

import android.view.View
import ru.smartro.worknote.R
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoKgoServedF : APhotoFragment() {
    private var mPlatformEntity: PlatformEntity? = null

    override fun onSaveFoto() {
//        TODO("Not yet implemented")
    }



    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + "kgoServed"
    }

    override fun onBeforeUSE() {
        val platformId = getArgumentID()
        mPlatformEntity = viewModel.baseDat.getPlatformEntity(platformId)
        mPlatformEntity?.getServedKGOMediaSize()
    }

    override fun onAfterUSE() {
        navigateMain(R.id.PServeF, mPlatformEntity?.platformId)
    }

    override fun onClickBtnCancel() {
        TODO("Not yet implemented")
    }

    override fun onGetTextLabelFor() = "Крупногабаритные отходы.забрал"

    override fun onGetIsVisibleBtnCancel() = false

    override fun onmThumbNailClick() {
//        TODO("Not yet implemented")
    }

    override fun onBtnAcceptPhoto_know1() {
//        TODO("Not yet implemented")
    }

}