package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.presentation.work.ImageEntity
import ru.smartro.worknote.presentation.work.PlatformMediaEntity
import java.io.File

class PhotoBeforeMediaContainerF : APhotoFragment() {

    private val mPlatformMediaEntity: PlatformMediaEntity
        get() = viewModel.getPlatformMediaEntity()

    override fun onGetTextLabelFor() = "контейнер: фото до"
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        return mPlatformMediaEntity.beforeMedia
    }

    override fun onGetDirName(): String {
       return getArgumentID().toString() + File.separator + "beforeMediaContainer"
    }

    override fun onBeforeUSE() {
        mMaxPhotoCount = Int.MAX_VALUE
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        viewModel.addBeforeMediaComntainerByTypes(imageS)
        navigateNext(R.id.PServeF, viewModel.getPlatformId())
    }

    override fun onGetIsVisibleBtnCancel() = false
    override fun onClickBtnCancel() {}

    override fun onSavePhoto() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        super.dropOutputD()
        navigateBack()
    }
}