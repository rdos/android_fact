package ru.smartro.worknote.work.cam

import ru.smartro.worknote.R
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoBreakdownMediaContainerF : APhotoFragment() {
    private var mContainerEntity: ContainerEntity? = null
    private val mPlatformId = getArgumentName()!!.toInt()
    override fun onSaveFoto() {
//        TODO("Not yet implemented")
    }

    override fun onClickBtnCancel() {
        //тут нужно очистить    mPlatformEntity.volumePickup
    }

    override fun onGetDirName(): String {
        return getArgumentName() + File.separator + getArgumentID().toString() + File.separator + "breakdownMedia"
    }

    override fun onBeforeUSE() {
        val containerId = getArgumentID()

        mContainerEntity  = viewModel.baseDat.getContainerEntity(containerId)
        mContainerEntity?.breakdownMedia?.size
    }

    override fun onAfterUSE() {
//        navigateMain(R.id.PServeF, mContainerEntity?.platformId)
        navigateMain(R.id.PServeF, mPlatformId)
    }

    override fun onGetTextLabelFor() = "Поломка контейнера"

    override fun onGetIsVisibleBtnCancel() = false

    override fun onmThumbNailClick() {
//        TODO("Not yet implemented")
    }

    override fun onBtnAcceptPhoto_know1() {
//        TODO("Not yet implemented")
    }

}