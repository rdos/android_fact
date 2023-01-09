package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.*
import ru.smartro.worknote.log.todo.ConfigName
import ru.smartro.worknote.log.todo.ImageInfoEntity
import ru.smartro.worknote.log.todo.PlatformEntity
import ru.smartro.worknote.log.todo.PlatformMediaEntity
import java.io.File

class FPhotoBeforeMedia : APhotoF() {
    private val mPlatformMediaEntity: PlatformMediaEntity
        get() =  viewModel.getPlatformMediaEntity()
//    override fun onGetTextLabelFor() = "фото до обслуживания КП"
    override fun onGetMediaRealmList(): RealmList<ImageInfoEntity> {
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
    override fun onAfterUSE(imageS: List<ImageInfoEntity>) {
        viewModel.addBeforeMedia(imageS)
        val platformEntity = viewModel.getPlatformEntity()
        val platformServeMode = platformEntity.getServeMode()
        LOG.info("PLATFORM SERVE MODE ::: ${platformServeMode}")
        if (platformServeMode != null) {
//            // TODO: FYI: влад, "!!!"= значит точно знаю КАК PlatformEntity.Companion.ServeMode.PServeF
////            if mPLatformEntity.isServeModeFixPServeF
//            if (mPlatformEntity.serveModeFixCODENAME == PlatformEntity.Companion.ServeMode.PServeF) {
//                navigateNext(PServeF.NAV_ID, mPlatformEntity.platformId)
//                return
//            }
//                             //todo: линию не заметил)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
////            //            if mPLatformEntity.isModeFixPServeGroupByContainersF
//            if (mPlatformEntity.serveModeFixCODENAME == PlatformEntity.Companion.ServeMode.PServeGroupByContainersF) {
//                navigateNext(PServeByTypesF.NAV_ID, mPlatformEntity.platformId)
//                return
//            }
            if(platformServeMode == PlatformEntity.Companion.ServeMode.PServeF) {
                navigateNext(FPServe.NAV_ID, viewModel.getPlatformId())
                return
            } else {
                navigateNext(FPServeGroupByContainers.NAV_ID, viewModel.getPlatformId())
                return
            }
        }

        val configVal = viewModel.database.getConfigString(ConfigName.USER_WORK_SERVE_MODE_CODENAME)
        if (configVal == PlatformEntity.Companion.ServeMode.PServeF) {
            navigateNext(FPServe.NAV_ID, viewModel.getPlatformId())
            return
        }
        if (configVal == PlatformEntity.Companion.ServeMode.PServeGroupByContainersF) {
            navigateNext(FPServeGroupByContainers.NAV_ID, viewModel.getPlatformId())
            return
        }

        navigateNext(FPServe.NAV_ID, viewModel.getPlatformId())
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
        navigateBack(FPMap.NAV_ID)

    }
    companion object {
        const val NAV_ID = R.id.FPhotoBeforeMedia
    }
}