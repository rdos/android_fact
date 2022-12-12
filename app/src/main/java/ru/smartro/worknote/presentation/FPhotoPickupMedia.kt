package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.Dnull
import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.ImageEntity
import ru.smartro.worknote.log.todo.PlatformMediaEntity
import java.io.File

class FPhotoPickupMedia : APhotoF() {
    private var newVolume: Double = Dnull
    private val mPlatformMediaEntity: PlatformMediaEntity
        get() =  viewModel.getPlatformMediaEntity()
    override fun onGetTextLabelFor() = "фото подбора"
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        return mPlatformMediaEntity.pickupMedia
    }

    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + "pickupMedia"
    }

    override fun onBeforeUSE() {
        newVolume = getArgumentName()!!.toDouble()
//        mMaxPhotoCount = mPlatformEntity!!.getPickupMediaSize()
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {

        viewModel.database.addPlatformPickupMedia(viewModel.getPlatformId(), imageS)
        viewModel.updateVolumePickup(newVolume)
        navigateNext(R.id.PServeF, viewModel.getPlatformId())
    }

    override fun onSavePhoto() {
//        TODO("Not yet implemented")
//        id: String = UUID.randomUUID().toString(),
    }

    override fun onGetIsVisibleBtnCancel() = true

    override fun onClickBtnCancel() {
        viewModel.updateVolumePickup(newVolume)
        navigateNext(R.id.PServeF, viewModel.getPlatformId())
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        super.dropOutputD()
        navigateBack()
    }
}