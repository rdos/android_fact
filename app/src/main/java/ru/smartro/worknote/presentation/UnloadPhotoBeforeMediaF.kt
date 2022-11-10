package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class UnloadPhotoBeforeMediaF : APhotoFragment() {
    private val mPlatformEntity: PlatformEntity
        get() =  viewModel.getPlatformEntity()
//    override fun onGetTextLabelFor() = "фото до обслуживания КП"
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        return mPlatformEntity.unloadEntity!!.beforeMedia
    }

    override fun onGetDirName(): String {
       return getArgumentID().toString() + File.separator + "beforeMediaUnload"
    }

    override fun onBeforeUSE() {

    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        viewModel.addBeforeMediaUnload(imageS)
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