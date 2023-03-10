package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.App
import ru.smartro.worknote.Dnull
import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.ImageInfoEntity
import ru.smartro.worknote.log.todo.PlatformEntity
import java.io.File

class FPhotoPickupMedia : APhotoF() {

    companion object {
        const val NAV_ID = R.id.FPhotoPickupMedia
    }

    private var newVolume: Double = Dnull
    private val mPlatformEntity: PlatformEntity
        get() =  viewModel.getPlatformEntity()
    override fun onGetTextLabelFor() = "фото подбора"
    override fun onGetMediaRealmList(): RealmList<ImageInfoEntity> {
        return mPlatformEntity.pickupMedia
    }

    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + App.Companion.PhotoTypeMapping.PICKUP_MEDIA
    }

    override fun onBeforeUSE() {
        newVolume = getArgumentName()!!.toDouble()
//        mMaxPhotoCount = mPlatformEntity!!.getPickupMediaSize()
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageInfoEntity>) {

        viewModel.database.addPlatformPickupMedia(viewModel.getPlatformId(), imageS)
        viewModel.updateVolumePickup(newVolume)
        navigateNext(FPServe.NAV_ID, viewModel.getPlatformId())
    }

    override fun onSavePhoto() {
//        TODO("Not yet implemented")
//        id: String = UUID.randomUUID().toString(),
    }

    override fun onGetIsVisibleBtnCancel() = true

    override fun onClickBtnCancel() {
        viewModel.updateVolumePickup(newVolume)
        navigateNext(FPServe.NAV_ID, viewModel.getPlatformId())
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        navigateBack()
    }
}