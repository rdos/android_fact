package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.log.work.ImageEntity
import ru.smartro.worknote.log.work.PlatformEntity
import java.io.File

class FModeUnloadPhotoAfterMedia : APhotoF() {
    private val mPlatformEntity: PlatformEntity
        get() =  viewModel.getPlatformEntity()
//    override fun onGetTextLabelFor() = "фото до обслуживания КП"
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        return mPlatformEntity.unloadEntity!!.afterMedia
    }

    override fun onGetDirName(): String {
       return getArgumentID().toString() + File.separator + "afterMediaUnload"
    }

    override fun onBeforeUSE() {

    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        viewModel.addAfterMediaUnload(imageS)
        navigateNext(R.id.MapPlatformsF)
    }

    override fun onSavePhoto() {
//        TODO("Not yet implemented")
//        id: String = UUID.randomUUID().toString(),
    }

    override fun onGetIsVisibleBtnCancel() = false

    override fun onClickBtnCancel() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        super.dropOutputD()
        navigateNext(R.id.MapPlatformsF)

    }
    companion object {
    }
}