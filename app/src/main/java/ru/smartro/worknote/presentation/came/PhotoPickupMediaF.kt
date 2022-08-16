package ru.smartro.worknote.presentation.came

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoPickupMediaF : APhotoFragment() {
    private var mPlatformEntity: PlatformEntity? = null
    override fun onGetTextLabelFor() = "фото подбора"
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        if (mPlatformEntity == null) {
            toast("Ошибка.todo:::")
            return RealmList<ImageEntity>()
        }
        return mPlatformEntity!!.pickupMedia
    }

    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + "pickupMedia"
    }


    override fun onBeforeUSE() {
        if(vm.mPlatformEntityLiveData.value == null)
            throw Exception("${this::class.java.simpleName}//onBeforeUse//viewModel.mPlatformEntity.value == null")
        mPlatformEntity = vm.mPlatformEntityLiveData.value
//        mMaxPhotoCount = mPlatformEntity!!.getPickupMediaSize()
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        val newVolume = getArgumentName()!!.toDouble()
        vm.database.addPlatformPickupMedia(mPlatformEntity?.platformId!!, imageS)
        vm.updateVolumePickup(mPlatformEntity?.platformId!!, newVolume)
        navigateMain(R.id.PServeF, mPlatformEntity?.platformId)
    }

    override fun onSavePhoto() {
//        TODO("Not yet implemented")
//        id: String = UUID.randomUUID().toString(),
    }

    override fun onGetIsVisibleBtnCancel() = true

    override fun onClickBtnCancel() {
        val newVolume = getArgumentName()!!.toDouble()
        vm.updateVolumePickup(mPlatformEntity?.platformId!!, newVolume)
        navigateMain(R.id.PServeF, mPlatformEntity?.platformId)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        super.dropOutputD()
        navigateBack()
    }

    companion object {
    }
}