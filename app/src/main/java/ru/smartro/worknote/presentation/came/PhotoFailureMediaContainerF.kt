package ru.smartro.worknote.presentation.came

import io.realm.RealmList
import ru.smartro.worknote.LoG
import ru.smartro.worknote.R
import ru.smartro.worknote.log
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.ImageEntity
import java.io.File

class PhotoFailureMediaContainerF : APhotoFragment() {
    private var mFailReasonS: List<String>? = null
    private var mContainerEntity: ContainerEntity? = null
    override fun onGetTextForFailHint() = "Причина невывоза контейнера"
    override fun onGetStringList(): List<String>? {
        mFailReasonS = vm.getFailReasonS()
        if (mFailReasonS == null) {
            toast("Ошибка.todo:::")
            return emptyList()
        }
        return mFailReasonS
    }

    override fun onGetIsVisibleComment(): Boolean = true

    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        if (mContainerEntity == null) {
            toast("Ошибка.todo:::")
            return RealmList<ImageEntity>()
        }
        return mContainerEntity!!.failureMedia
    }

    override fun onGetDirName(): String {
        val containerId = getArgumentID().toString()
        val platformId = getArgumentName()
        return platformId + File.separator + containerId + File.separator + "failureMediaContainer"
    }

    override fun onBeforeUSE() {
        val containerId = getArgumentID()
        mContainerEntity = vm.database.getContainerEntity(containerId)
        tvLabelFor(requireView())
//        viewModel.mPlatformEntity.observe(viewLifecycleOwner){
//            mPlatformEntity = it
//        }
    }

    override fun onSavePhoto() {
//        TODO("Not yet implemented")
        log(":P:onSavePhoto")
    }

    var failText: String? = null
    override fun onGotoNext(): Boolean {
        val result = true
        if (mAcactvFail?.text.isNullOrEmpty()) {
            toast("Выберите причину невывоза")
            return false
        }
        failText = mAcactvFail?.text.toString()
        return result
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        val platformId = getArgumentName()?.toInt()!!
        val containerId = mContainerEntity?.containerId!!
        vm.database.addFailureMediaContainer(platformId, containerId, imageS)
        vm.updateContainerFailure(platformId, containerId, failText!!, getCommentText())
        navigateMain(R.id.PServeF, platformId)
    }

    override fun onGetIsVisibleBtnCancel() = false

    override fun onClickBtnCancel() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        super.dropOutputD()
        navigateBack()
    }

}