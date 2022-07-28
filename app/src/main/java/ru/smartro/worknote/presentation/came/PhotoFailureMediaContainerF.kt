package ru.smartro.worknote.presentation.came

import io.realm.RealmList
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ContainerEntity
import ru.smartro.worknote.work.ImageEntity
import java.io.File

class PhotoFailureMediaContainerF : APhotoFragment() {
    private var mFailReasonS: List<String>? = null
    private var mContainerEntity: ContainerEntity? = null
    override fun onGetTextForFailHint() = "Причина невывоза контейнера"
    override fun onGetStringList(): List<String>? {
        mFailReasonS = viewModel.getFailReasonS()
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
        mContainerEntity = viewModel.baseDat.getContainerEntity(containerId)
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
//        navigateClose(R.id.PServeF, mPlatformEntity?.platformId)
        //        val problemComment = problem_comment.text.toString()
        viewModel.baseDat.addFailureMediaContainer(platformId, mContainerEntity?.containerId!!, imageS)
        viewModel.baseDat.setStateFailureForContainer(platformId, mContainerEntity?.containerId!!, failText!!, getCommentText())
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