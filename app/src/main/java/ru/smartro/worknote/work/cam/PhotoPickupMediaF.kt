package ru.smartro.worknote.work.cam

import android.view.View
import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoPickupMediaF : APhotoFragment() {
    private var mPlatformEntity: PlatformEntity? = null
    override fun onGetTextLabelFor() = "Крупногабаритные отходы.забрал"
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        if (mPlatformEntity == null) {
            toast("Ошибка.todo:::")
            return RealmList<ImageEntity>()
        }
        return mPlatformEntity!!.kgoServed!!.media
    }

    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + "kgoServed"
    }


    override fun onBeforeUSE() {
        val platformId = getArgumentID()
        mPlatformEntity = viewModel.getPlatformEntity(platformId)
//        viewModel.mPlatformEntity.observe(viewLifecycleOwner){
//            mPlatformEntity = it
//        }
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        viewModel.baseDat.addKgoServed(mPlatformEntity?.platformId!!, imageS)
        navigateMain(R.id.PServeF, mPlatformEntity?.platformId)
//        findNavController().navigatorProvider.navigators.forEach { t, u ->  println("TAGSS${t}")}
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
        if (getMediaCount() <= 0) {
            navigateClose()
        } else {
            navigateMain(R.id.PServeF, mPlatformEntity?.platformId)
        }
    }

    companion object {
    }
}

    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + "pickupMedia"
    }

    override fun onBeforeUSE() {
        val platformId = getArgumentID()
        mPlatformEntity = viewModel.baseDat.getPlatformEntity(platformId)
        mPlatformEntity?.getPickupMediaSize()
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        navigateMain(R.id.PServeF, mPlatformEntity?.platformId)

//        vm.updateSelectionVolume(platform.platformId!!, newVolumeValue)
//        acsbVolumePickup?.progress = newVolumeValue?.toInt() ?: (prevVolumeValue?.toInt() ?: 0)
//        prevVolumeValue = newVolumeValue
//        tvVolumePickuptext(prevVolumeValue)
    }

    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        TODO("Not yet implemented")
    }

    override fun onGetTextLabelFor() = getString(R.string.service_pickup_volume)

    override fun onGetIsVisibleBtnCancel() = true

}