package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.ImageInfoEntity
import ru.smartro.worknote.log.todo.PlatformEntity
import java.io.File

class FPhotoKgoRemaining : APhotoF() {

    companion object {
        const val NAV_ID = R.id.FPhotoKgoRemaining
    }

    private val mPlatformEntity: PlatformEntity
        get() =  viewModel.getPlatformEntity()
    override fun onGetTextLabelFor() = "КГО.заказать борт"
    override fun onGetMediaRealmList(): RealmList<ImageInfoEntity> {
        return mPlatformEntity.getRemainingKGOMedia()
    }

    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + "kgoRemaining"
    }

    override fun onBeforeUSE() {
       
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageInfoEntity>) {
        val remainingKGOVolumeText = getArgumentName()!!
        viewModel.database.addPlatformKgoRemaining(viewModel.getPlatformId(), imageS)
        viewModel.updatePlatformKGO(remainingKGOVolumeText, isServedKGO = false)
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