package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.ImageInfoEntity
import ru.smartro.worknote.log.todo.PlatformEntity
import java.io.File

class DFModeUnloadPhotoBeforeMedia : APhotoF() {
    private val mPlatformEntity: PlatformEntity
        get() =  viewModel.getPlatformEntity()
//    override fun onGetTextLabelFor() = "фото до обслуживания КП"
    override fun onGetMediaRealmList(): RealmList<ImageInfoEntity> {
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

    override fun onAfterUSE(imageS: List<ImageInfoEntity>) {
        viewModel.addBeforeMediaUnload(imageS)
        navigateNext(FPMap.NAV_ID)
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
        
        navigateNext(FPMap.NAV_ID)

    }
    companion object {
        const val NAV_ID = R.id.DFModeUnloadPhotoBeforeMedia
    }
}