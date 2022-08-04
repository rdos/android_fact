package ru.smartro.worknote.presentation.came

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoKgoServedF : APhotoFragment() {
    private var mPlatformEntity: PlatformEntity? = null
    override fun onGetTextLabelFor() = "КГО.забрал"
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
        if(viewModel.mPlatformEntity.value == null)
            throw Exception("${this::class.java.simpleName}//onBeforeUse//viewModel.mPlatformEntity.value == null")
        mPlatformEntity = viewModel.mPlatformEntity.value
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        val servedKGOVolumeText = getArgumentName()!!
        viewModel.baseDat.addKgoServed(mPlatformEntity?.platformId!!, imageS)
        viewModel.updatePlatformKGO(mPlatformEntity?.platformId!!, servedKGOVolumeText, isServedKGO = true)
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
        super.dropOutputD()
        navigateBack()
    }

    companion object {
    }
}