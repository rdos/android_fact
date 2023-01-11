package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.ImageInfoEntity
import ru.smartro.worknote.log.todo.PlatformEntity
import java.io.File

class FPhotoBeforeMediaContainer : APhotoF() {

    companion object {
        const val NAV_ID = R.id.FPhotoBeforeMediaContainer
    }

    private val mPlatformEntity: PlatformEntity
        get() = viewModel.getPlatformEntity()

    override fun onGetTextLabelFor() = "контейнер: фото до"
    override fun onGetMediaRealmList(): RealmList<ImageInfoEntity> {
        return mPlatformEntity.beforeMedia
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

    override fun onAfterUSE(imageS: List<ImageInfoEntity>) {
        viewModel.addBeforeMediaComntainerByTypes(imageS)
        navigateNext(FPServe.NAV_ID, viewModel.getPlatformId())
    }

    override fun onGetIsVisibleBtnCancel() = false
    override fun onClickBtnCancel() {}

    override fun onSavePhoto() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        
        navigateBack()
    }
}