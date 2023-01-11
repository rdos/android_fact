package ru.smartro.worknote.presentation


import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.log.todo.ImageInfoEntity
import ru.smartro.worknote.log.todo.PlatformEntity
import java.io.File

class FPhotoBeforeMediaContainerByTypes : APhotoF() {

    companion object {
        const val NAV_ID = R.id.FPhotoBeforeMediaContainerByTypes
    }

    private val mPlatformEntity: PlatformEntity
        get() =  viewModel.getPlatformEntity()
    override fun onGetTextLabelFor() = "фото контейнера до"
    override fun onGetMediaRealmList(): RealmList<ImageInfoEntity> {
        return mPlatformEntity.beforeMedia
    }

    override fun onGetDirName(): String {
        return getArgumentID().toString() + File.separator + "beforeMediaContainer"
    }

    override fun onBeforeUSE() {
        mMaxPhotoCount = Int.MAX_VALUE
    }

    override fun onGotoNext(): Boolean {
        return true
    }

    override fun onAfterUSE(imageS: List<ImageInfoEntity>) {
        val typeId = getArgumentID()
        val client = getArgumentName()
        val platformId = viewModel.getPlatformId()
        val groupByContainerTypeClientEntity = viewModel.database.loadContainerGROUPClientTypeEntity(platformId, typeId, client)
        viewModel.incGroupByContainerTypeClientS(groupByContainerTypeClientEntity)
        viewModel.addBeforeMediaComntainerByTypes(imageS)
        navigateNext(FPServeGroupByContainers.NAV_ID, platformId)
    }

    override fun onSavePhoto() {

    }

    override fun onGetIsVisibleBtnCancel(): Boolean = false

    override fun onClickBtnCancel() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        
        navigateBack()
    }
}