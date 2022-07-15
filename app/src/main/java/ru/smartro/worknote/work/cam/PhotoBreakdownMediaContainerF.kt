package ru.smartro.worknote.work.cam

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoBreakdownMediaContainerF : APhotoFragment() {
    private var mContainerEntity: ContainerEntity? = null
    private val mPlatformId = getArgumentName()!!.toInt()
    override fun onSavePhoto() {
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

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
//        navigateMain(R.id.PServeF, mContainerEntity?.platformId)
        navigateMain(R.id.PServeF, mPlatformId)
    }

    override fun onGetTextLabelFor() = "Поломка контейнера"
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        TODO("Not yet implemented")
    }

    override fun onGetIsVisibleBtnCancel() = false


}