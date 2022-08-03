package ru.smartro.worknote.presentation.came

import ru.smartro.worknote.work.PlatformEntity

class FAStPhotoFailureMediaF : PhotoFailureMediaF() {

    override fun onBeforeUSE() {
        val platformId = getArgumentID()
        mPlatformEntity = viewModel.getPlatformEntity(platformId)
        tvLabelFor(requireView())
    }

    override fun onBackPressed() {
        super.dropOutputD()
        navigateBack()
    }

}