package ru.smartro.worknote.presentation.came

import io.realm.RealmList
import ru.smartro.worknote.*
import ru.smartro.worknote.work.ConfigName
import ru.smartro.worknote.work.ImageEntity
import ru.smartro.worknote.work.PlatformEntity
import java.io.File

class PhotoBeforeMediaF : APhotoFragment() {
        private val mPlatformEntity: PlatformEntity
        get() =  vm.getPlatformEntity()
//    override fun onGetTextLabelFor() = "фото до обслуживания КП"
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        if (mPlatformEntity == null) {
            toast("Ошибка.todo:::")
            return RealmList<ImageEntity>()
        }
        return mPlatformEntity!!.beforeMedia
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
        vm.database.addBeforeMedia(mPlatformEntity.platformId, imageS)

        val platformServeMode = mPlatformEntity.getServeMode()
        LOG.info("PLATFORM SERVE MODE ::: ${platformServeMode}")
        if (platformServeMode != null) {
//            // TODO: FYI: влад, "!!!"= значит точно знаю КАК PlatformEntity.Companion.ServeMode.PServeF
////            if mPLatformEntity.isServeModeFixPServeF
//            if (mPlatformEntity.serveModeFixCODENAME == PlatformEntity.Companion.ServeMode.PServeF) {
//                navigateMain(R.id.PServeF, mPlatformEntity.platformId)
//                return
//            }
//                             //todo: линию незаметил)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
////            //            if mPLatformEntity.isModeFixPServeGroupByContainersF
//            if (mPlatformEntity.serveModeFixCODENAME == PlatformEntity.Companion.ServeMode.PServeGroupByContainersF) {
//                navigateMain(R.id.PServeByTypesF, mPlatformEntity.platformId)
//                return
//            }
            if(platformServeMode == PlatformEntity.Companion.ServeMode.PServeF) {
                navigateMain(R.id.PServeF, mPlatformEntity.platformId)
                return
            } else {
                navigateMain(R.id.PServeGroupByContainersF, mPlatformEntity.platformId)
                return
            }
        }

        // TODO: !!!
        val configEntity = vm.database.loadConfig(ConfigName.USER_WORK_SERVE_MODE_CODENAME)

        if (configEntity.value == PlatformEntity.Companion.ServeMode.PServeF) {
            navigateMain(R.id.PServeF, mPlatformEntity.platformId)
            return
        }
        if (configEntity.value == PlatformEntity.Companion.ServeMode.PServeGroupByContainersF) {
            navigateMain(R.id.PServeGroupByContainersF, mPlatformEntity.platformId)
            return
        }

        navigateMain(R.id.PServeF, mPlatformEntity.platformId)
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