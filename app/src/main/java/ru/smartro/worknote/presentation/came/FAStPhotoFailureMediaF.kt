package ru.smartro.worknote.presentation.came

class FAStPhotoFailureMediaF : PhotoFailureMediaF() {

    override fun onBeforeUSE() {
        val platformId = getArgumentID()
        mPlatformEntity = vm.getPlatformEntity()
        tvLabelFor(requireView())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        super.dropOutputD()
        navigateBack()
    }

}