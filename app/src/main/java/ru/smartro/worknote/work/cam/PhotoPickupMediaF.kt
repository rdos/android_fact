package ru.smartro.worknote.work.cam

import android.view.View
import ru.smartro.worknote.R
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoPickupMediaF : APhotoFragment() {
    private var mPlatformEntity: PlatformEntity? = null

    override fun onSaveFoto() {
//        TODO("Not yet implemented")
    }


    override fun onClickBtnCancel() {
        //тут нужно очистить    mPlatformEntity.volumePickup
//        acsbVolumePickup?.progress = prevVolumeValue?.toInt() ?: 0
//        vm.updateSelectionVolume(platform.platformId!!, prevVolumeValue)
//        tvVolumePickuptext(prevVolumeValue)
    }

    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + "pickupMedia"
    }

    override fun onBeforeUSE() {
        val platformId = getArgumentID()
        mPlatformEntity = viewModel.baseDat.getPlatformEntity(platformId)
        mPlatformEntity?.getPickupMediaSize()
    }

    override fun onAfterUSE() {
        navigateMain(R.id.PServeF, mPlatformEntity?.platformId)

//        vm.updateSelectionVolume(platform.platformId!!, newVolumeValue)
//        acsbVolumePickup?.progress = newVolumeValue?.toInt() ?: (prevVolumeValue?.toInt() ?: 0)
//        prevVolumeValue = newVolumeValue
//        tvVolumePickuptext(prevVolumeValue)
    }

    override fun onGetTextLabelFor() = getString(R.string.service_pickup_volume)

    override fun onGetIsVisibleBtnCancel() = true

    override fun onmThumbNailClick() {
//        TODO("Not yet implemented")
    }

    override fun onBtnAcceptPhoto_know1() {
//        TODO("Not yet implemented")
    }

}