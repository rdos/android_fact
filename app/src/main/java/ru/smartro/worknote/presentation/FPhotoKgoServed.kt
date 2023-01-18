package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.App
import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.ImageInfoEntity
import ru.smartro.worknote.log.todo.PlatformEntity
import java.io.File

class FPhotoKgoServed : APhotoF() {

    companion object {
        const val NAV_ID = R.id.FPhotoKgoServed
    }

    private val mPlatformEntity: PlatformEntity
        get() =  viewModel.getPlatformEntity()
    override fun onGetTextLabelFor() = "КГО.забрал"
    override fun onGetMediaRealmList(): RealmList<ImageInfoEntity> {
        return mPlatformEntity.getServedKGOMedia()
    }

    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + App.Companion.PhotoTypeMapping.KGO_SERVED_MEDIA
    }


    override fun onBeforeUSE() {
       
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageInfoEntity>) {
        val servedKGOVolumeText = getArgumentName()!!
        viewModel.database.addKgoServed(viewModel.getPlatformId(), imageS)
        viewModel.updatePlatformKGO(servedKGOVolumeText, isServedKGO = true)
        navigateNext(FPServe.NAV_ID, viewModel.getPlatformId())
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
        
        navigateBack()
    }

}