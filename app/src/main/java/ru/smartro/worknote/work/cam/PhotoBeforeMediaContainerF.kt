package ru.smartro.worknote.work.cam

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import io.realm.RealmList
import ru.smartro.worknote.App
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoBeforeMediaContainerF : APhotoFragment() {

    private var mPlatformEntity: PlatformEntity? = null

    override fun onSavePhoto() {
//        TODO("Not yet implemented")
    }


    override fun onGetDirName(): String {
       return getArgumentID().toString() + File.separator + "beforeMedia"
    }

    override fun onBeforeUSE() {
//        TODO("Not yet implemented")
        val platformId = getArgumentID()
        mPlatformEntity = viewModel.baseDat.getPlatformEntity(platformId)

    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        navigateMain(R.id.PServeF, mPlatformEntity?.platformId)
//        findNavController().navigatorProvider.navigators.forEach { t, u ->  println("TAGSS${t}")}
    }

    override fun onGetTextLabelFor() = "Фотографии до обслуживания контейнера"
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        TODO("Not yet implemented")
    }

    override fun onClickBtnCancel() {
        TODO("Not yet implemented")
    }

    override fun onGetIsVisibleBtnCancel() = false

    companion object {
    }
}