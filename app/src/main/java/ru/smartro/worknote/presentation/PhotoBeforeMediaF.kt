package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.*
import ru.smartro.worknote.work.ConfigName
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import ru.smartro.worknote.work.PlatformMediaEntity
import java.io.File

class PhotoBeforeMediaF : APhotoFragment() {
    private val mPlatformMediaEntity: PlatformMediaEntity
        get() =  viewModel.getPlatformMediaEntity()
//    override fun onGetTextLabelFor() = "фото до обслуживания КП"
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        return mPlatformMediaEntity.beforeMedia
    }

    override fun onGetDirName(): String {
       return getArgumentID().toString() + File.separator + "beforeMedia"
    }

    override fun onBeforeUSE() {

    }

    override fun onGotoNext(): Boolean {
        return true
    }

    //TODO: r_dos!!!
    override fun onAfterUSE(imageS: List<ImageEntity>) {
        viewModel.addBeforeMedia(imageS)
        val platformEntity = viewModel.getPlatformEntity()
        val platformServeMode = platformEntity.getServeMode()
        LOG.info("PLATFORM SERVE MODE ::: ${platformServeMode}")
        if (platformServeMode != null) {
//            // TODO: FYI: влад, "!!!"= значит точно знаю КАК PlatformEntity.Companion.ServeMode.PServeF
////            if mPLatformEntity.isServeModeFixPServeF
//            if (mPlatformEntity.serveModeFixCODENAME == PlatformEntity.Companion.ServeMode.PServeF) {
//                navigateNext(R.id.PServeF, mPlatformEntity.platformId)
//                return
//            }
//                             //todo: линию не заметил)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
////            //            if mPLatformEntity.isModeFixPServeGroupByContainersF
//            if (mPlatformEntity.serveModeFixCODENAME == PlatformEntity.Companion.ServeMode.PServeGroupByContainersF) {
//                navigateNext(R.id.PServeByTypesF, mPlatformEntity.platformId)
//                return
//            }
            if(platformServeMode == PlatformEntity.Companion.ServeMode.PServeF) {
                navigateNext(R.id.PServeF, viewModel.getPlatformId())
                return
            } else {
                navigateNext(R.id.PServeGroupByContainersF, viewModel.getPlatformId())
                return
            }
        }

        val configVal = viewModel.database.getConfigString(ConfigName.USER_WORK_SERVE_MODE_CODENAME)
        if (configVal == PlatformEntity.Companion.ServeMode.PServeF) {
            navigateNext(R.id.PServeF, viewModel.getPlatformId())
            return
        }
        if (configVal == PlatformEntity.Companion.ServeMode.PServeGroupByContainersF) {
            navigateNext(R.id.PServeGroupByContainersF, viewModel.getPlatformId())
            return
        }

        navigateNext(R.id.PServeF, viewModel.getPlatformId())
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
        navigateBack(R.id.MapPlatformsF)

    }
    companion object {
    }
}