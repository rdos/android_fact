package ru.smartro.worknote.presentation

import io.realm.RealmList
import ru.smartro.worknote.LOG
import ru.smartro.worknote.R
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.ContainerMediaEntity
import ru.smartro.worknote.work.ImageEntity
import java.io.File

class PhotoBreakdownMediaContainerF : APhotoFragment() {
    private var mBreakDownReasonS: List<String>? = null
    private val mContainerId: Int
        get() = getArgumentID()
    private val mContainerMediaEntity: ContainerMediaEntity
        get() {
            return viewModel.getContainerMediaEntity(mContainerId)
        }
    override fun onGetTextForFailHint() = "Причина поломки контейнера"
    override fun onGetStringList(): List<String> {
        mBreakDownReasonS = viewModel.database.findAllBreakDownReasonS()
        if (mBreakDownReasonS == null || mBreakDownReasonS?.size == 0) {
            toast("Ошибка.todo:::")
            return emptyList()
        }
        return mBreakDownReasonS!!
    }
    override fun onGetMediaRealmList(): RealmList<ImageEntity> {
        return mContainerMediaEntity.breakdownMedia
    }

    override fun onGetIsVisibleComment(): Boolean = true

    override fun onGetDirName(): String {
        val containerId = getArgumentID().toString()
        val platformId = getArgumentName()
        return platformId + File.separator + containerId + File.separator + "breakdownMediaContainer"
    }

    override fun onBeforeUSE() {
        tvLabelFor(requireView())
    }

    override fun onSavePhoto() {
//        TODO("Not yet implemented")
        LOG.debug(":P:onSavePhoto")
    }

    var breakdownText: String? = null
    override fun onGotoNext(): Boolean {
        val result = true
        if (mAcactvFail?.text.isNullOrEmpty()) {
            toast("Выберите причину невывоза")
            return false
        }
        breakdownText = mAcactvFail?.text.toString()
        return result
    }

    override fun onAfterUSE(imageS: List<ImageEntity>) {
        val platformId = getArgumentName()?.toInt()!!
//        navigateClose(R.id.PServeF, mPlatformEntity?.platformId)
        //        val problemComment = problem_comment.text.toString()
        viewModel.addBreakdownMediaContainer(mContainerId, imageS)
        viewModel.updateContainerBreakDown(mContainerId, breakdownText!!, getCommentText())
        navigateNext(R.id.PServeF, platformId)
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