package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.ImageEntity
import ru.smartro.worknote.log.todo.PlatformMediaEntity
import java.io.File

class FPhotoKgoServed : APhotoF() {
    private val mPlatformMediaEntity: PlatformMediaEntity
        get() =  viewModel.getPlatformMediaEntity()
    override fun onGetTextLabelFor() = "КГО.забрал"
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        return mPlatformMediaEntity.kgoServedMedia
    }

    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + "kgoServed"
    }


    override fun onBeforeUSE() {
       
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        val servedKGOVolumeText = getArgumentName()!!
        viewModel.database.addKgoServed(viewModel.getPlatformId(), imageS)
        viewModel.updatePlatformKGO(servedKGOVolumeText, isServedKGO = true)
        navigateNext(R.id.PServeF, viewModel.getPlatformId())
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